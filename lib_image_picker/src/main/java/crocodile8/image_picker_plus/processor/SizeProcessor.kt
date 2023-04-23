package crocodile8.image_picker_plus.processor

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.core.net.toFile
import androidx.core.net.toUri
import crocodile8.image_picker_plus.PickRequest
import crocodile8.image_picker_plus.utils.BitmapUtils.decodeScaledBitmapFromFile
import crocodile8.image_picker_plus.utils.Utils.createEmptyUniqueFile
import crocodile8.image_picker_plus.utils.Utils.mapCompressFormat
import crocodile8.image_picker_plus.utils.getExtOrJpeg

internal class SizeProcessor(
    activity: ComponentActivity,
    private val onResult: (Uri?) -> Unit,
) {
    private val context = activity.applicationContext

    fun launch(uri: Uri, request: PickRequest) {
        if (request.maxSidePx > 0) {
            val bitmap = decodeScaledBitmapFromFile(uri.toFile(), request.maxSidePx)
            val ext = uri.getExtOrJpeg(context)
            val file = createEmptyUniqueFile(context, ext)
            if (file != null) {
                bitmap.compress(mapCompressFormat(ext), 80, file.outputStream())
                bitmap.recycle()
                onResult(file.toUri())
            } else {
                onResult(null)
            }
        } else {
            onResult(uri)
        }
    }

}
