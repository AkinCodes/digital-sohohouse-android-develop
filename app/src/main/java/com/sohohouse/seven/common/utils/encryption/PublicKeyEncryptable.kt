package com.sohohouse.seven.common.utils.encryption

import io.reactivex.Single

interface PublicKeyEncryptable {
    /**
     * Encrypt a message with the public key
     *
     * @param msgText plain message
     * @param publicKey the public key used to encrypt the message
     *
     * @return on failure, null.  On success the encrypted string.
     */
    fun encrypt(msgText: String, publicKey: String): Single<String>
}