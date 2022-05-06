package com.sohohouse.seven.video

import java.util.regex.Pattern

class YouTubePlayerHelper {
    companion object {

        const val YOUTUBE_THUMBNAIL_URL = "https://img.youtube.com/vi/{videoId}/0.jpg"

        fun parseVideoIdFromYouTube(url: String): String {
            val pattern =
                "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|\u0026v=|youtu.be%2F|%2Fv%2F)[^#&?\\n]*"
            val compiledPattern = Pattern.compile(pattern)
            val matcher = compiledPattern.matcher(url)
            return if (matcher.find()) matcher.group() else url
        }
    }
}