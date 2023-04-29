@file:Suppress("Unused", "MemberVisibilityCanBePrivate")

package crocodile8.image_picker_plus.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import java.io.File

/*
 * https://developer.android.com/topic/performance/graphics/load-bitmap
 */
object BitmapUtils {

    fun resize(uri: Uri, context: Context, maxSidePx: Int, onResult: (Uri?) -> Unit) {
        if (maxSidePx > 0) {
            try {
                val bitmap = decodeScaledBitmapFromFile(uri.toFile(), maxSidePx)
                val ext = uri.getExtOrJpeg(context)
                val file = Utils.createEmptyLocalUniqueFile(context, ext)
                Logger.d("resize file: $file")
                if (file != null) {
                    bitmap.compress(ImageFormatUtils.mapCompressFormat(ext), 80, file.outputStream())
                    bitmap.recycle()
                    onResult(file.toUri())
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                Logger.e("SizeProcessor error", e)
                onResult(null)
            }
        } else {
            onResult(uri)
        }
    }

    fun decodeScaledBitmapFromFile(imageFile: File, maxSide: Int): Bitmap {
        Logger.d("decodeScaledBitmapFromFile: $imageFile")

        val bitmap = decodeSampledBitmapFromFile(imageFile, maxSide)
        Logger.d("bitmap maxSide: $maxSide decoded as: ${bitmap.width} / ${bitmap.height}")

        val resized = resize(bitmap, maxSide, maxSide)
        bitmap.recycle()
        Logger.d("bitmap maxSide: $maxSide resized as: ${resized.width} / ${resized.height}")

        return fixImageRotation(imageFile, resized)
    }

    private fun fixImageRotation(imageFile: File, bitmap: Bitmap): Bitmap {
        val neededRotation = getNeededRotation(imageFile)
        if (neededRotation == 0f) {
            return bitmap
        }
        val matrix = Matrix()
        matrix.postRotate(neededRotation)
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotated
    }

    private fun getNeededRotation(imageFile: File): Float {
        val exif = ExifInterface(imageFile.absolutePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
        Logger.d("orientation: $orientation")
        return when (orientation) {
            6 -> 90f
            3 -> 180f
            8 -> 270f
            else -> 0f
        }
    }

    private fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        return if (maxHeight > 0 && maxWidth > 0) {
            val ratioBitmap = image.width.toFloat() / image.height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
        } else {
            image
        }
    }

    /*
     * https://developer.android.com/topic/performance/graphics/load-bitmap#load-bitmap
     */
    private fun decodeSampledBitmapFromFile(imageFile: File, maxSide: Int): Bitmap {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(imageFile.absolutePath, this)
            inSampleSize = calculateInSampleSize(this, maxSide)
            inJustDecodeBounds = false
            BitmapFactory.decodeFile(imageFile.absolutePath, this)
        }
    }

    /*
     * https://developer.android.com/topic/performance/graphics/load-bitmap#load-bitmap
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, maxSide: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > maxSide || width > maxSide) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= maxSide && halfWidth / inSampleSize >= maxSide) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

}
