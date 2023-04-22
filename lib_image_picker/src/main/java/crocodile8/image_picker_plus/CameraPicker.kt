package crocodile8.image_picker_plus

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.FileProvider
import crocodile8.image_picker_plus.utils.Logger
import java.io.File
import java.io.IOException

//https://developer.android.com/training/camera-deprecated/photobasics#TaskPath

class CameraPicker(
    activity: ComponentActivity,
    private val onResult: (Uri) -> Unit,
) {

    private val context = activity.applicationContext
    private val packageManager = context.packageManager

    private var tmpFile: File? = null

    private val launcher = activity.registerForActivityResult(StartActivityForResult()) {
        val uri = it.data?.data
        val tmpUri = Uri.fromFile(tmpFile)
        Logger.i("uri: $uri / $tmpUri")
        if (it.resultCode == Activity.RESULT_OK && tmpUri != null) {
            onResult(tmpUri)
        } else {
            Logger.e("CameraPicker result error: ${it.resultCode}, uri: $uri")
            //TODO show error
        }
    }

    fun launch(request: PickRequest) {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Logger.e("Don't have camera")
            return
        }
        val file = createEmptyFile()
        Logger.d("CameraPicker launch file: $file")
        tmpFile = file
        if (file != null && file.exists()) {
            val intent = createIntent(file)
            if (intent.resolveActivity(packageManager) != null) {
                launcher.launch(intent)
            } else {
                Logger.e("CameraPicker intent resolveActivity error")
                //TODO show error
            }
        } else {
            Logger.e("Error, file: $file")
        }
    }

    private fun createIntent(file: File): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val authority = context.packageName + context.getString(R.string.file_provider_name)
        val photoURI = FileProvider.getUriForFile(context, authority, file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        return intent
    }

    private fun createEmptyFile(): File? =
        try {
            val fileDir = File(context.cacheDir.absolutePath + "/image_picker_plus_cache")
            val ext = ".jpg"
            val fileName = "tmp_${System.currentTimeMillis()}"
            val imageFileName = "$fileName$ext"
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            val file = File(fileDir, imageFileName)
            file.createNewFile()
            file
        } catch (e: IOException) {
            Logger.e("", e)
            null
        }
}
