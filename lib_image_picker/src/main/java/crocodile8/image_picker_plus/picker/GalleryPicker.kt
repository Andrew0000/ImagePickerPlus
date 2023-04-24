package crocodile8.image_picker_plus.picker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.net.toUri
import crocodile8.image_picker_plus.PickRequest
import crocodile8.image_picker_plus.TypeFilter
import crocodile8.image_picker_plus.utils.Logger
import crocodile8.image_picker_plus.utils.Utils.copyUriContentToFile
import crocodile8.image_picker_plus.utils.Utils.createEmptyLocalUniqueFile
import crocodile8.image_picker_plus.utils.getExtOrJpeg

// https://developer.android.com/training/data-storage/shared/documents-files#bitmap

internal class GalleryPicker(
    private val activity: ComponentActivity,
    private val onResult: (Uri?) -> Unit,
) {
    private val context = activity.applicationContext

    private val launcher = activity.registerForActivityResult(StartActivityForResult()) {
        val uri = it.data?.data
        Logger.i("GalleryPicker uri: $uri")
        if (it.resultCode == Activity.RESULT_OK && uri != null) {
            // Copy content of public Uri to a local file
            val tmpFile = createEmptyLocalUniqueFile(context, uri.getExtOrJpeg(context))
            Logger.i("GalleryPicker tmpFile: $tmpFile")
            if (tmpFile == null) {
                Logger.e("GalleryPicker null tmpFile")
                onResult(null)
            } else {
                try {
                    copyUriContentToFile(context, uri, tmpFile)
                    onResult(tmpFile.toUri())
                } catch (e: Exception) {
                    Logger.e("", e)
                    onResult(null)
                }
            }
        } else {
            Logger.e("GalleryPicker result error: ${it.resultCode}, uri: $uri")
            onResult(null)
        }
    }

    fun launch(request: PickRequest) {
        val intent = createPickIntent(request.filter)
        if (intent.resolveActivity(activity.packageManager) != null) {
            Logger.i("GalleryPicker intent resolveActivity OK")
            launcher.launch(intent)
        } else {
            Logger.e("GalleryPicker intent resolveActivity error")
            onResult(null)
        }
    }

    private fun createPickIntent(filter: TypeFilter): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = filter.mimeType
            if (filter.subTypes.isNotEmpty()) {
                putExtra(Intent.EXTRA_MIME_TYPES, filter.subTypes.toTypedArray())
            }
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        return intent
    }
}
