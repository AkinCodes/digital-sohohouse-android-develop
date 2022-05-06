package com.sohohouse.seven.profile

import android.content.res.Resources
import java.io.Serializable
import kotlin.collections.HashSet

interface Errorable {

    val errors: HashSet<Error>

    fun getConcatenatedErrorMessages(resources: Resources): String {
        return errors.concatenateMsg(resources)
    }

    val hasError get() = errors.isNotEmpty()

    fun addError(error: Error) {
        errors.add(error)
    }

    fun clearErrors() {
        errors.clear()
    }

}

data class Error(val messageRes: Int?, val errorCode: String) : Serializable

fun Collection<Error>.concatenateMsg(resources: Resources): String {
    return StringBuilder().apply {
        this@concatenateMsg.forEach {
            it.messageRes?.let { messageRes ->
                append("${resources.getString(messageRes)}\n")
            }
        }
    }.toString()
}