package crocodile8.image_picker_plus.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import java.io.Closeable
import java.io.File
import java.io.IOException

object Utils {

    private val separator = File.separator

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

    internal fun clearTmpDir(context: Context) {
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

    internal fun createEmptyLocalUniqueFile(context: Context, ext: String = "jpg"): File? =
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

    private fun getTmpDir(context: Context): File {
        // Don't forget file_provider_paths.xml if change
        val dir = File(context.cacheDir.absolutePath + "${separator}image_picker_plus_cache")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
}

fun Closeable.closeSilent() =
    try {
        close()
    } catch (e: Exception) {
        // Ignore
    }
