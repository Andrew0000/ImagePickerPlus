package crocodile8.image_picker_plus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ImagePickerPlusActivity : AppCompatActivity() {

    private val request: PickRequest by lazy {
        @Suppress("DEPRECATION")
        intent?.getSerializableExtra(REQUEST_SPEC) as PickRequest
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    companion object {

        const val REQUEST_SPEC = "request_spec"
    }
}