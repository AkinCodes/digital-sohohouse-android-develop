package com.sohohouse.seven.connect.mynetwork

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.chat.invite.SentMessageRequest
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.models.*
import com.sohohouse.seven.network.core.request.*
import kotlinx.coroutines.flow.*

interface ConnectionRepository {

    val numberOfConnectRequests: Flow<Int>

    val blockedMembers: MutableStateFlow<List<String>>

    val totalConnections: Flow<Int>

    suspend fun getBlockedMembers(): Either<ServerError, BlockedMemberList>

    suspend fun patchUnblockMember(id: String): Either<ServerError, BlockedMemberList>

    suspend fun patchBlockMember(id: String): Either<ServerError, BlockedMemberList>

    suspend fun getConnectionRequests(
        page: Int,
        perPage: Int = ITEMS_PER_PAGE
    ): Either<ServerError, List<MutualConnectionRequests>>

    suspend fun getConnections(
        page: Int,
        perPage: Int = ITEMS_PER_PAGE
    ): Either<ServerError, List<MutualConnections>>

    suspend fun postMessageRequest(
        channelUrl: String,
        receiverIds: List<String>
    ): Either<ServerError, Unit>

    suspend fun postConnectionRequest(request: MutualConnectionRequests): Either<ServerError, MutualConnectionRequests>

    suspend fun patchConnectionRequest(request: MutualConnectionRequests): Either<ServerError, MutualConnectionRequests>

    suspend fun deleteMutualConnection(id: String): Either<ServerError, Void>

    suspend fun postReportMember(id: String, message: String): Either<ServerError, Void>

    companion object {
        const val ITEMS_PER_PAGE = 20
    }

}

class ConnectionRepositoryImpl(
    private val coreRequestFactory: CoreRequestFactory
) : ConnectionRepository {

    override val numberOfConnectRequests: MutableStateFlow<Int> = MutableStateFlow(0)

    override var blockedMembers: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    override val totalConnections = MutableStateFlow(0)

    override suspend fun getConnections(
        page: Int,
        perPage: Int
    ): Either<ServerError, List<MutualConnections>> {
        val result = coreRequestFactory.createV2(GetConnections(page, perPage))
        result.ifValue { response ->
            GetConnections.getMeta(response)?.totalItems?.let { totalItems ->
                totalConnections.emit(totalItems)
            }
        }
        return result
    }

    override suspend fun postMessageRequest(
        channelUrl: String,
        receiverIds: List<String>
    ): Either<ServerError, Unit> {
        return coreRequestFactory.createV2(
            PostMessageRequest(SentMessageRequest(channelUrl, receiverIds))
        ).fold(
            ifValue = { value(Unit) },
            ifEmpty = { empty() },
            ifError = { Either.Error(it) }
        )
    }

    override suspend fun getConnectionRequests(
        page: Int,
        perPage: Int
    ): Either<ServerError, List<MutualConnectionRequests>> {
        return coreRequestFactory.createV2(GetConnectionRequests(page, perPage)).ifValue {
            val meta = GetConnectionRequests.getMeta(it)
            val totalItems = if (page == 1 && meta == null) 0 else meta?.totalItems
                ?: return@ifValue
            numberOfConnectRequests.emit(totalItems)
        }
    }

    override suspend fun postConnectionRequest(request: MutualConnectionRequests): Either<ServerError, MutualConnectionRequests> {
        return coreRequestFactory.createV2(PostConnectionRequest(request))
    }

    override suspend fun patchConnectionRequest(request: MutualConnectionRequests): Either<ServerError, MutualConnectionRequests> {
        return coreRequestFactory.createV2(PatchAcceptConnectionRequest(request))
    }

    override suspend fun deleteMutualConnection(id: String): Either<ServerError, Void> {
        return coreRequestFactory.createV2(DeleteMutualConnection(id))
    }

    override suspend fun postReportMember(id: String, message: String): Either<ServerError, Void> {
        return coreRequestFactory.createV2(PostReportMember(message, id))
    }

    override suspend fun getBlockedMembers(): Either<ServerError, BlockedMemberList> {
        return coreRequestFactory.createV2(GetBlockedMembers).ifValue {
            blockedMembers.emit(it.blockedMembers ?: emptyList())
        }
    }

    override suspend fun patchBlockMember(id: String): Either<ServerError, BlockedMemberList> {
        return coreRequestFactory.createV2(PatchBlockMember(id)).ifValue { getBlockedMembers() }
    }

    override suspend fun patchUnblockMember(id: String): Either<ServerError, BlockedMemberList> {
        return coreRequestFactory.createV2(PatchUnblockMember(id)).ifValue { getBlockedMembers() }
    }

}