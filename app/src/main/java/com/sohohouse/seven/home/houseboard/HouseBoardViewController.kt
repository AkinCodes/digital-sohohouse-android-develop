package com.sohohouse.seven.home.houseboard

import com.sohohouse.seven.base.mvvm.ViewController
import com.sohohouse.seven.common.apihelpers.SohoWebHelper

interface HouseBoardViewController : ViewController {

    fun navigateToBrowseHousesScreen()

    fun navigateToMembershipCardScreen()

    fun loadURLinWebView(kickoutType: SohoWebHelper.KickoutType, headerRes: Int, id: String?)
}
