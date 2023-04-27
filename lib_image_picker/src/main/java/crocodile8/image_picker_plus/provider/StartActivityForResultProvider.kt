package crocodile8.image_picker_plus.provider

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import crocodile8.image_picker_plus.PickRequest

abstract class StartActivityForResultProvider(
    activity: ComponentActivity,
    protected val request: PickRequest,
    private val onResult: (Uri?, Throwable?) -> Unit,
) {

    protected val context: Context = activity.applicationContext
    protected val packageManager: PackageManager = context.packageManager

    protected val launcher = activity.registerForActivityResult(StartActivityForResult()) {
        onResult(it.resultCode, it.data)
    }

    abstract fun onCreate(savedInstanceState: Bundle?)

    open fun onResume() {}

    open fun onSaveInstanceState(outState: Bundle) {}

    open fun onRestoreInstanceState(savedInstanceState: Bundle) {}

    protected abstract fun onResult(resultCode: Int, intent: Intent?)

    protected fun onSuccess(uri: Uri) {
        onResult(uri, null)
    }

    protected fun onError(throwable: Throwable? = null) {
        onResult(null, throwable)
    }
}
