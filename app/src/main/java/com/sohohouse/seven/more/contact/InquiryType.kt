package com.sohohouse.seven.more.contact

enum class InquiryType {
    GENERAL_ENQUIRY,
    MEMBERSHIP_CHANGES_ENQUIRY,
    MEMBERSHIP_ACTIVE_SUBSCRIPTION;
}

data class EnquiryType(
    val key: String,
    val apiFieldIndex: Int,
    val headerTextRes: Int,
    val displayTextRes: Int,
    val childEnqTypes: MutableList<EnquiryType>?,
    val messages: List<Int> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        return other is EnquiryType && other.key == key
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + apiFieldIndex
        result = 31 * result + headerTextRes
        result = 31 * result + displayTextRes
        return result
    }
}
