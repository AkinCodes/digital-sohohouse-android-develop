package com.sohohouse.seven.connect.noticeboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.network.core.models.Reaction
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout

class NoticeboardPostReactionsTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val globalTimeout: Timeout = Timeout.seconds(10)

    @Test
    fun addAndRemoveReactionsFromPostThatHasNotBeenReacted() {
        val post = NoticeboardPost(
            postId = "postID",
            profile = ProfileItem(),
            timeStamp = "2H",
            messageContent = "message",
            showDeleteBtn = false,
        )

        val post2 = post.addReaction(Reaction.CELEBRATE)

        assert(post2.currentUserReaction == Reaction.CELEBRATE)
        assert(post2.reactions[Reaction.CELEBRATE] ?: 0 >= 1)

        val post3 = post2.removeUserReaction()

        assert(post3.currentUserReaction == null)
        assert(post3.reactions[Reaction.CELEBRATE] == null)
    }

    @Test
    fun addAndRemoveReactionsFromPostThatHasBeenReacted() {
        val post = NoticeboardPost(
            postId = "postID",
            profile = ProfileItem(),
            timeStamp = "2H",
            messageContent = "message",
            showDeleteBtn = false,
            currentUserReaction = Reaction.CELEBRATE,
            reactions = mapOf(Reaction.CELEBRATE to 4, Reaction.HEART to 22)
        )

        val post2 = post.addReaction(Reaction.CELEBRATE)

        assert(post2.currentUserReaction == Reaction.CELEBRATE)
        assert(post2.reactions[Reaction.CELEBRATE] == 4)

        val post3 = post2.removeUserReaction()

        assert(post3.currentUserReaction == null)
        assert(post3.reactions[Reaction.CELEBRATE] == 3)
    }
}