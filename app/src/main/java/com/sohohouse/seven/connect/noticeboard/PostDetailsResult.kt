package com.sohohouse.seven.connect.noticeboard

import java.io.Serializable

class PostDetailsResult(val postId: String, val action: Action) : Serializable {

    enum class Action {
        POST_UPDATED,
        POST_DELETED,
        TAG_SELECTED;
    }
}