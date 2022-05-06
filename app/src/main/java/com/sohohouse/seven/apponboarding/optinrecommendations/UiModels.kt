package com.sohohouse.seven.apponboarding.optinrecommendations

sealed interface LandingOptInPageData {


    object UpdateYourProfile : LandingOptInPageData
    class PersonalizedRecommendations(val onOptIn: (Boolean) -> Unit) : LandingOptInPageData
    class ContinueWithMoreMembers(val onClose: () -> Unit) : LandingOptInPageData

}

sealed interface ActionButtons {
    object Next : ActionButtons
    object Continue : ActionButtons
}

typealias AlertDialogActions = Pair<PositiveButtonOnClick, NegativeButtonOnClick>
typealias PositiveButtonOnClick = () -> Unit
typealias NegativeButtonOnClick = () -> Unit