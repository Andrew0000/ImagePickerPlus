package crocodile8.image_picker_plus

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crocodile8.image_picker_plus.picker.CameraPicker
import crocodile8.image_picker_plus.picker.GalleryPicker
import crocodile8.image_picker_plus.processor.CropProcessor
import crocodile8.image_picker_plus.utils.Logger

//TODO check with DKA
//TODO delete tmp files

internal class ImagePickerPlusActivity : AppCompatActivity() {

    private val request: PickRequest by lazy {
        @Suppress("DEPRECATION")
        intent?.getSerializableExtra(REQUEST_SPEC) as PickRequest
    }

    private val galleryPicker by lazy {
        GalleryPicker(this) {
            routeResult(it)
        }
    }

    private val cameraPicker by lazy {
        CameraPicker(this) {
            routeResult(it)
        }
    }

    private val cropProcessor by lazy {
        CropProcessor(this) {
            cropped = true
            routeResult(it)
        }
    }

    //TODO store in saved state
    private var cropped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.i("ImagePickerPlusActivity onCreate request: $request")
        if (request.useCrop) {
            cropProcessor // initialization
        }
        when (request.source) {
            PickSource.GALLERY -> {
                galleryPicker.launch(request)
            }
            PickSource.CAMERA -> {
                cameraPicker.launch(request)
            }
        }
    }

    private fun routeResult(uri: Uri?) {
        if (uri != null && request.useCrop && !cropped) {
            cropProcessor.launch(uri)
        } else {
            finishWithResult(uri)
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
    }
}