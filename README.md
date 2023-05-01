# Image Picker+

Simple Photo picker for Android. Supports at least jpeg, png, webp formats.

### Some functions:  
✔️ Pick picture from gallery without storage permission.  
✔️ Take photo from camera (camera permission is optional).  
✔️ Rotate or crop the captured image.  
✔️ Save final image to app's local directory so you can use it like a File, without content resolver.  
✔️ Transform result to Jpeg / PNG / WebP automatically if needed.  

### Why one more photo picker?
1. Photopicker from Google is not good enough.
2. Single entry point for camera/gallery images.
3. Other libraries that I've tried have bugs and/or abandoned.

### Why not photopicker from Google (https://developer.android.com/training/data-storage/shared/photopicker)?
This library:
1. Doesn't require Android 11 or Google Play services as photopicker from Google do.
2. Covers both gallery and camera sources of image.
3. Has basic image editor (rotate / crop)

### Why this library doesn't require storage permissions?
It doesn't have access to the file system of device. The system component provides content to this lib instead.
See Intent.ACTION_OPEN_DOCUMENT for more details (https://developer.android.com/training/data-storage/shared/documents-files).

### Why camera permission is optional?
It vaguely described in the documentation (https://developer.android.com/reference/android/provider/MediaStore#ACTION_IMAGE_CAPTURE). 
Unless you declare the camera permission in you app's manifest, you don't need to ask for runtime permission for this library.
If you declare the camera permission (for other features in your app), this lib will ask for runtime permission automatically.

### How rotation / crop works?
Thanks to uCrop library: https://github.com/Yalantis/uCrop

### Usage:
For instance in you activity:
```kotlin
// You can use the modern way to receive the result like this. Or you can use onActivityResult().
private val launcher = registerForActivityResult(StartActivityForResult()) {
    it?.data?.data?.let { uri ->
        // Done. Use the received uri with captured image inside.
        imageView.setImageURI(uri)
    }
}

ImagePickerPlus
    .createIntent(
        activity = this,
        PickRequest(
            source = PickSource.GALLERY, // or PickSource.CAMERA
            transformation = ImageTransformation(
                maxSidePx = 1024, // Or -1 if limit isn't needed
                encodeToFormat = ImageFormat.JPEG, // Or null is transformation isn't needed
            ),
        )
    )
    .let { launcher.launch(it) }
```

## License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.