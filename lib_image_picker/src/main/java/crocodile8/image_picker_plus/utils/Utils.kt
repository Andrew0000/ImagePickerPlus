package crocodile8.image_picker_plus.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import java.io.File
import java.io.IOException

internal object Utils {

    fun createEmptyUniqueFile(context: Context, ext: String = "jpg"): File? =
        try {
            val fileName = "tmp_${System.currentTimeMillis()}"
            val fileNameFull = "$fileName.$ext"
            val file = File(getTmpDir(context), fileNameFull)
            file.createNewFile()
            file
        } catch (e: IOException) {
            Logger.e("", e)
            null
        }

    fun mapCompressFormat(ext: String): Bitmap.CompressFormat {
        return when {
            ext.contains("png", ignoreCase = true) -> {
                Bitmap.CompressFormat.PNG
            }
            ext.contains("webp", ignoreCase = true) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSLESS
                } else {
                    @Suppress("DEPRECATION")
                    Bitmap.CompressFormat.WEBP
                }
            }
            else -> {
                Bitmap.CompressFormat.JPEG
            }
        }
    }

    private fun getTmpDir(context: Context): File {
        val dir = File(context.cacheDir.absolutePath + "/image_picker_plus_cache")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
}

internal fun Uri.getExtOrJpeg(context: Context): String =
    getExt(context) ?: "jpg"

internal fun Uri.getExt(context: Context): String? {
    val mime = context.contentResolver.getType(this)
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
}