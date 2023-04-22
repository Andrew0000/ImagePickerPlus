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

    private val galleryPicker by lazy { GalleryPicker(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.i("ImagePickerPlusActivity onCreate request: $request")
        when (request.source) {
            PickSource.GALLERY -> {
                galleryPicker.launch(this, request)
            }
            PickSource.CAMERA -> {
                //TODO
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.i("ImagePickerPlusActivity onActivityResult " +
                "requestCode: $requestCode, resultCode: $resultCode, data: $data")
        when (request.source) {
            PickSource.GALLERY -> {
                val uri = galleryPicker.onActivityResult(requestCode, resultCode, data)
                setResultOK(uri)
            }
            PickSource.CAMERA -> {
                //TODO
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