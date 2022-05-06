package com.sohohouse.seven.profile

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.sohohouse.seven.R
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class SocialMediaItem constructor(
    val type: Type,
    private var _url: String?,
    private var _handle: String? = null
) : Errorable, Serializable, Parcelable {

    val hasValue: Boolean
        get() = !url.isNullOrBlank() || !handle.isNullOrBlank()
    val icon = type.iconResId
    val name = type.title

    var url
        get() = _url
        set(value) {
            _url = value
            _handle = null
        }

    var handle
        get() = _handle
        set(value) {
            _handle = value
            _url = null
        }

    override val errors = HashSet<Error>()

    enum class Type(@DrawableRes val iconResId: Int, val title: String) {
        SPOTIFY(R.drawable.ic_spotify, "Spotify"),
        YOUTUBE(R.drawable.ic_youtube, "Youtube"),
        LINKEDIN(R.drawable.ic_linkedin, "LinkedIn"),
        TWITTER(R.drawable.ic_twitter, "Twitter"),
        INSTAGRAM(R.drawable.ic_instagram, "Instagram"),
        WEBSITE(R.drawable.ic_website, "Website")
    }

    companion object {
        val ERROR_CODES_MAP = mapOf(
            "INVALID_WEBSITE" to Type.WEBSITE,
            "INVALID_LINKEDIN_URL" to Type.LINKEDIN,
            "INVALID_SPOTIFY_URL" to Type.SPOTIFY,
            "INVALID_YOUTUBE_URL" to Type.YOUTUBE,
            "WEBSITE_TOO_LONG" to Type.WEBSITE
        )
    }
}