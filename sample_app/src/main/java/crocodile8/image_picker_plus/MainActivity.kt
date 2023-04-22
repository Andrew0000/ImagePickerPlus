package crocodile8.image_picker_plus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import crocodile8.image_picker_plus.utils.Logger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger.enabled = true

        ImagePickerPlus.launch(
            activity = this,
            PickRequest(
                source = PickSource.GALLERY,
                size = PickSize(maxSidePx = 500, maxWeightBytes = 10_000),
                useCrop = true,
            )
        )
    }
}