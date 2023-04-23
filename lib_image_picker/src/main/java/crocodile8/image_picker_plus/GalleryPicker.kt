package crocodile8.image_picker_plus

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import crocodile8.image_picker_plus.utils.Logger

// https://developer.android.com/training/data-storage/shared/documents-files#bitmap

class GalleryPicker(
    private val activity: ComponentActivity,
    private val onResult: (Uri?) -> Unit,
) {

    private val launcher = activity.registerForActivityResult(StartActivityForResult()) {
        val uri = it.data?.data
        Logger.i("uri: $uri")
        if (it.resultCode == Activity.RESULT_OK && uri != null) {
            onResult(uri)
        } else {
            Logger.e("GalleryPicker result error: ${it.resultCode}, uri: $uri")
            onResult(null)
        }
    }

    fun launch(request: PickRequest) {
        val intent = createPickIntent(request.mimeTypes)
        if (intent.resolveActivity(activity.packageManager) != null) {
            Logger.i("GalleryPicker intent resolveActivity OK")
            launcher.launch(intent)
        } else {
            Logger.e("GalleryPicker intent resolveActivity error")
            //TODO show error
        }
    }

    private fun createPickIntent(mimeTypes: List<String>): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            //TODO handle other types
            type = "image/*"
            if (mimeTypes.isNotEmpty()) {
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes.toTypedArray())
            }
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        return intent
    }
}
