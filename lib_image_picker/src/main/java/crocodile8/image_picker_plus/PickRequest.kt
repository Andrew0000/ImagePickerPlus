package crocodile8.image_picker_plus

import java.io.Serializable

data class PickRequest(
    val sources: List<PickSource>,
    val size: PickSize? = null,
    val useCrop: Boolean = true,
) : Serializable

enum class PickSource {
    GALLERY,
    CAMERA,
}

data class PickSize(
    val maxSidePx: Int,
    val maxWeightBytes: Int,
) : Serializable
