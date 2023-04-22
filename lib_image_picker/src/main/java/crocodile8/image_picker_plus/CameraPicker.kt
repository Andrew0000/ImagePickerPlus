package crocodile8.image_picker_plus

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import crocodile8.image_picker_plus.utils.Logger
import java.io.File
import java.io.IOException

//https://developer.android.com/training/camera-deprecated/photobasics#TaskPath

class CameraPicker(
    private val context: Context,
) {

    private var tmpFile: File? = null

    fun launch(activity: Activity, request: PickRequest) {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Logger.e("Don't have camera")
            return
        }
        val file = createEmptyFile()
        Logger.d("CameraPicker launch file: $file")
        tmpFile = file
        if (file != null && file.exists()) {
            val intent = createIntent(file)
            if (intent.resolveActivity(context.packageManager) != null) {
                activity.startActivityForResult(intent, REQUEST_CODE)
            } else {
                Logger.e("CameraPicker intent resolveActivity error")
                //TODO show error
            }
        } else {
            Logger.e("Error, file: $file")
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Uri? {
        val uri = data?.data
        val tmpUri = Uri.fromFile(tmpFile)
        Logger.i("uri: $uri / $tmpUri")
        return tmpUri
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

    companion object {

        const val REQUEST_CODE = 803
    }
}
