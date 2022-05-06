package com.sohohouse.seven.shake

import androidx.appcompat.app.AppCompatActivity
import com.sohohouse.seven.more.membershipdetails.Mode
import com.sohohouse.seven.more.membershipdetails.MoreMembershipDetailsActivity

// If an activity needs to show the membership card on shake, implement this interface
interface MembershipCardShakeListener : ShakeListener {

    override fun onShakeStarted() {
        if (this is AppCompatActivity) {
            this.startActivity(
                MoreMembershipDetailsActivity.getIntent(
                    this,
                    Mode.CARD_ONLY
                )
            )
        }
    }

    override fun onShakeStopped() {
        // do nothing
    }
}