@file:Suppress("unused")

package crocodile8.image_picker_plus

import android.app.Activity
import android.content.Intent
import android.os.Bundle

object ImagePickerPlus {

    /**
     * Can be used with ActivityResultContracts.StartActivityForResult.
     */
    fun createIntent(activity: Activity, request: PickRequest): Intent {
        val intent = Intent(activity, ImagePickerPlusActivity::class.java)
        intent.putExtras(createBundle(request))
        return intent
    }

    /**
     * Must be used with onActivityResult() override.
     */
    fun launch(activity: Activity, request: PickRequest, requestCode: Int = 1) {
        activity.startActivityForResult(createIntent(activity, request), requestCode)
    }

    private fun createBundle(request: PickRequest) =
        Bundle().apply {
            putSerializable(ImagePickerPlusActivity.REQUEST_SPEC, request)
        }
}
