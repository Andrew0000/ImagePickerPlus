package crocodile8.image_picker_plus

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crocodile8.image_picker_plus.utils.Logger

class ImagePickerPlusActivity : AppCompatActivity() {

    private val request: PickRequest by lazy {
        @Suppress("DEPRECATION")
        intent?.getSerializableExtra(REQUEST_SPEC) as PickRequest
    }

    private val galleryPicker by lazy {
        GalleryPicker(this) {
            setResultOK(it)
        }
    }

    private val cameraPicker by lazy {
        CameraPicker(this) {
            setResultOK(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.i("ImagePickerPlusActivity onCreate request: $request")
        when (request.source) {
            PickSource.GALLERY -> {
                galleryPicker.launch(request)
            }
            PickSource.CAMERA -> {
                cameraPicker.launch(request)
            }
        }
    }

    private fun setResultOK(uri: Uri?) {
        val intent = Intent()
        intent.data = uri
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {

        const val REQUEST_SPEC = "request_spec"
    }
}