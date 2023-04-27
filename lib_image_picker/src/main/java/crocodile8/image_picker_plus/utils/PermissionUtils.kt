package crocodile8.image_picker_plus.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

internal fun Context.isCameraPermissionGranted(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

/**
 * https://developer.android.com/reference/android/provider/MediaStore#ACTION_IMAGE_CAPTURE
 *
 * Quotation:
 * if you app targets M and above and declares as using the Manifest.permission.CAMERA permission
 * which is not granted, then attempting to use this action will result in a SecurityException.
 */
internal fun Context.isCameraPermissionDeclared(): Boolean =
    isPermissionDeclared(Manifest.permission.CAMERA)

internal fun Context.isPermissionDeclared(permission: String): Boolean {
    try {
        @Suppress("DEPRECATION")
        val info: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        if (info.requestedPermissions != null) {
            for (p in info.requestedPermissions) {
                if (p == permission) {
                    return true
                }
            }
        }
    } catch (e: Exception) {
        Logger.e("", e)
    }
    return false
}

internal fun Activity.launchAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}
