package crocodile8.image_picker_plus

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import crocodile8.image_picker_plus.utils.Logger

class GalleryPicker(
    private val context: Context,
) {

    fun launch(activity: Activity, request: PickRequest) {
        val intent = createPickIntent(request.mimeTypes)
        if (intent.resolveActivity(context.packageManager) != null) {
            Logger.i("GalleryPicker intent resolveActivity OK")
            activity.startActivityForResult(intent, REQUEST_CODE)
        } else {
            Logger.e("GalleryPicker intent resolveActivity error")
            //TODO show error
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Uri? {
        val uri = data?.data
        Logger.i("uri: $uri")
        return uri
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

    companion object {

        const val REQUEST_CODE = 802
    }
}
