package crocodile8.image_picker_plus.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

/*
 * https://developer.android.com/topic/performance/graphics/load-bitmap
 */
object BitmapUtils {

    fun decodeScaledBitmapFromFile(imageFile: File, maxSide: Int): Bitmap {
        val bitmap = decodeSampledBitmapFromFile(imageFile, maxSide)
        Logger.d("bitmap maxSide: $maxSide decoded as: ${bitmap.width} / ${bitmap.height}")

        val resized = resize(bitmap, maxSide, maxSide)
        bitmap.recycle()
        Logger.d("bitmap maxSide: $maxSide resized as: ${resized.width} / ${resized.height}")

        return resized
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
