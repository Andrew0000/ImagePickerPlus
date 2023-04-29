package crocodile8.image_picker_plus.utils

import android.os.Build
import androidx.activity.ComponentActivity

fun ComponentActivity.shouldRequestRationale(permission: String): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        shouldShowRequestPermissionRationale(permission)
    } else {
        false
    }
