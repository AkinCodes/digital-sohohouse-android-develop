package com.sohohouse.seven.common.utils.encryption

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule
import org.junit.Test

class PGPTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val pgp: PublicKeyEncryptable = PGP()

    companion object {
        private val publicKey =
            "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
                    "Version: BCPG v@RELEASE_NAME@\n" +
                    "\n" +
                    "mQENBFuhCCIDCACMG6NCXtXXN6t/qzogdw7A5ZMaearwTFrWj45oMJsr2+QpUHy3\n" +
                    "cVMrD/21pKTn0rsgWNu3PSkbCP/NnsDuWI6zYS57rxzxdInZVgvWNHlliN+lGk5w\n" +
                    "AS3ayxRIhYCJNRWepQDeYq3IJSs6BNV7mGNbs1qhZk6ZWf6v9b2L+uG8MOHnnafQ\n" +
                    "yrERswv+k2l7aOLmJAYJG27mIRkF7Oo3sfgKVEPHIS/aiptZJo0fviAQv+CO6Bp7\n" +
                    "Hfs5fImATfdVkoeLcwaZhnqheEeSIL48FcELImwEZPSZOXeOcynEUxSgLzUWYlQr\n" +
                    "aezHA7rKzEJAWLwhfz2y3joDHIPIZjqYxjFXABEBAAG0E2phbi5yYWJlQGtpYm90\n" +
                    "dS5uZXSJAS4EEwMCABgFAluhCCICG4MECwkIBwYVCAIJCgsCHgEACgkQesP+mb4K\n" +
                    "ikKd/Af/cQJcaPI4hiZQaKYmva6HE5SRsTF9/iG4s9fQDgUbNLZb6mim2Z0weeSQ\n" +
                    "kDrYjsQ31ICTBbA/fk2H05/HolF/gI3ubLkH7/BGhS+S7j0E2QOtdhtwyscfBIYR\n" +
                    "Ie8qtUiI9qroqirmxoew1PjWp212KGJriJeSHiu5mGMV6QvE4seZNEKPdNBUwnkC\n" +
                    "7swaypeG3uBZjgbysrgGXHPPuhYz3sjDupIIoYP3gj7gNoXMfstCTcFHB7AR9BsB\n" +
                    "jTRrSgmUSCOOUIqIfC6iNLlsMPs2oUPYGb21/3GQwJTgTTCRT4oOk/Z/Hyi5bzL0\n" +
                    "xSh15tiyCBjYwU5iWaElIXmoqtfy27kBDQRboQgiAggAvjYntovKxt4u//ilSINn\n" +
                    "FDrAD6e8VPOcB+4YiJ5ajg5n8hycAvQK/ckVa754yhL5FmM3n+fYgnMbjwSezZfE\n" +
                    "ouyy/YXxElFbzHknSmL+Oz+ZYBRflp4Uj4PEzL793R2iXyE7x6V2erUakVUfH+8i\n" +
                    "LpzvOsSsMn/O8gd0xL4oct9c/klQgbWZEJxcXHWg6vWwI95FajDTa3g6EH7V0c93\n" +
                    "i638rpHJuw7uwvkJv8uggdx+Pw/ci3Y/Qx6ZcN2nexjgmV/aket0y5BqHKntXn72\n" +
                    "Xj4v5GFbPF7bLEj96fd6RlIbkxL2AyqCg+JjwGZ9d8xXoBWN2JEtyzZvjYHbf2my\n" +
                    "aQARAQABiQEfBBgDAgAJBQJboQgiAhsMAAoJEHrD/pm+CopC7a0H/0uYKBQkbjOo\n" +
                    "/VCKoYL7ZrgVMy2yPGYDX/SEw6KZNRs6yy0IncSvD/X8PXbyTVOJPnvN+aTNbZSQ\n" +
                    "M+k33QrwdCVXihZG+ycdqlVi+fBIbwSspdcZWo204kARmL0ABZ4zUlzbh4LTPvbV\n" +
                    "vZCUsZ7Es913ukn2Jli9kbjIqlElb21jUT4SU4E4h0pV3ACYrXHcmpwB10V7eexN\n" +
                    "YL8JTijJQd3BgbLMCQuQ2dmR2z++khgVfUpiakr8QolL9E5OqaBJwGWxFsjStzOp\n" +
                    "N0MWSdQ+YfeA/O13DiIGn28p5A/WYkEu1EkIltEIht/WVScQ85nGXoXoHQ7a6cPa\n" +
                    "jycAUbxoj6c=\n" +
                    "=AclD\n" +
                    "-----END PGP PUBLIC KEY BLOCK-----\n"
    }

    @Test
    fun `payload is not same as input`() {
        // GIVEN message and a public key
        val message = "this is the message"

        // WHEN PGP encryption is executed
        val payload = pgp.encrypt(message, publicKey).test()

        // THEN the output is not the same as the message
        payload.assertNoErrors()
        payload.assertValue {
            it != message
        }
    }

    @Test
    fun `bad public key returns an empty string`() {
        // GIVEN message and a public key
        val message = "this is the message"
        val badPublicKey = ""

        // WHEN PGP encryption is executed
        val payload = pgp.encrypt(message, badPublicKey).test()

        // THEN the output is not the same as the message
        payload.assertNoErrors()
        payload.assertValue("")
    }
}