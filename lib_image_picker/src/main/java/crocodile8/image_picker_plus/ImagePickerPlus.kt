package crocodile8.image_picker_plus

import android.app.Activity
import android.content.Intent
import android.os.Bundle

object ImagePickerPlus {

    private const val REQUEST_CODE = 801

    fun launch(activity: Activity, request: PickRequest) {
        val intent = Intent(activity, ImagePickerPlusActivity::class.java)
        intent.putExtras(createBundle(request))
        activity.startActivityForResult(intent, REQUEST_CODE)
    }

    private fun createBundle(request: PickRequest) =
        Bundle().apply {
            putSerializable(ImagePickerPlusActivity.REQUEST_SPEC, request)
        }
}
