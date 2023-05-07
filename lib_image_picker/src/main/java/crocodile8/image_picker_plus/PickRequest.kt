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
    /** Only image/ is supported at the moment, don't change default value */
    val mimeType: String = "image/*",
    /** "image/jpeg", "image/png", "image/webp" */
    val subTypes: List<String> = listOf("image/jpeg", "image/png", "image/webp"),
) : Serializable

data class ImageTransformation(
    val maxSidePx: Int = -1,
    val encodeToFormat: ImageFormat? = null,
) : Serializable

enum class ImageFormat(val ext: String) {
    JPEG("jpeg"),
    PNG("png"),
    WEBP("webp"),
}
