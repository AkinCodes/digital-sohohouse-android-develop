package com.sohohouse.seven.connect.noticeboard.reactions

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.isSuccessful
import com.sohohouse.seven.network.core.models.Reaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeboardReactionIconsProvider @Inject constructor(
    private val api: SohoApiService,
    ioDispatcher: CoroutineDispatcher,
) {

    private val icons: MutableMap<Reaction, String> = mutableMapOf()
    private val scope = CoroutineScope(ioDispatcher)
    private val fetchJob = fetchIcons()

    fun fetchIcons() = scope.launch {
        val rawIcons = api.getNoticeboardIcons()

        if (rawIcons.isSuccessful()) {
            rawIcons.response.forEach {
                icons[Reaction.valueOf(it.id)] = it.iconUrl
            }
        } else {
            FirebaseCrashlytics.getInstance().recordException(
                IllegalStateException("Unable to get icons: " + rawIcons.code)
            )
        }
    }

    fun getNoticeBoardReaction(key: Reaction): NoticeBoardReaction {
        runBlocking { fetchJob.join() }
        return NoticeBoardReaction(key, icons[key] ?: "")
    }

    fun getAllReactions(): MutableMap<Reaction, String> {
        return icons
    }
}
