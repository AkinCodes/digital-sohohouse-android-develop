package com.sohohouse.seven.common.prefs

import android.content.res.AssetManager
import com.sohohouse.seven.network.core.models.Venue
import com.squareup.moshi.*
import moe.banana.jsonapi2.*
import java.io.File
import moe.banana.jsonapi2.ArrayDocument

interface VenueCache {

    fun read(): List<Venue>
    fun write(venues: List<Venue>)
    fun clear()

    class Impl(
        private val fileName: String,
        private val assetsFileName: String,
        private val cacheDir: File,
        private val moshi: Moshi,
        private val fileCreator: FileCreator,
        private val assets: AssetManager
    ) : VenueCache {

        private val cachedFile: File
            get() {
                val cachedFile = fileCreator.file(cacheDir, fileName)
                if (!cachedFile.exists()) cachedFile.createNewFile()
                return cachedFile
            }

        init {
            readFromAssets()
        }

        //TODO consider executing this on IO thread.
        private fun readFromAssets() {
            if (read().isEmpty())
                assets.open(assetsFileName).use {
                    val formArray = ByteArray(it.available())
                    it.read(formArray)
                    cachedFile.writeBytes(formArray)
                }
        }

        override fun read(): List<Venue> {
            val json = cachedFile.readText()
            return if (json.isNotEmpty())
                moshi.adapter(Document::class.java).fromJson(json)?.asArrayDocument<Venue>()
                    ?.toList() ?: emptyList()
            else
                emptyList()
        }

        override fun write(venues: List<Venue>) {
            clear()
            val json = moshi.adapter(Document::class.java)
                .toJson(venues.firstOrNull()?.document ?: ArrayDocument<Venue>())
            cachedFile.writeText(json)
        }

        override fun clear() {
            val cacheFile = fileCreator.file(cacheDir, fileName)
            cacheFile.delete()
        }

    }
}
