package crocodile8.image_picker_plus

import java.io.Serializable

data class PickRequest(
    val source: PickSource,
    val mimeTypes: List<String> = listOf("image/*"),
    val maxSidePx: Int = -1,
    val useCrop: Boolean = true,
    val clearPreviousCache: Boolean = true,
) : Serializable

enum class PickSource {
    GALLERY,
    CAMERA,
}
