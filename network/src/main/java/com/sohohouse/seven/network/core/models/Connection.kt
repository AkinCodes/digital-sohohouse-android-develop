package com.sohohouse.seven.network.core.models

import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.Resource
import java.io.Serializable
import java.util.*

abstract class Connection : Resource(), Serializable {
    abstract val message: String?
    abstract val createdAt: Date?
    abstract val state: String?
    abstract val sender: HasOne<Profile>
    abstract val receiver: HasOne<Profile>

    companion object {
        fun getValidConnectionId(myId: String, connection: Connection): String? {
            if (connection.receiver.get().id == myId || connection.sender.get().id == myId) {
                return connection.id
            }
            return null
        }
    }
}