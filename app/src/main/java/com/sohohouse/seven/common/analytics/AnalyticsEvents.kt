package com.sohohouse.seven.common.analytics

import com.sohohouse.seven.common.extensions.concatenateWithSymbol
import com.sohohouse.seven.common.extensions.getApiFormattedDate
import com.sohohouse.seven.common.extensions.getFilterApiFormattedDate
import java.util.*

@Deprecated("Deprecated: Use AnalyticsManager.Action instead")
sealed class AnalyticsEvent(
    val category: String,
    open val action: String,
    open val label: String? = null
) {

    sealed class Authentication(override val action: String, override val label: String) :
        AnalyticsEvent("authentication", action, label) {
        sealed class Login(override val label: String) : Authentication("login_request", label) {
            object Success : Login("success")
            object MembershipSuspended : Login("membership_suspended")
            object PaymentOverdue : Login("payment_overdue")
            object MembershipOnHold : Login("membership_on_hold")
            object AccountNotActive : Login("account_not_active")
            object Expired : Login("expired")
            object Resigned : Login("resigned")
        }

        sealed class PaymentOverdue(override val label: String) :
            Authentication("payment_overdue", label) {
            object UpdatePayment : PaymentOverdue("select_update_payment_method")
            object Continue : PaymentOverdue("select_continue")
        }

        sealed class MembershipOnHold(override val label: String) :
            Authentication("membership_on_hold", label) {
            object ContactSupport : MembershipOnHold("contact_support_team")
            object Logout : MembershipOnHold("select_logout")
        }
    }

    sealed class Welcome(override val label: String) : AnalyticsEvent("welcome", "select", label) {
        object SignIn : Welcome("sign_in")
        object CreateAccount : Welcome("create_account")
    }

    sealed class MemberOnBoarding(override val action: String, override val label: String) :
        AnalyticsEvent("member_onboarding", action, label) {
        sealed class HouseIntroduction(override val label: String) :
            MemberOnBoarding("house_introduction", label) {
            class Success(inductionDate: Date) :
                HouseIntroduction("success_${inductionDate.getApiFormattedDate()}")

            object FollowUp : HouseIntroduction("follow_up")
        }

        object Calendar : MemberOnBoarding("calendar", "link")
    }

    sealed class AppOnBoarding(override val action: String, override val label: String) :
        AnalyticsEvent("app_onboarding", action, label) {
        object DataCollection : AppOnBoarding("data_collection", "accept")

        class HousePreferences(houseIds: List<String>) : AppOnBoarding(
            "house_preferences",
            "{locations: {${houseIds.concatenateWithSymbol(",")}}}"
        )

        class HouseNotesPreferences(categoryIds: List<String>) : AppOnBoarding(
            "house_notes_settings",
            "{categories: {${categoryIds.concatenateWithSymbol(",")}}}"
        )
    }

    sealed class Explore(override val action: String, override val label: String) :
        AnalyticsEvent("explore", action, label) {
        sealed class Navigation(override val label: String) : Explore("nav", label) {
            object Events : Navigation("events")
            object Cinema : Navigation("cinema")
            object Fitness : Navigation("fitness")
        }

        sealed class Events(override val label: String) : Explore("events", label) {
            class LoadMore(pageNum: Int?) : Events("load_more_$pageNum")
            object FilterButtonClick : Events("filter")
            class Filter(
                locationIds: List<String>,
                startDate: Date,
                endDate: Date?,
                categoryIds: List<String>
            ) : Events(
                "{filter: {locations: {${locationIds.concatenateWithSymbol(",")}}, startDate: ${startDate.getFilterApiFormattedDate()}, endDate: ${
                    endDate?.getFilterApiFormattedDate(
                    )
                }, categories: {${categoryIds.concatenateWithSymbol(",")}}}}"
            )

            object FilterReset : Events("filter_reset")
            class View(eventId: String) : Events("view_$eventId")
        }

        sealed class HouseVisit(override val label: String) : Explore("house_visit", label) {
            class LoadMore(pageNum: Int?) : HouseVisit("load_more_$pageNum")
            object FilterButtonClick : HouseVisit("filter")
            class Filter(
                locationIds: List<String>,
                startDate: Date,
                endDate: Date?,
                categoryIds: List<String>
            ) : HouseVisit(
                "{filter: {locations: {${locationIds.concatenateWithSymbol(",")}}, startDate: ${startDate.getFilterApiFormattedDate()}, endDate: ${
                    endDate?.getFilterApiFormattedDate(
                    )
                }, categories: {${categoryIds.concatenateWithSymbol(",")}}}}"
            )

            object FilterReset : HouseVisit("filter_reset")
            class View(eventId: String) : HouseVisit("view_$eventId")
        }

        sealed class Cinema(override val label: String) : Explore("cinema", label) {
            class LoadMore(pageNum: Int?) : Cinema("load_more_$pageNum")
            object FilterButtonClick : Cinema("filter")
            class Filter(
                locationIds: List<String>,
                startDate: Date,
                endDate: Date?,
                categoryIds: List<String>
            ) : Cinema(
                "{filter: {locations: {${locationIds.concatenateWithSymbol(",")}}, startDate: ${startDate.getFilterApiFormattedDate()}, endDate: ${
                    endDate?.getFilterApiFormattedDate(
                    )
                }, categories: {${categoryIds.concatenateWithSymbol(",")}}}}"
            )

            object FilterReset : Cinema("filter_reset")
            class View(eventId: String) : Cinema("view_$eventId")
        }

        sealed class Fitness(override val label: String) : Explore("fitness", label) {
            class LoadMore(pageNum: Int?) : Fitness("load_more_$pageNum")
            object FilterButtonClick : Fitness("filter")
            class Filter(
                locationIds: List<String>,
                startDate: Date,
                endDate: Date?,
                categoryIds: List<String>
            ) : Fitness(
                "{filter: {locations: {${locationIds.concatenateWithSymbol(",")}}, startDate: ${startDate.getFilterApiFormattedDate()}, endDate: ${
                    endDate?.getFilterApiFormattedDate(
                    )
                }, categories: {${categoryIds.concatenateWithSymbol(",")}}}}"
            )

            object FilterReset : Fitness("filter_reset")
            class View(eventId: String) : Fitness("view_$eventId")
        }
    }

    sealed class Events(override val action: String, override val label: String) :
        AnalyticsEvent("event", action, label) {
        class BookFree(eventId: String, ticketNum: Int) :
            Events("book_free", "${eventId}_$ticketNum")

        class BookPaid(eventId: String, ticketNum: Int) :
            Events("book_paid", "${eventId}_$ticketNum")

        class CancelFree(eventId: String) : Events("cancel_free", eventId)
        class CancelPaid(eventId: String) : Events("cancel_paid", eventId)
        class CancelConfirmation(eventId: String, price: String) :
            Events("cancel_confirmation", "${eventId}_$price")

        class JoinWaitListFree(eventId: String, ticketNum: Int) :
            Events("join_waitlist_free", "${eventId}_$ticketNum")

        class JoinWaitListPaid(eventId: String, ticketNum: Int) :
            Events("join_waitlist_paid", "${eventId}_$ticketNum")

        class LeaveWaitListFree(eventId: String, ticketNum: Int) :
            Events("leave_waitlist_free", "${eventId}_$ticketNum")

        class LeaveWaitListPaid(eventId: String, ticketNum: Int) :
            Events("leave_waitlist_paid", "${eventId}_$ticketNum")

        class RemoveGuestFree(eventId: String) : Events("remove_guest_free", eventId)
        class RemoveGuestPaid(eventId: String) : Events("remove_guest_paid", eventId)
    }

    sealed class PaymentMethods(override val action: String, override val label: String) :
        AnalyticsEvent("payment_methods", action, label) {
        sealed class Add(override val label: String) : PaymentMethods("add_payment_method", label) {
            object Success : Add("success")
            class Failure(errorDescription: String) : Add("failure_$errorDescription")
        }

        sealed class Delete(override val label: String) : PaymentMethods("delete", label) {
            object Success : Delete("success")
            class Failure(errorDescription: String) : Delete("failure_$errorDescription")
        }

        sealed class MakeDefault(override val label: String) : PaymentMethods("delete", label) {
            object Success : MakeDefault("success")
            class Failure(errorDescription: String) : MakeDefault("failure_$errorDescription")
        }
    }

    sealed class More(override val label: String) : AnalyticsEvent("more", "select", label) {
        object Profile : More("profile")
        object MembershipDetails : More("membership_details")
        object Perks : More("perks_landing")
        object PaymentMethods : More("payment_methods")
        object ChangePassword : More("change_password")
        object HouseNotes : More("house_notes_settings")
        object HousePreferences : More("house_preferences")
        object CalendarSync : More("calendar_sync")
        object Contact : More("contact_us")
        object FAQs : More("faqs")
        object TermsAndPolicies : More("account_terms_and_policies")
        object Notification : More("notification_preferences")
        object LogOut : More("logout")
        object EditPhoto : More("edit_photo")
    }

    sealed class PhotoUpload(override val label: String) :
        AnalyticsEvent("photo_upload", "upload", label) {
        object Success : PhotoUpload("success")
        class Failure(errorDescription: String) : PhotoUpload("failure_$errorDescription")
    }

    object MembershipDetails : AnalyticsEvent("membership_details", "select", "make_payment")
    sealed class ChangePassword(override val label: String) :
        AnalyticsEvent("change_password", "request", label) {
        object Success : ChangePassword("success")
        class Failure(errorDescription: String) : ChangePassword("failure_$errorDescription")
    }

    class HousePreferences(houseIds: List<String>) : AnalyticsEvent(
        "housePreferences",
        "select",
        "{locations: {${houseIds.concatenateWithSymbol(",")}}}"
    )

    class HouseNotesPreferences(categoryIds: List<String>) : AnalyticsEvent(
        "house_notes_settings",
        "select",
        "{categories: {${categoryIds.concatenateWithSymbol(",")}}}"
    )

    sealed class ContactUs(override val label: String) :
        AnalyticsEvent("contact_us", "submit", label) {
        object Success : ContactUs("success")
        class Failure(errorDescription: String) : ContactUs("failure_$errorDescription")
    }

    sealed class PrivacyPolices(override val action: String, override val label: String) :
        AnalyticsEvent("privacy_policies", action, label) {
        sealed class View(override val label: String) : PrivacyPolices("view", label) {
            object TermsConditions : View("tnc")
            object PrivacyCookie : View("privacy_cookie")
        }

        object Select : PrivacyPolices("select", "delete_data_contact_us")
    }

    sealed class Notification(override val action: String, override val label: String) :
        AnalyticsEvent("notification_preferences", action, label) {
        sealed class Update(override val label: String) :
            Notification("update_preferences", label) {
            class Success(
                digitalHouseEmail: Boolean,
                membershipEmail: Boolean,
                sohoEmail: Boolean
            ) : Update("dh_email=$digitalHouseEmail,membership_email=$membershipEmail,sh_email=$sohoEmail")

            class Failure(errorDescription: String) : Update("failure_$errorDescription")
        }
    }

    sealed class LogOut(override val action: String) : AnalyticsEvent("logout", action) {
        object Confirm : LogOut("confirm")
        object Cancel : LogOut("cancel")
    }

    sealed class Home(override val label: String) : AnalyticsEvent("home", "select", label) {
        class HouseBoardSeeAll(houseId: String) : Home("house_board_see_all_$houseId")
        class HouseBoardPostNote(houseId: String) : Home("house_board_post_note_$houseId")
        class HouseNotesSeeAll(houseId: String) : Home("house_notes_see_all_$houseId")
        class HouseNotes(houseNoteId: String) : Home("house_note_$houseNoteId")
        class ForYouSeeAll(houseId: String) : Home("for_you_see_all_$houseId")
        object PersonalizeFeed : Home("personalize_feed")
        object PlannerSeeAll : Home("planner_see_all")
        class PlannerEvent(eventId: String) : Home("planner_event_$eventId")
        object RoomBooking : Home("room_booking")
        class House(houseId: String) : Home("house_$houseId")

        class FooterPerksSeeAll : Home("perks_open_from_home_footer")
    }

    sealed class HouseBoard(override val action: String, override val label: String) :
        AnalyticsEvent("house_board", action, label) {
        class PostNote(houseId: String) : HouseBoard("post_note", houseId)
        class UpdateNote(houseId: String) : HouseBoard("update_note", houseId)
        class DeleteNote(houseId: String) : HouseBoard("delete_note", houseId)
    }

    sealed class Planner(override val label: String) : AnalyticsEvent("planner", "select", label) {
        class Event(eventId: String) : Planner("event_$eventId")
        object RoomBooking : Planner("room_booking")
        object EventMore : Planner("event_more")
        object CinemaMore : Planner("cinema_more")
        object FitnessMore : Planner("fitness_more")
    }

    sealed class HouseNotes(override val action: String, override val label: String) :
        AnalyticsEvent("house_notes", action, label) {
        sealed class Select(override val label: String) : HouseNotes("select", label) {
            class HouseNote(houseNoteId: String) : Select("house_note_$houseNoteId")
            object SeeAllForYou : Select("see_all_for_you")
            object PersonalizeFeed : Select("personalized_feed")
        }

        sealed class Filter(override val label: String) : HouseNotes("filter", label) {
            class Data(locationIds: List<String>, categoryIds: List<String>) : Filter(
                "data_${locationIds.concatenateWithSymbol("_")},${
                    categoryIds.concatenateWithSymbol("_")
                }"
            )

            object Reset : Filter("reset")
        }

        sealed class LoadMore(override val label: String) : HouseNotes("load_more", label) {
            class HouseNote(pageId: Int) : LoadMore("$pageId")
            class FilteredHouseNotes(pageId: Int) : LoadMore("filtered_$pageId")
        }
    }

    sealed class HouseLanding(override val label: String) :
        AnalyticsEvent("house_landing", "select", label) {
        class RoomBooking(houseId: String) : HouseLanding("room_booking_$houseId")
        class HouseBoardSeeAll(houseId: String) : HouseLanding("house_board_see_all_$houseId")
        class HouseBoardPostNote(houseId: String) : HouseLanding("house_board_post_note_$houseId")
        class HouseNotesSeeAll(houseId: String) : HouseLanding("house_notes_see_all_$houseId")
        class HouseNotes(houseNoteId: String) : HouseLanding("house_note_$houseNoteId")
        class HouseTour(houseId: String) : HouseLanding("house_tour_$houseId")
        class HouseRules(houseId: String) : HouseLanding("house_rules_$houseId")
        class ContactHouse(houseId: String) : HouseLanding("contact_house_$houseId")
        class CityGuide(cityGuideId: String) : HouseLanding("city_gufide_$cityGuideId")
    }
}