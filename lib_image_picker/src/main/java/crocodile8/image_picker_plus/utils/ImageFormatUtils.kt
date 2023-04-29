package crocodile8.image_picker_plus.utils

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import crocodile8.image_picker_plus.ImageFormat

@Suppress("DEPRECATION")
fun ImageFormat.toCompressFormat() = when (this) {
    ImageFormat.JPEG -> Bitmap.CompressFormat.JPEG
    ImageFormat.PNG -> Bitmap.CompressFormat.PNG
    ImageFormat.WEBP -> Bitmap.CompressFormat.WEBP
}

@Suppress("Unused")
fun Bitmap.CompressFormat.asStringExt() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        asStringExtNew()
    } else {
        asStringExtLegacy()
    }

@RequiresApi(Build.VERSION_CODES.R)
@Suppress("DEPRECATION")
private fun Bitmap.CompressFormat.asStringExtNew() = when (this) {
    Bitmap.CompressFormat.PNG -> "png"
    Bitmap.CompressFormat.WEBP,
    Bitmap.CompressFormat.WEBP_LOSSY,
    Bitmap.CompressFormat.WEBP_LOSSLESS -> "webp"
    else -> "jpeg"
}

private fun Bitmap.CompressFormat.asStringExtLegacy() = when (this) {
    Bitmap.CompressFormat.PNG -> "png"
    @Suppress("DEPRECATION")
    Bitmap.CompressFormat.WEBP -> "webp"
    else -> "jpeg"
}
