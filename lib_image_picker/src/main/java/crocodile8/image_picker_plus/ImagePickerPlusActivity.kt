package crocodile8.image_picker_plus

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crocodile8.image_picker_plus.processor.PostProcessor
import crocodile8.image_picker_plus.provider.CameraProvider
import crocodile8.image_picker_plus.provider.GalleryProvider
import crocodile8.image_picker_plus.provider.Provider
import crocodile8.image_picker_plus.utils.Logger
import crocodile8.image_picker_plus.utils.FileUtils

internal class ImagePickerPlusActivity : AppCompatActivity() {

    private val request: PickRequest by lazy {
        @Suppress("DEPRECATION")
        intent?.getSerializableExtra(REQUEST_SPEC) as PickRequest
    }

    private lateinit var provider: Provider

    private lateinit var postProcessor: PostProcessor

    private var postProcessed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.i("ImagePickerPlusActivity onCreate request: $request")

        savedInstanceState?.getBoolean(SAVED_POST_PROCESSED)?.let { postProcessed = it }
        Logger.i("ImagePickerPlusActivity onCreate, postProcessed: $postProcessed")

        val launchedBefore = savedInstanceState != null // if activity recreation
        if (!launchedBefore && request.clearPreviousCache) {
            FileUtils.clearTmpDir(applicationContext)
        }

        provider = getProvider()
        provider.onCreate(savedInstanceState)
        postProcessor = PostProcessor(this, request) {
            postProcessed = true
            routeResult(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_POST_PROCESSED, postProcessed)
        Logger.i("onSaveInstanceState, postProcessed: $postProcessed")
        provider.onSaveInstanceState(outState)
    }

    private fun routeResult(uri: Uri?) {
        when {
            uri != null && !postProcessed -> {
                postProcessor.launch(uri)
            }
            else -> {
                finishWithResult(uri)
            }
        }
    }

    private fun finishWithResult(uri: Uri?) {
        if (uri == null) {
            finishAsCancelled()
        } else {
            finishAsOK(uri)
        }
    }

    private fun finishAsCancelled() {
        val intent = Intent()
        intent.putExtra("error", "Error")
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    private fun finishAsOK(uri: Uri) {
        val intent = Intent()
        intent.data = uri
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun getProvider() = when (request.source) {
        PickSource.GALLERY -> {
            GalleryProvider(this, request) { uri, _ ->
                routeResult(uri)
            }
        }
        PickSource.CAMERA -> {
            CameraProvider(this, request) { uri, _ ->
                routeResult(uri)
            }
        }
    }

    companion object {
        const val REQUEST_SPEC = "request_spec"
        private const val SAVED_POST_PROCESSED = "camera_picker_activity_saved_post_processed"
    }
}