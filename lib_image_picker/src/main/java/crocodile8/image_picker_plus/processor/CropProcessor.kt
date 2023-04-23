package crocodile8.image_picker_plus.processor

import android.app.Activity
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.yalantis.ucrop.UCrop
import crocodile8.image_picker_plus.utils.Logger
import crocodile8.image_picker_plus.utils.Utils
import crocodile8.image_picker_plus.utils.getExtOrJpeg

internal class CropProcessor(
    activity: ComponentActivity,
    private val onResult: (Uri?) -> Unit,
) {
    private val context = activity.applicationContext

    private val launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val data = it.data
        val uri = if (data != null) {
            UCrop.getOutput(data)
        } else {
            null
        }
        Logger.i("uri: $uri / $data")
        if (it.resultCode == Activity.RESULT_OK && uri != null) {
            onResult(uri)
        } else {
            Logger.e("CropProcessor result error: ${it.resultCode}, uri: $uri")
            onResult(null)
        }
    }

    fun launch(uri: Uri) {
        val ext = uri.getExtOrJpeg(context)
        val file = Utils.createEmptyUniqueFile(context, ext)
        Logger.d("CropProxy launch file: $file")

        if (file == null || !file.exists()) {
            Logger.e("No file")
            onResult(null)
            return
        }

        val uCrop = UCrop
            .of(uri, Uri.fromFile(file))
            .withOptions(
                UCrop.Options().apply {
                    setCompressionFormat(Utils.mapCompressFormat(ext))
                }
            )

        launcher.launch(uCrop.getIntent(context))
    }
}
