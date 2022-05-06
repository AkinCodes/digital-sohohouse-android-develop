package com.sohohouse.seven.memberonboarding.induction.booking

enum class InductItemType { HEADER, SECTION_WEEK, SECTION_MORE_WEEK, APPOINTMENT, APPOINTMENTS_NONE, REQUEST_FOLLOWUP, NONE_SCHEDULED }

open class BaseInductItem(val type: InductItemType)

class HeaderInductItem(
    val colorString: String,
    val name: String,
    val imageURL: String?,
    val isPlanner: Boolean
) : BaseInductItem(InductItemType.HEADER)

class SectionWeekInductItem : BaseInductItem(InductItemType.SECTION_WEEK)

class SectionMoreWeekInductItem : BaseInductItem(InductItemType.SECTION_MORE_WEEK)

class AppointmentInductItem(
    val eventID: String,
    val dateString: String,
    var isClicked: Boolean = false
) : BaseInductItem(InductItemType.APPOINTMENT)

class AppointmentsNoneInductItem : BaseInductItem(InductItemType.APPOINTMENTS_NONE)

class FollowUpInductItem : BaseInductItem(InductItemType.REQUEST_FOLLOWUP)

class NoneScheduledInductItem : BaseInductItem(InductItemType.NONE_SCHEDULED)