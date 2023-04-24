package crocodile8.image_picker_plus

import java.io.Serializable

data class PickRequest(
    val source: PickSource,
    val filter: TypeFilter = TypeFilter(),
    val maxSidePx: Int = -1,
    val useCrop: Boolean = true,
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
