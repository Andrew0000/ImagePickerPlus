package crocodile8.image_picker_plus.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap

internal fun Uri.getExtOrJpeg(context: Context): String {
    val ext = getExt(context)
    if (!ext.isNullOrEmpty()) {
        return ext
    }
    return "jpg"
}

internal fun Uri.getExt(context: Context): String? {
    val mime = context.contentResolver.getType(this)
    Logger.d("Uri.getExt: $this, mime: $mime")
    if (mime != null) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
    }
    if (ContentResolver.SCHEME_FILE == scheme) {
        return MimeTypeMap.getFileExtensionFromUrl(path).also {
            Logger.d("ext: $it")
        }
    }
    return getExtFromFileName().also {
        Logger.d("ext: $it")
    }
}

/**
 * Last resort, doesn't work with files from some uri providers.
 */
internal fun Uri.getExtFromFileName(): String? =
    path.getFileExt()

/**
 * Last resort, doesn't work with files from some uri providers.
 */
private fun String?.getFileExt(): String? {
    if (this?.contains(".") == true) {
        return substring(lastIndexOf(".") + 1)
    }
    return null
}
