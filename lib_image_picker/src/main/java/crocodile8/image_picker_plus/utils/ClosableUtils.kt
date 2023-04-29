package crocodile8.image_picker_plus.utils

import java.io.Closeable

fun Closeable.closeSilent() =
    try {
        close()
    } catch (e: Exception) {
        // Ignore
    }
