package com.sohohouse.seven.common.deeplink

import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DeeplinkRepoImpl : DeeplinkRepo {

    private var deeplink = MutableLiveData(Uri.EMPTY)

    override fun get(): LiveData<Uri> = deeplink

    override fun put(uri: Uri?, redirect: Boolean) {
        CoroutineScope(Dispatchers.Default).launch {
            val url = (uri ?: Uri.EMPTY).toString()
            deeplink.postValue(if (redirect) redirectUrl(url) else Uri.parse(url))
        }
    }

    override fun delete() {
        deeplink.postValue(Uri.EMPTY)
    }

    private fun redirectUrl(url: String): Uri {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = false

            when (connection.responseCode) {
                HttpURLConnection.HTTP_OK,
                HttpURLConnection.HTTP_MOVED_PERM,
                HttpURLConnection.HTTP_MOVED_TEMP -> getDeepLink(connection.inputStream)
                else -> Uri.EMPTY
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Uri.EMPTY
        }
    }

    private fun getDeepLink(inputStream: InputStream): Uri {
        val response = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
        val matcher = Patterns.WEB_URL.matcher(response)
        if (matcher.find()) {
            return Uri.parse(matcher.group()).buildUpon()
                .scheme(DeeplinkBuilder.APPS_SCHEME)
                .clearQuery()
                .build()
        }
        return Uri.EMPTY
    }
}