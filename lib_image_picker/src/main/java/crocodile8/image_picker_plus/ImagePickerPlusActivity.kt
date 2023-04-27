package crocodile8.image_picker_plus

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import crocodile8.image_picker_plus.processor.CropProcessor
import crocodile8.image_picker_plus.processor.SizeProcessor
import crocodile8.image_picker_plus.provider.CameraProvider
import crocodile8.image_picker_plus.provider.GalleryProvider
import crocodile8.image_picker_plus.utils.Logger
import crocodile8.image_picker_plus.utils.Utils
import crocodile8.image_picker_plus.utils.isCameraPermissionDeclared
import crocodile8.image_picker_plus.utils.isCameraPermissionGranted
import crocodile8.image_picker_plus.utils.launchAppSettings

internal class ImagePickerPlusActivity : AppCompatActivity() {

    private val request: PickRequest by lazy {
        @Suppress("DEPRECATION")
        intent?.getSerializableExtra(REQUEST_SPEC) as PickRequest
    }

    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) {
        if (waitingCameraPermission) {
            if (isCameraPermissionGranted()) {
                waitingCameraPermission = false
                waitingCameraPermissionSettings = false
                cameraProvider.launch(request)
            } else {
                Toast.makeText(this, R.string.ipp_camera_permission_go_to_settings, Toast.LENGTH_LONG).show()
                waitingCameraPermissionSettings = true
                launchAppSettings()
            }
        } else {
            finishAsCancelled()
        }
    }

    private val galleryProvider by lazy {
        GalleryProvider(this) {
            routeResult(it)
        }
    }

    private val cameraProvider by lazy {
        CameraProvider(this) {
            routeResult(it)
        }
    }

    private val sizeProcessor by lazy {
        SizeProcessor(this) {
            sized = true
            routeResult(it)
        }
    }

    private val cropProcessor by lazy {
        CropProcessor(this) {
            cropped = true
            routeResult(it)
        }
    }

    //TODO store these flags in single serialized state + state machine to decide next steps in onCreate or onResume?
    private var sized = false
    private var cropped = false

    private var waitingCameraPermission = false
    private var waitingCameraPermissionSettings = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val launchedBefore = savedInstanceState != null // if activity recreation
        Logger.i("ImagePickerPlusActivity onCreate request: $request")

        savedInstanceState?.getBoolean(SAVED_SIZED)?.let { sized = it }
        savedInstanceState?.getBoolean(SAVED_CROPPED)?.let { cropped = it }
        savedInstanceState?.getBoolean(SAVED_WAIT_CAMERA)?.let { waitingCameraPermission = it }
        savedInstanceState?.getBoolean(SAVED_WAIT_CAMERA_SETTINGS)?.let { waitingCameraPermissionSettings = it }
        Logger.i("ImagePickerPlusActivity onCreate, sized: $sized, cropped: $cropped")

        if (!launchedBefore && request.clearPreviousCache) {
            Utils.clearTmpDir(applicationContext)
        }

        if (request.useCrop) {
            cropProcessor // initialization
        }
        when (request.source) {
            PickSource.GALLERY -> {
                galleryProvider // initialization
                if (!launchedBefore) {
                    galleryProvider.launch(request)
                }
            }
            PickSource.CAMERA -> {
                cameraProvider // initialization
                if (!launchedBefore) {
                    val cameraPermissionDeclared = isCameraPermissionDeclared()
                    Logger.d("cameraPermissionDeclared: $cameraPermissionDeclared")
                    if (cameraPermissionDeclared && !isCameraPermissionGranted()) {
                        waitingCameraPermission = true
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        cameraProvider.launch(request)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (waitingCameraPermissionSettings) {
            if (isCameraPermissionGranted()) {
                waitingCameraPermissionSettings = false
                cameraProvider.launch(request)
            } else {
                finishAsCancelled()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(SAVED_SIZED, sized)
        outState.putBoolean(SAVED_CROPPED, cropped)
        outState.putBoolean(SAVED_WAIT_CAMERA, waitingCameraPermission)
        outState.putBoolean(SAVED_WAIT_CAMERA_SETTINGS, waitingCameraPermissionSettings)
        Logger.i("onSaveInstanceState, sized: $sized, cropped: $cropped")

        when (request.source) {
            PickSource.GALLERY -> {
                // Empty
            }
            PickSource.CAMERA -> {
                cameraProvider.onSaveInstanceState(outState)
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        when (request.source) {
            PickSource.GALLERY -> {
                // Empty
            }
            PickSource.CAMERA -> {
                cameraProvider.onRestoreInstanceState(savedInstanceState)
            }
        }
    }

    private fun routeResult(uri: Uri?) {
        when {
            uri != null && request.maxSidePx > 0 && !sized -> {
                sizeProcessor.launch(uri, request)
            }
            uri != null && request.useCrop && !cropped -> {
                cropProcessor.launch(uri)
            }
            else -> {
                finishWithResult(uri)
            }
        }
    }

    private fun finishWithResult(uri: Uri?) {
        if (uri == null) {
            finishAsCancelled()
        } else {
            finishAsOK(uri)
        }
    }

    private fun finishAsCancelled() {
        val intent = Intent()
        intent.putExtra("error", "Error")
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    private fun finishAsOK(uri: Uri) {
        val intent = Intent()
        intent.data = uri
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        const val REQUEST_SPEC = "request_spec"
        private const val SAVED_SIZED = "camera_picker_activity_saved_sized"
        private const val SAVED_CROPPED = "camera_picker_activity_saved_cropped"
        private const val SAVED_WAIT_CAMERA = "camera_picker_activity_saved_wait_camera"
        private const val SAVED_WAIT_CAMERA_SETTINGS = "camera_picker_activity_saved_wait_camera_settings"
    }
}