package crocodile8.image_picker_plus.processor

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.core.net.toFile
import androidx.core.net.toUri
import crocodile8.image_picker_plus.PickRequest
import crocodile8.image_picker_plus.utils.BitmapUtils.decodeScaledBitmapFromFile
import crocodile8.image_picker_plus.utils.Logger
import crocodile8.image_picker_plus.utils.Utils.createEmptyLocalUniqueFile
import crocodile8.image_picker_plus.utils.Utils.mapCompressFormat
import crocodile8.image_picker_plus.utils.getExtOrJpeg

internal class SizeProcessor(
    activity: ComponentActivity,
    private val onResult: (Uri?) -> Unit,
) {
    private val context = activity.applicationContext

    fun launch(uri: Uri, request: PickRequest) {
        if (request.maxSidePx > 0) {
            try {
                val bitmap = decodeScaledBitmapFromFile(uri.toFile(), request.maxSidePx)
                val ext = uri.getExtOrJpeg(context)
                val file = createEmptyLocalUniqueFile(context, ext)
                Logger.d("SizeProcessor file: $file")
                if (file != null) {
                    bitmap.compress(mapCompressFormat(ext), 80, file.outputStream())
                    bitmap.recycle()
                    onResult(file.toUri())
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                Logger.e("SizeProcessor error", e)
                onResult(null)
            }
        } else {
            onResult(uri)
        }
    }

}
