package crocodile8.image_picker_plus.provider

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.FileProvider
import crocodile8.image_picker_plus.PickRequest
import crocodile8.image_picker_plus.R
import crocodile8.image_picker_plus.utils.Logger
import crocodile8.image_picker_plus.utils.Utils.createEmptyLocalUniqueFile
import crocodile8.image_picker_plus.utils.isCameraPermissionDeclared
import crocodile8.image_picker_plus.utils.isCameraPermissionGranted
import crocodile8.image_picker_plus.utils.launchAppSettings
import crocodile8.image_picker_plus.utils.shouldRequestRationale
import java.io.File

//https://developer.android.com/training/camera-deprecated/photobasics#TaskPath

internal class CameraProvider(
    activity: ComponentActivity,
    request: PickRequest,
    onResult: (Uri?, Throwable?) -> Unit,
) : StartActivityForResultProvider(activity, request, onResult) {

    private var tmpFile: File? = null

    private var waitingSettings = false

    private val permissionLauncher = activity.registerForActivityResult(RequestPermission()) {
        if (needPermission()) {
            if (activity.shouldRequestRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(context, R.string.ipp_camera_permission_allow, Toast.LENGTH_LONG).show()
                onError()
            } else {
                // Probably "never ask again"
                Toast.makeText(context, R.string.ipp_camera_permission_go_to_settings, Toast.LENGTH_LONG).show()
                waitingSettings = true
                activity.launchAppSettings()
            }
        } else {
            launch()
        }
    }

    override fun onResult(resultCode: Int, intent: Intent?) {
        val uri = intent?.data
        val tmpUri = Uri.fromFile(tmpFile)
        Logger.i("CameraProvider uri: $uri / $tmpUri")
        if (resultCode == Activity.RESULT_OK && tmpUri != null) {
            onSuccess(tmpUri)
        } else {
            Logger.e("CameraProvider result error: $resultCode, uri: $uri")
            onError()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            // Don't react on activity recreation
            return
        }
        if (!haveCamera()) {
            Logger.e("Don't have camera")
            onError()
            return
        }
        if (needPermission()) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }
        launch()
    }

    override fun onResume() {
        if (waitingSettings) {
            if (needPermission()) {
                onError()
            } else {
                waitingSettings = false
                launch()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Logger.i("CameraProvider onSaveInstanceState, tmpFile: $tmpFile")
        tmpFile?.let { outState.putString(SAVED_FILE, it.path) }
        outState.putBoolean(WAIT_SETTINGS, waitingSettings)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Logger.i("CameraProvider onRestoreInstanceState: $savedInstanceState")
        savedInstanceState.getString(SAVED_FILE)?.let { tmpFile = File(it) }
        waitingSettings = savedInstanceState.getBoolean(WAIT_SETTINGS)
    }

    private fun needPermission() =
        context.isCameraPermissionDeclared() && !context.isCameraPermissionGranted()

    private fun haveCamera() =
        packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    private fun launch() {
        val file = createEmptyLocalUniqueFile(context)
        Logger.d("CameraProvider launch file: $file")
        tmpFile = file
        if (file != null && file.exists()) {
            val intent = createIntent(file)
            if (intent.resolveActivity(packageManager) != null) {
                launcher.launch(intent)
            } else {
                Logger.e("CameraProvider intent resolveActivity error")
                onError()
            }
        } else {
            Logger.e("Error, file: $file")
            onError()
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
        private const val WAIT_SETTINGS = "camera_picker_wait_settings"
    }
}
