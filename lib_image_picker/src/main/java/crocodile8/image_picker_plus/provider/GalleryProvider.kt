package crocodile8.image_picker_plus.provider

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import crocodile8.image_picker_plus.PickRequest
import crocodile8.image_picker_plus.TypeFilter
import crocodile8.image_picker_plus.utils.Logger
import crocodile8.image_picker_plus.utils.FileUtils.copyUriContentToFile
import crocodile8.image_picker_plus.utils.FileUtils.createEmptyLocalUniqueFile
import crocodile8.image_picker_plus.utils.getExtOrJpeg

// https://developer.android.com/training/data-storage/shared/documents-files#bitmap

class GalleryProvider(
    activity: ComponentActivity,
    request: PickRequest,
    onResult: (Uri?, Throwable?) -> Unit,
) : Provider(activity, request, onResult) {

    override fun onActivityResult(resultCode: Int, intent: Intent?) {
        val uri = intent?.data
        Logger.i("GalleryProvider uri: $uri")
        if (resultCode == Activity.RESULT_OK && uri != null) {
            // Copy content of public Uri to a local file
            val tmpFile = createEmptyLocalUniqueFile(context, uri.getExtOrJpeg(context))
            Logger.i("GalleryProvider tmpFile: $tmpFile")
            if (tmpFile == null) {
                Logger.e("GalleryProvider null tmpFile")
                onError()
            } else {
                try {
                    copyUriContentToFile(context, uri, tmpFile)
                    onSuccess(tmpFile.toUri())
                } catch (e: Exception) {
                    Logger.e("", e)
                    onError()
                }
            }
        } else {
            Logger.e("GalleryProvider result error: ${resultCode}, uri: $uri")
            onError()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            // Don't react on activity recreation
            return
        }
        launch()
    }

    private fun launch() {
        val intent = createPickIntent(request.filter)
        if (intent.resolveActivity(packageManager) != null) {
            Logger.i("GalleryProvider intent resolveActivity OK")
            launcher.launch(intent)
        } else {
            Logger.e("GalleryProvider intent resolveActivity error")
            onError()
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
