package com.sohohouse.seven.connect.message.chat.content.menu.attach

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.base.BaseFragment
import timber.log.Timber
import java.io.File
import java.io.IOException

class TakeImageManager(val activity: AppCompatActivity) {
    companion object {
        const val REQUEST_CODE_CAMERA = 10201
        const val REQUEST_CODE_LIBRARY = 10301
    }

    var onFileCreateError: ((msg: String?) -> Unit)? = null

    var cameraMode = ""
    var file: File? = null
    var fileUri: Uri? = null

    fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun pickCamera(from: BaseFragment) {
        if (checkCameraPermission()) {
            val intent = getCameraIntent()
            intent?.let {
                if (it.resolveActivity(activity.packageManager) != null
                    || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                ) {
                    from.startActivityForResult(intent, REQUEST_CODE_CAMERA)
                } else
                    Timber.w("pickCamera: No Camera found!")
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis().toString()
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun getCameraIntent(): Intent? {
        return if (cameraMode == AttachFileBottomSheet.ATTACH_TYPE_CAMERA_VIDEO) {
            Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        } else {
            file = try {
                createImageFile()
            } catch (ex: IOException) {
                FirebaseCrashlytics.getInstance().recordException(ex)
                onFileCreateError?.invoke(ex.message)
                null
            }
            file?.let {
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
    }

    fun openMediaLibrary(from: BaseFragment) {
        from.startActivityForResult(getGalleryIntent(), REQUEST_CODE_LIBRARY)
    }

    private fun getGalleryIntent(): Intent {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "*/*"
        val mimetypes = arrayOf("image/*", "video/*")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return Intent.createChooser(intent, null)
    }

    fun deleteFile() {
        file?.delete()
    }

}