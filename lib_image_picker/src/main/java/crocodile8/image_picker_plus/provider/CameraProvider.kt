package crocodile8.image_picker_plus.provider

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.FileProvider
import crocodile8.image_picker_plus.PickRequest
import crocodile8.image_picker_plus.R
import crocodile8.image_picker_plus.utils.Logger
import crocodile8.image_picker_plus.utils.Utils.createEmptyLocalUniqueFile
import java.io.File

//https://developer.android.com/training/camera-deprecated/photobasics#TaskPath

internal class CameraProvider(
    activity: ComponentActivity,
    private val onResult: (Uri?) -> Unit,
) {

    private val context = activity.applicationContext
    private val packageManager = context.packageManager

    private var tmpFile: File? = null

    private val launcher = activity.registerForActivityResult(StartActivityForResult()) {
        val uri = it.data?.data
        val tmpUri = Uri.fromFile(tmpFile)
        Logger.i("CameraPicker uri: $uri / $tmpUri")
        if (it.resultCode == Activity.RESULT_OK && tmpUri != null) {
            onResult(tmpUri)
        } else {
            Logger.e("CameraPicker result error: ${it.resultCode}, uri: $uri")
            onResult(null)
        }
    }

    fun launch(request: PickRequest) {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Logger.e("Don't have camera")
            onResult(null)
            return
        }
        val file = createEmptyLocalUniqueFile(context)
        Logger.d("CameraPicker launch file: $file")
        tmpFile = file
        if (file != null && file.exists()) {
            val intent = createIntent(file)
            if (intent.resolveActivity(packageManager) != null) {
                launcher.launch(intent)
            } else {
                Logger.e("CameraPicker intent resolveActivity error")
                onResult(null)
            }
        } else {
            Logger.e("Error, file: $file")
            onResult(null)
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        Logger.i("CameraPicker onSaveInstanceState, tmpFile: $tmpFile")
        tmpFile?.let {
            outState.putString(SAVED_FILE, it.path)
        }
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Logger.i("CameraPicker onRestoreInstanceState: $savedInstanceState")
        savedInstanceState.getString(SAVED_FILE)?.let {
            tmpFile = File(it)
        }
    }

    private fun createIntent(file: File): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val authority = context.packageName + context.getString(R.string.file_provider_name)
        val photoURI = FileProvider.getUriForFile(context, authority, file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        return intent
    }

    companion object {
        private const val SAVED_FILE = "camera_picker_saved_file"
    }
}
