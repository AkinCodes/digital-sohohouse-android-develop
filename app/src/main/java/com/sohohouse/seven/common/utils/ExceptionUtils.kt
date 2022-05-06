/*
  Copyright (C) SYMBILITY SOLUTIONS INC. - All Rights Reserved
  Unauthorized copying of this file, via any medium is strictly prohibited
  This content is proprietary and confidential
 */
package com.sohohouse.seven.common.utils

import com.sohohouse.seven.BuildConfig
import java.lang.Exception
import java.lang.RuntimeException

object ExceptionUtils {
    /**
     * Throw exception only on debug mode
     *
     * @param message exception message
     */
    fun throwExceptionDebug(message: String?) {
        if (BuildConfig.DEBUG) {
            throw RuntimeException(message)
        } else {
            // TODO: Add error logging via analytics
        }
    }

    /**
     * Throw exception only on debug mode with a message
     *
     * @param message exception message
     */
    fun throwExceptionDebug(e: Exception, message: String) {
        if (BuildConfig.DEBUG) {
            val errorMessage = """
                $message
                ${e.message}
                """.trimIndent()
            throw RuntimeException(errorMessage, e)
        } else {
            // TODO: Add error logging via analytics
        }
    }

    fun throwExceptionDebugDefaultCase(caseObject: Any) {
        throwExceptionDebug("This case is not handled, case=$caseObject")
    }

    fun throwExceptionDebugDefaultCase() {
        throwExceptionDebug("This case is not handled")
    }
}