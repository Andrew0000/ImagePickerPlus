package crocodile8.image_picker_plus.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
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
