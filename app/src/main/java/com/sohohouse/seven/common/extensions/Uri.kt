package com.sohohouse.seven.common.extensions

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

fun Uri.baseUrl(): String {
    return Uri.Builder()
        .authority(this.authority)
        .scheme(this.scheme)
        .build()
        .toString()
}

@Throws(IOException::class)
fun Uri.getFile(context: Context): File? {
    val destinationFilename: File =
        File(context.filesDir.path + File.separatorChar + queryName(context, this))
    try {
        context.contentResolver.openInputStream(this)?.use { ins ->
            createFileFromStream(
                ins,
                destinationFilename
            )
        }
    } catch (ex: Exception) {
        Timber.tag("Save File").e(ex.message!!)
        return null
    }
    return destinationFilename
}

fun createFileFromStream(ins: InputStream, destination: File?) {
    try {
        FileOutputStream(destination).use { os ->
            val buffer = ByteArray(4096)
            var length: Int
            while (ins.read(buffer).also { length = it } > 0) {
                os.write(buffer, 0, length)
            }
            os.flush()
        }
    } catch (ex: Exception) {
        FirebaseCrashlytics.getInstance().recordException(ex)
        ex.printStackTrace()
    }
}

private fun queryName(context: Context, uri: Uri): String {
    val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
    val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name: String = returnCursor.getString(nameIndex)
    returnCursor.close()
    return name
}