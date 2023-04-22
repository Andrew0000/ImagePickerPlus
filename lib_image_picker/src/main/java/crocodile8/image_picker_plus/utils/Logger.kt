package crocodile8.image_picker_plus.utils

import android.util.Log

object Logger {

    var enabled = false

    private const val TAG = "IPP"

    fun d(text: String) {
        if (enabled) Log.d(TAG, text)
    }

    fun i(text: String) {
        if (enabled) Log.i(TAG, text)
    }

    fun e(text: String, throwable: Throwable? = null) {
        if (enabled) Log.e(TAG, text, throwable)
    }
}
