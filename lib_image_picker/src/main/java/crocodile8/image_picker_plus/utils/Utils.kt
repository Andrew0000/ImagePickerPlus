package crocodile8.image_picker_plus.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import java.io.Closeable
import java.io.File
import java.io.IOException

internal object Utils {

    private val separator = File.separator

    fun clearTmpDir(context: Context) {
        try {
            val listFiles = getTmpDir(context).listFiles()
            Logger.d("clearTmpDir: ${listFiles?.size}")
            listFiles?.forEach {
                it.delete()
            }
        } catch (e: IOException) {
            Logger.e("clearTmpDir", e)
        }
    }

    @Throws
    fun copyUriContentToFile(context: Context, uri: Uri, file: File) {
        @SuppressLint("Recycle") // False positive, inputStream is closed with .closeSilent()
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val outputStream = file.outputStream()
        try {
            inputStream.copyTo(outputStream, 16 * 1024)
        } finally {
            inputStream.closeSilent()
            outputStream.closeSilent()
        }
    }

    fun createEmptyLocalUniqueFile(context: Context, ext: String = "jpg"): File? =
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
        val dir = File(context.cacheDir.absolutePath + "${separator}image_picker_plus_cache")
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

fun Closeable.closeSilent() =
    try {
        close()
    } catch (e: Exception) {
        // Ignore
    }
