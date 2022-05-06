package com.sohohouse.seven.network.chat.model

class ChannelOperationException(
    errorType: ChannelOperationErrorType,
    cause: Exception,
    errorCode: Int,
) : RuntimeException(
    "Channel operation:$errorType did not complete. error code $errorCode", cause
)

enum class ChannelOperationErrorType {
    SendMessageToBlockedReceiver,
    SendMessage,
    SendImageMessage,
    GetChannel,
    MarkAsRead,
    Mute,
    Delete,
    Create,
    Invite,
    GetMessages,
    AcceptInvite,
    DeclineInvite;
}