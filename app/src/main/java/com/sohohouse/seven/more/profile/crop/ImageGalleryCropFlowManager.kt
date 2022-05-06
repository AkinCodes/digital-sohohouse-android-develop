package com.sohohouse.seven.more.profile.crop

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.base.BaseActivity
import com.sohohouse.seven.base.error.ErrorDialogHelper
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.common.extensions.hasCameraPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.IOException

class ImageGalleryCropFlowManager(
    private val registry: ActivityResultRegistry? = null
) : DefaultLifecycleObserver {

    private var forResultLauncher: ActivityResultLauncher<Intent>? = null
    private var requestCameraPermissions: ActivityResultLauncher<Array<String>>? = null

    private val _cropManagerEvent = MutableStateFlow<Event?>(null)
    val cropManagerEvent = _cropManagerEvent.asStateFlow()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        forResultLauncher = registry?.register(
            "forResultLauncher",
            owner,
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                navigateFromImageChooseResult(
                    result.resultCode,
                    result.data
                )
        }
        requestCameraPermissions = registry?.register(
            "requestCameraPermissions",
            owner,
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (it[Manifest.permission.CAMERA] == true)
                _cropManagerEvent.value = Event.PermissionGranted
        }
    }

    var file: File? = null
    var fileUri: Uri? = null

    private val permissionsForCamera = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun navigateFromImageChooseResult(
        resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK) {
            val galleryPictureUri = data?.data ?: fileUri
            galleryPictureUri?.let {
                _cropManagerEvent.value = Event.ImageChosen(it)
            }
        } else {
            file?.delete()
        }
    }

    fun onGallerySelected() {
        forResultLauncher?.launch(createGalleryIntent())
    }

    fun onCameraSelected(activity: AppCompatActivity) {
        _cropManagerEvent.value = null
        if (activity.hasCameraPermission())
            createCameraIntent(activity)?.let { camIntent ->
                forResultLauncher?.launch(camIntent)
            }
        else
            requestCameraPermissions?.launch(permissionsForCamera)
    }

    fun createImageCropperIntent(activity: AppCompatActivity, imageUri: Uri): Intent =
        Intent(activity, ProfileImageCropActivity::class.java).apply {
            putExtra(ProfileImageCropActivity.EXTRA_UNCROPPED_IMAGE_FILE, imageUri)
        }.also {
            _cropManagerEvent.value = null
        }

    private fun createGalleryIntent(): Intent {
        return Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            addCategory(Intent.CATEGORY_OPENABLE)
        }
    }

    private fun createCameraIntent(activity: AppCompatActivity): Intent? {
        file = try {
            createImageFile(activity)
        } catch (ex: IOException) {
            ErrorDialogHelper.showErrorDialogByErrorCode(
                activity,
                arrayOf(ErrorHelper.FILE_CREATE_ERROR)
            )
            FirebaseCrashlytics.getInstance().recordException(ex)
            null
        }
        return file?.let {
            fileUri = FileProvider.getUriForFile(
                activity,
                "com.sohohouse.seven.fileprovider",
                it
            )
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(activity: AppCompatActivity): File {
        val timeStamp = System.currentTimeMillis().toString()
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}

sealed class Event {
    object PermissionGranted : Event()
    class ImageChosen(val imageUri: Uri) : Event()
}