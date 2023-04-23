package crocodile8.image_picker_plus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import crocodile8.image_picker_plus.utils.Logger

class MainActivity : AppCompatActivity() {

    private val imageView1 by lazy { findViewById<ImageView>(R.id.imageView1) }
    private val btnGallery by lazy { findViewById<Button>(R.id.btnGallery) }
    private val btnCamera by lazy { findViewById<Button>(R.id.btnCamera) }
    private val cbSize by lazy { findViewById<CheckBox>(R.id.cbSize) }
    private val cbUseCrop by lazy { findViewById<CheckBox>(R.id.cbUseCrop) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Logger.enabled = true

        btnGallery.setOnClickListener {
            ImagePickerPlus.launch(
                activity = this,
                PickRequest(
                    source = PickSource.GALLERY,
                    maxSidePx = getSize(),
                    useCrop = cbUseCrop.isChecked,
                )
            )
        }

        btnCamera.setOnClickListener {
            ImagePickerPlus.launch(
                activity = this,
                PickRequest(
                    source = PickSource.CAMERA,
                    maxSidePx = getSize(),
                    useCrop = cbUseCrop.isChecked,
                )
            )
        }
    }

    private fun getSize() =
        if (cbSize.isChecked) {
           50
        } else {
            0
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.data?.let {
            imageView1.setImageURI(it)
        }
    }
}
