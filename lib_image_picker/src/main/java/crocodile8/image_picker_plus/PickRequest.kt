package crocodile8.image_picker_plus

import java.io.Serializable

data class PickRequest(
    val modes: List<PickMode>,
) : Serializable

enum class PickMode {
    GALLERY,
    CAMERA,
}
