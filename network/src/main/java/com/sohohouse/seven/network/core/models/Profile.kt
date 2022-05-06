package com.sohohouse.seven.network.core.models

import com.sohohouse.seven.network.core.common.extensions.nullIfEmpty
import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasMany
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource
import java.io.Serializable
import java.util.*

@JsonApi(type = "profiles")
data class Profile(
    @field:Json(name = "title") private var _title: String? = "",
    @field:Json(name = "first_name") private var _firstName: String? = "",
    @field:Json(name = "last_name") private var _lastName: String? = "",
    @field:Json(name = "image_url") private var _imageUrl: String? = "",
    @field:Json(name = "is_staff") private var _isStaff: Boolean? = false,
    @field:Json(name = "account") private var _account: HasOne<Account>? = HasOne(),
    @field:Json(name = "occupation") var occupation: String? = "",
    @field:Json(name = "nature_of_business") var industry: String? = "",
    @field:Json(name = "ask_me_about") var askMeAbout: String? = "",
    @field:Json(name = "interests") var interestsResource: HasMany<Interest>? = HasMany(),
    @field:Json(name = "instagram_handle") var instagramHandle: String? = "",
    @field:Json(name = "twitter_handle") var twitterHandle: String? = "",
    @field:Json(name = "linkedin_handle") var linkedInHandle: String? = "",
    @field:Json(name = "spotify_handle") var spotifyHandle: String? = "",
    @field:Json(name = "youtube_handle") var youtubeHandle: String? = "",
    @field:Json(name = "instagram_url") var instagramUrl: String? = "",
    @field:Json(name = "twitter_url") var twitterUrl: String? = "",
    @field:Json(name = "linkedin_url") var linkedInUrl: String? = "",
    @field:Json(name = "spotify_url") var spotifyUrl: String? = "",
    @field:Json(name = "youtube_url") var youtubeUrl: String? = "",
    @field:Json(name = "website") var website: String? = "",
    @field:Json(name = "bio") var bio: String? = "",
    @field:Json(name = "city") var city: String? = "",
    @field:Json(name = "confirmed_at") var confirmedAt: Date? = null,
    @field:Json(name = "share_social_media_opt_in") var socialsOptIn: Boolean = false,
    @field:Json(name = "bio_question") var bioQuestion: String? = "",
    @field:Json(name = "connections") private var _connections: HasMany<Connections>? = null,
    @field:Json(name = "mutual_connections") private var _mutualConnections: HasMany<MutualConnections>? = null,
    @field:Json(name = "mutual_connection_requests") private var _mutualConnectionRequest: HasMany<MutualConnectionRequests>? = null,
    @field:Json(name = "gender_pronouns") var pronouns: List<String> = emptyList(),
    @field:Json(name = "connect_recommendation_opt_in") var connectRecommendationOptIn: String? = "",
) : Resource(), Serializable {

    val title: String
        get() = _title ?: ""
    val firstName: String
        get() = _firstName ?: ""
    val lastName: String
        get() = _lastName ?: ""
    var imageUrl: String
        get() = _imageUrl ?: ""
        set(value) {
            _imageUrl = value
        }
    val isStaff: Boolean
        get() = _isStaff ?: false

    val account: Account?
        get() = _account?.get(document)

    val interests: List<Interest>?
        get() = interestsResource?.get(document)?.nullIfEmpty()


    val connections: List<Connection>
        get() = _connections?.get(document)?.filterNotNull() ?: emptyList()

    val mutualConnections: List<MutualConnections>
        get() = _mutualConnections?.get(document)?.filterNotNull() ?: emptyList()

    val mutualConnectionRequest: List<MutualConnectionRequests>
        get() = _mutualConnectionRequest?.get(document)?.filterNotNull() ?: emptyList()
}