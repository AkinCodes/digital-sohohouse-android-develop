package com.sohohouse.seven.common.utils.encryption

import io.reactivex.Single
import org.bouncycastle.bcpg.ArmoredInputStream
import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.*
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.SecureRandom
import java.security.Security
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Based on
 * https://github.com/kibotu/Android-PGP/blob/master/android-pgp/src/main/java/net/kibotu/pgp/Pgp.kt
 */
@Singleton
class PGP @Inject constructor() : PublicKeyEncryptable {
    companion object {
        private const val ERROR_PAYLOAD = ""
    }

    private val bcKeyFingerprintCalculator = BcKeyFingerprintCalculator()

    init {
        // added because of:
        // 1. https://stackoverflow.com/a/46857694
        // 2. http://www.oracle.com/technetwork/java/javase/8u151-relnotes-3850493.html
        // You may have to manually execute setProperty on app launch
        Security.setProperty("crypto.policy", "unlimited")

        Security.addProvider(BouncyCastleProvider())
    }

    @Throws(IOException::class)
    private fun pgpPublicKeyRingFun(publicKey: ByteArray): PGPPublicKeyRing {
        val ais = ArmoredInputStream(ByteArrayInputStream(publicKey))
        val pgpObjectFactory = PGPObjectFactory(ais, bcKeyFingerprintCalculator)
        return pgpObjectFactory.nextObject() as PGPPublicKeyRing
    }

    private fun getPublicKey(publicKeyRing: PGPPublicKeyRing): PGPPublicKey? {
        val kIt = publicKeyRing.publicKeys
        while (kIt.hasNext()) {
            val k = kIt.next() as PGPPublicKey
            if (k.isEncryptionKey) {
                return k
            }
        }
        return null
    }

    /**
     * Spits out an encrypted version of the msg
     *
     * @param msg Plain message.
     * @param publicKey the public pgp key
     * @return PGP encrypted message.
     */
    @Throws(IOException::class, PGPException::class)
    private fun encrypt(msg: ByteArray, publicKey: String): ByteArray? {
        val encKey = getPublicKey(pgpPublicKeyRingFun(publicKey.toByteArray()))
        val encOut = ByteArrayOutputStream()
        val out = ArmoredOutputStream(encOut)
        val bOut = ByteArrayOutputStream()
        val comData = PGPCompressedDataGenerator(PGPCompressedDataGenerator.ZIP)
        val cos = comData.open(bOut)
        val lData = PGPLiteralDataGenerator()
        val pOut = lData.open(
            cos,
            PGPLiteralData.BINARY,
            PGPLiteralData.CONSOLE,
            msg.size.toLong(),
            Date()
        )
        pOut.write(msg)
        lData.close()
        comData.close()
        val encGen = PGPEncryptedDataGenerator(
            JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256)
                .setWithIntegrityPacket(true)
                .setSecureRandom(SecureRandom())
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
        )
        if (encKey != null) {
            encGen.addMethod(
                JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider(
                    BouncyCastleProvider.PROVIDER_NAME
                )
            )
            val bytes = bOut.toByteArray()
            val cOut = encGen.open(out, bytes.size.toLong())
            cOut.write(bytes)
            cOut.close()
        }
        out.close()
        return encOut.toByteArray()
    }

    //region PublicKeyEncryptable
    /**
     * Spits out an encrypted version of the msg
     *
     * @param msgText Plain message.
     * @param publicKey the public pgp key
     * @return PGP encrypted message.
     */
    @Throws(IOException::class, PGPException::class)
    override fun encrypt(msgText: String, publicKey: String): Single<String> {
        return Single.fromCallable {
            encrypt(msgText.toByteArray(), publicKey)?.let { String(it) } ?: ERROR_PAYLOAD
        }.onErrorReturn { ERROR_PAYLOAD }
    }
    //endregion
}