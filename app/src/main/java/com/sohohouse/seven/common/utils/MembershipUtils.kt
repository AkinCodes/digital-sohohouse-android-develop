package com.sohohouse.seven.common.utils

object MembershipUtils {

    fun formatMembershipNumber(membershipNumber: String): String {
        if (membershipNumber.length < 8) return membershipNumber
        return ArrayList(membershipNumber.toCharArray().asList()).apply {
            add(size - 3, ' ')
            add(size - 7, ' ')
        }.joinToString(separator = "")
    }

}