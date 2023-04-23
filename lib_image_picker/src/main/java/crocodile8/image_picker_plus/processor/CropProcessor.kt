package crocodile8.image_picker_plus.processor

import android.app.Activity
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.yalantis.ucrop.UCrop
import crocodile8.image_picker_plus.utils.Logger
import crocodile8.image_picker_plus.utils.Utils
import crocodile8.image_picker_plus.utils.getExtOrJpeg
import java.io.File

internal class CropProcessor(
    activity: ComponentActivity,
    private val onResult: (Uri?) -> Unit,
) {
    private val context = activity.applicationContext

    private var tmpFile: File? = null

    private val launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val uri = it.data?.data
        val tmpUri = Uri.fromFile(tmpFile)
        Logger.i("uri: $uri / $tmpUri")
        if (it.resultCode == Activity.RESULT_OK && tmpUri != null) {
            onResult(tmpUri)
        } else {
            Logger.e("CropProxy result error: ${it.resultCode}, uri: $uri")
            onResult(null)
        }
    }

    fun launch(uri: Uri) {
        val ext = uri.getExtOrJpeg(context)
        val file = Utils.createEmptyUniqueFile(context, ext)
        Logger.d("CropProxy launch file: $file")
        tmpFile = file

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
