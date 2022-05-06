package com.sohohouse.seven.common.extensions

import android.text.TextUtils
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.sohohouse.seven.network.core.models.DeviceNotificationPreferences

fun MarketingCloudSdk.updateAllTags(prefs: List<DeviceNotificationPreferences>) {
    this.cleanAllTags()
    prefs.iterator().forEach {
        tag(
            it.marketingCloud.tag,
            it.marketingCloud.tag_indicates_disabled,
            it.enabled
        )
    }
}

fun MarketingCloudSdk.tag(tag: String, tagMeansDisable: Boolean, isEnable: Boolean) {
    if (TextUtils.isEmpty(tag)) return

    this.registrationManager.edit().run {
        //Checking double tags. Those tags are hardcoded below.
        if (!TextUtils.isEmpty(matchTag(tag, isEnable))) {
            addTag(matchTag(tag, isEnable))
            removeTag(matchTag(tag, !isEnable))
        } else if (isEnable) {
            if (tagMeansDisable) removeTag(tag)
            else addTag(tag)
        } else {
            if (tagMeansDisable) addTag(tag)
            else removeTag(tag)
        }

        commit()
    }

}

fun MarketingCloudSdk.cleanAllTags() {
    this.registrationManager.edit().run {
        this@cleanAllTags.registrationManager.tags.forEach { removeTag(it) }
        commit()
    }
}

private const val TAG_MARKETING_ENABLE = "MarketingEnable"
private const val TAG_MARKETING_DISABLE = "MarketingDisabled"
private const val TAG_UPDATED_ENABLE = "UpdatesEnable"
private const val TAG_UPDATED_DISABLE = "UpdatesDisabled"
private const val TAG_AFFILATES_ENABLE = "AffiliatesEnable"
private const val TAG_AFFILATES_DISABLE = "AffiliatesDisabled"

private fun matchTag(tag: String, isEnable: Boolean) = if (isEnable) {
    when (tag) {
        TAG_MARKETING_ENABLE, TAG_MARKETING_DISABLE -> TAG_MARKETING_ENABLE
        TAG_UPDATED_ENABLE, TAG_UPDATED_DISABLE -> TAG_UPDATED_ENABLE
        TAG_AFFILATES_ENABLE, TAG_AFFILATES_DISABLE -> TAG_AFFILATES_ENABLE
        else -> ""
    }
} else {
    when (tag) {
        TAG_MARKETING_ENABLE, TAG_MARKETING_DISABLE -> TAG_MARKETING_DISABLE
        TAG_UPDATED_ENABLE, TAG_UPDATED_DISABLE -> TAG_UPDATED_DISABLE
        TAG_AFFILATES_ENABLE, TAG_AFFILATES_DISABLE -> TAG_AFFILATES_DISABLE
        else -> ""
    }
}