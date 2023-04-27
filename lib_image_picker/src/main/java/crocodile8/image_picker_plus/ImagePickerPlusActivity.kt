package crocodile8.image_picker_plus

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crocodile8.image_picker_plus.processor.CropProcessor
import crocodile8.image_picker_plus.processor.SizeProcessor
import crocodile8.image_picker_plus.provider.CameraProvider
import crocodile8.image_picker_plus.provider.GalleryProvider
import crocodile8.image_picker_plus.provider.Provider
import crocodile8.image_picker_plus.utils.Logger
import crocodile8.image_picker_plus.utils.Utils

internal class ImagePickerPlusActivity : AppCompatActivity() {

    private val request: PickRequest by lazy {
        @Suppress("DEPRECATION")
        intent?.getSerializableExtra(REQUEST_SPEC) as PickRequest
    }

    private lateinit var provider: Provider

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

    private var sized = false
    private var cropped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val launchedBefore = savedInstanceState != null // if activity recreation
        Logger.i("ImagePickerPlusActivity onCreate request: $request")

        savedInstanceState?.getBoolean(SAVED_SIZED)?.let { sized = it }
        savedInstanceState?.getBoolean(SAVED_CROPPED)?.let { cropped = it }
        Logger.i("ImagePickerPlusActivity onCreate, sized: $sized, cropped: $cropped")

        if (!launchedBefore && request.clearPreviousCache) {
            Utils.clearTmpDir(applicationContext)
        }

        if (request.useCrop) {
            cropProcessor // initialization
        }

        provider = getProvider()
        provider.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_SIZED, sized)
        outState.putBoolean(SAVED_CROPPED, cropped)
        Logger.i("onSaveInstanceState, sized: $sized, cropped: $cropped")
        provider.onSaveInstanceState(outState)
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

    private fun getProvider() = when (request.source) {
        PickSource.GALLERY -> {
            GalleryProvider(this, request) { uri, _ ->
                routeResult(uri)
            }
        }
        PickSource.CAMERA -> {
            CameraProvider(this, request) { uri, _ ->
                routeResult(uri)
            }
        }
    }

    companion object {
        const val REQUEST_SPEC = "request_spec"
        private const val SAVED_SIZED = "camera_picker_activity_saved_sized"
        private const val SAVED_CROPPED = "camera_picker_activity_saved_cropped"
    }
}