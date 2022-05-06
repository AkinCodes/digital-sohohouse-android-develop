package com.sohohouse.seven.common.utils

class LuhnValidationUtils {

    companion object {

        fun isValid(input: String): Boolean {
            val sanitizedInput = input.replace(Regex("""[$ -]"""), "")
            return checksum(sanitizedInput) % 10 == 0
        }

        private fun checksum(input: String) = addends(input).sum()

        private fun addends(input: String) = input.mapToInt().mapIndexed { index, value ->
            when {
                (input.length - index + 1) % 2 == 0 -> value
                value >= 5 -> value * 2 - 9
                else -> value * 2
            }
        }

        private fun String.mapToInt() = this.map(Character::getNumericValue)

    }


}
