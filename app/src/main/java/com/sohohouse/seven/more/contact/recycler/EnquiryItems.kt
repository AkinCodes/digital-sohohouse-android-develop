package com.sohohouse.seven.more.contact.recycler

import com.sohohouse.seven.more.contact.EnquiryType

open class BaseEnquiryItem(val type: EnquiryItemType)

class HeaderContactItemType : BaseEnquiryItem(EnquiryItemType.CONTACT_HEADER)

class HeaderEnquiryItemType : BaseEnquiryItem(EnquiryItemType.ENQUIRY_HEADER)

class SpinnerEnquiryItemType(val enquiryType: EnquiryType, var selectedIndex: Int? = null) :
    BaseEnquiryItem(EnquiryItemType.SPINNER)

class TextInputEnquiryItemType(var text: CharSequence = "", val hints: List<Int> = emptyList()) :
    BaseEnquiryItem(EnquiryItemType.TEXT_INPUT)

class FooterInputEnquiryItemType : BaseEnquiryItem(EnquiryItemType.FOOTER)