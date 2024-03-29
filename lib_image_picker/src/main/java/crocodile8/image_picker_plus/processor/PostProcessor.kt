package crocodile8.image_picker_plus.processor

import android.app.Activity
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import crocodile8.image_picker_plus.ImageFormat
import crocodile8.image_picker_plus.PickRequest
import crocodile8.image_picker_plus.utils.ImageFormatUtils
import crocodile8.image_picker_plus.utils.Logger
import crocodile8.image_picker_plus.utils.FileUtils
import crocodile8.image_picker_plus.utils.getExt
import crocodile8.image_picker_plus.utils.getExtOrJpeg
import crocodile8.image_picker_plus.utils.toCompressFormat
import java.io.File

internal class PostProcessor(
    activity: ComponentActivity,
    private val request: PickRequest,
    private val onResult: (Uri?) -> Unit,
) {
    private val context = activity.applicationContext

    private val launcher = activity.registerForActivityResult(StartActivityForResult()) {
        val data = it.data
        val uri = if (data != null) {
            UCrop.getOutput(data)
        } else {
            null
        }
        Logger.i("uri: $uri / $data")
        if (it.resultCode == Activity.RESULT_OK && uri != null) {
            val finalUri = renameFileExtIfNeeded(uri, request.transformation.encodeToFormat)
            onResult(finalUri)
        } else {
            Logger.e("PostProcessor result error: ${it.resultCode}, uri: $uri")
            onResult(null)
        }
    }

    fun launch(uri: Uri) {
        val ext = uri.getExtOrJpeg(context)
        val file = FileUtils.createEmptyLocalUniqueFile(context, ext)
        Logger.d("PostProcessor launch file: $file, from: $uri")

        if (file == null || !file.exists()) {
            Logger.e("No file")
            onResult(null)
            return
        }

        val uCrop = UCrop
            .of(uri, Uri.fromFile(file))
            .applyMaxSideLimit()
            .withOptions(
                UCrop.Options().apply {
                    applyFormatRestriction(ext)
                    applyAspectRatioRestriction()
                }
            )

        launcher.launch(uCrop.getIntent(context))
    }

    private fun UCrop.applyMaxSideLimit() = let {
        val maxSidePx = request.transformation.maxSidePx
        if (maxSidePx > 0) {
            it.withMaxResultSize(maxSidePx, maxSidePx)
        } else {
            it
        }
    }

    private fun UCrop.Options.applyFormatRestriction(ext: String) {
        val encodeToFormat = request.transformation.encodeToFormat
        if (encodeToFormat != null) {
            setCompressionFormat(encodeToFormat.toCompressFormat())
        } else {
            setCompressionFormat(ImageFormatUtils.getCompressFormat(ext))
        }
    }

    private fun UCrop.Options.applyAspectRatioRestriction() {
        request.transformation.strictAspectRatio?.let { ratio ->
            setAspectRatioOptions(
                0,
                AspectRatio(ratio.title, ratio.x, ratio.y),
            )
        }
    }

    private fun renameFileExtIfNeeded(uri: Uri, requiredFormat: ImageFormat?): Uri {
        if (requiredFormat == null) {
            return uri
        }
        val ext = uri.getExt(context)
        val requiredExt = requiredFormat.ext
        if (ext != null && ext != requiredExt) {
            try {
                val originalFile = uri.toFile()
                var path = originalFile.path
                if (path.endsWith(".$ext")) {
                    path = path.dropLast(ext.length + 1)
                }
                val newFile = File("$path.$requiredExt")
                originalFile.renameTo(newFile)
                val finalUri = newFile.toUri()
                Logger.d("Renamed OK: $finalUri")
                return finalUri
            } catch (e: Exception) {
                Logger.e("", e)
            }
        }
        return uri
    }

}
