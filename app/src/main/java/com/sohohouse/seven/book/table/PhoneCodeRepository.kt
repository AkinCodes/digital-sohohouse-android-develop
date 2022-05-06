package com.sohohouse.seven.book.table

import android.content.res.Resources
import androidx.annotation.Keep
import com.google.gson.Gson
import com.sohohouse.seven.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader


class PhoneCodeRepository(private val resources: Resources) {

    suspend fun getPhoneCodes(): List<PhoneCode> {
        return withContext(Dispatchers.IO) {
            val input = resources.openRawResource(R.raw.phone_country_codes)
            val reader = BufferedReader(input.reader())
            val json = reader.readText()
            reader.close()
            val codes: List<PhoneCode> =
                Gson().fromJson(json, Array<PhoneCode>::class.java).toList()
            codes
        }
    }

}

@Keep
data class PhoneCode(val name: String = "", val dial_code: String = "", val code: String = "")