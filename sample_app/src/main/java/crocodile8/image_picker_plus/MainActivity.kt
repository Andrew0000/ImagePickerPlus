package crocodile8.image_picker_plus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.net.toFile
import crocodile8.image_picker_plus.utils.BitmapUtils
import crocodile8.image_picker_plus.utils.Logger

class MainActivity : AppCompatActivity() {

    private val imageView1 by lazy { findViewById<ImageView>(R.id.imageView1) }
    private val tvDescription by lazy { findViewById<TextView>(R.id.tvDescription) }
    private val btnGallery by lazy { findViewById<Button>(R.id.btnGallery) }
    private val btnCamera by lazy { findViewById<Button>(R.id.btnCamera) }
    private val cbSize by lazy { findViewById<CheckBox>(R.id.cbSize) }
    private val cbForceWebP by lazy { findViewById<CheckBox>(R.id.cbForceWebP) }

    private val launcher = registerForActivityResult(StartActivityForResult()) {
        it?.data?.data?.let { uri ->
            Log.i("IPP", "result: $uri")
            // Don't use .setImageURI() in order to check access via File
            val bitmap = BitmapUtils.decodeBitmapWithFixedRotation(uri.toFile())
            imageView1.setImageBitmap(bitmap)
            tvDescription.text = uri.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Logger.enabled = true

        btnGallery.setOnClickListener {
            ImagePickerPlus.createIntent(
                activity = this,
                PickRequest(
                    source = PickSource.GALLERY,
                    transformation = getTransformation(),
                )
            ).let { launcher.launch(it) }
        }

        btnCamera.setOnClickListener {
            ImagePickerPlus.createIntent(
                activity = this,
                PickRequest(
                    source = PickSource.CAMERA,
                    transformation = getTransformation(),
                )
            ).let { launcher.launch(it) }
        }
    }

    private fun getTransformation() =
        ImageTransformation(
            maxSidePx = if (cbSize.isChecked) {
                50
            } else {
                -1
            },
            encodeToFormat = if (cbForceWebP.isChecked) {
                ImageFormat.WEBP
            } else {
                null
            },
        )

}
