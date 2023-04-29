package crocodile8.image_picker_plus

import java.io.Serializable

data class PickRequest(
    val source: PickSource,
    val filter: TypeFilter = TypeFilter(),
    val transformation: ImageTransformation = ImageTransformation(),
    val clearPreviousCache: Boolean = true,
) : Serializable

enum class PickSource {
    GALLERY,
    CAMERA,
}

data class TypeFilter(
    val mimeType: String = "image/*",
    val subTypes: List<String> = listOf(), // "image/jpeg", "image/png", "image/webp"
) : Serializable

data class ImageTransformation(
    val maxSidePx: Int = -1,
    val encodeToFormat: ImageFormat? = null,
) : Serializable {

    fun isNotEmpty() =
        maxSidePx > 0 || encodeToFormat != null
}

enum class ImageFormat(val ext: String) {
    JPEG("jpeg"),
    PNG("png"),
    WEBP("webp"),
}
