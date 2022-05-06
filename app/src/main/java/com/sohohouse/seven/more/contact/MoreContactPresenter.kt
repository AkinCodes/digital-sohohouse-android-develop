package com.sohohouse.seven.more.contact

import android.annotation.SuppressLint
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.error.ErrorDialogPresenter
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.GymMembership
import com.sohohouse.seven.more.contact.InquiryType.*
import com.sohohouse.seven.more.contact.recycler.*
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.request.PostInquiryRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import javax.inject.Inject

class MoreContactPresenter @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val analyticsManager: AnalyticsManager
) :
    BasePresenter<MoreContactViewController>(), PresenterLoadable<MoreContactViewController>,
    ErrorDialogPresenter<MoreContactViewController> {
    private var isAccountBarred = false

    override fun onAttach(
        view: MoreContactViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        if (isFirstAttach) {
            fetchEnquiryMap()
        }
    }

    private fun fetchEnquiryMap(predefinedEnquiryTypes: MutableList<EnquiryType>? = null) {
        val dataList = generateItems(predefinedEnquiryTypes)
        executeWhenAvailable { view, _, _ ->
            view.showEnquiryForm(
                dataList, predefinedEnquiryTypes
                    ?: mutableListOf(), isAccountBarred
            )
        }
    }

    fun onSubmitSuccessDialogDismiss() {
        executeWhenAvailable { view, _, _ ->
            if (!isAccountBarred) view.resetEnquiryForm()
            view.clearInquiryText()
        }
    }

    @SuppressLint("CheckResult")
    fun onSubmitClicked(enquiryTypes: List<EnquiryType>, text: String) {
        var inquiryType = ""
        var reason = ""
        var venueType: String? = null
        var venueName: String? = null
        for (enquiryType in enquiryTypes) {
            when (enquiryType.apiFieldIndex) {
                0 -> inquiryType = enquiryType.key
                1 -> reason = enquiryType.key
                2 -> venueType = enquiryType.key
                3 -> venueName = enquiryType.key
            }
        }
        zipRequestsUtil.issueApiCall(
            PostInquiryRequest(
                inquiryType,
                reason,
                venueType,
                venueName,
                text
            )
        )
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorDialogTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Error -> {
                        analyticsManager.track(AnalyticsEvent.ContactUs.Failure(it.error.toString()))
                    }
                    is Either.Empty -> {
                        analyticsManager.track(AnalyticsEvent.ContactUs.Success)
                        executeWhenAvailable { view, _, _ ->
                            view.showSubmitSuccessDialog()
                        }

                        val mutableEnquiryTypes =
                            if (isAccountBarred) enquiryTypes.toMutableList() else null

                        fetchEnquiryMap(mutableEnquiryTypes)
                    }
                }
            })
    }

    @SuppressLint("CheckResult")
    fun onHouseInfoClicked() {
        executeWhenAvailable { v, _, _ ->
            v.loadUrlInWebView(
                SohoWebHelper.getWebViewFormatted(
                    SohoWebHelper.KickoutType.CONTACT_SUPPORT
                ).toString()
            )
        }
    }

    private fun generateItems(preselectedEnquiryTypes: MutableList<EnquiryType>?): MutableList<BaseEnquiryItem> {
        val dataList = mutableListOf<BaseEnquiryItem>()
        dataList.add(HeaderContactItemType())
        dataList.add(HeaderEnquiryItemType())

        val enquiryType = EnquiryMap.getTopLevelEnquiryType()
        if (preselectedEnquiryTypes != null) {
            addPreselectedItem(
                dataList,
                EnquiryMap.getTopLevelEnquiryType(),
                preselectedEnquiryTypes
            )
        } else {
            dataList.add(SpinnerEnquiryItemType(enquiryType))
        }

        if (!isAccountBarred) dataList.add(FooterInputEnquiryItemType())
        return dataList
    }

    private fun addPreselectedItem(
        dataList: MutableList<BaseEnquiryItem>, enquiryTypes: EnquiryType,
        preselectedEnquiryTypes: MutableList<EnquiryType>, index: Int = 0
    ) {
        when {
            index < preselectedEnquiryTypes.size -> {
                val indexOfPreselectedType =
                    enquiryTypes.childEnqTypes?.indexOf(preselectedEnquiryTypes[index])
                dataList.add(SpinnerEnquiryItemType(enquiryTypes, indexOfPreselectedType))
                indexOfPreselectedType?.let {
                    addPreselectedItem(
                        dataList, enquiryTypes.childEnqTypes[it],
                        preselectedEnquiryTypes, index + 1
                    )
                }
            }
            preselectedEnquiryTypes.last().childEnqTypes == null -> dataList.add(
                TextInputEnquiryItemType(hints = preselectedEnquiryTypes.last().messages)
            )
            else -> dataList.add(SpinnerEnquiryItemType(preselectedEnquiryTypes.last()))
        }
    }

    fun onInquiryTypeReceived(predefinedInquiryType: InquiryType, gymMembership: GymMembership) {
        val preselectedEnquiryTypes = when (predefinedInquiryType) {
            GENERAL_ENQUIRY -> mutableListOf(
                EnquiryType(
                    "GENERAL_ENQUIRY", 0, R.string.enquiry_level_two_header,
                    R.string.enquiry_reason_general_enquiry, EnquiryMap.generateLevelTwoGeneral()
                )
            )
            MEMBERSHIP_CHANGES_ENQUIRY -> mutableListOf(
                EnquiryType(
                    "MEMBERSHIP_ENQUIRY", 0, R.string.enquiry_level_two_header,
                    R.string.enquiry_reason_membership_enquiry, null
                ),
                EnquiryType(
                    "CHANGE", 1, R.string.enquiry_level_two_header,
                    R.string.enquiry_membership_changes_label, null
                )
            )
            MEMBERSHIP_ACTIVE_SUBSCRIPTION -> mutableListOf(
                EnquiryType(
                    "MEMBERSHIP_ENQUIRY", 0, R.string.enquiry_level_two_header,
                    R.string.enquiry_reason_membership_enquiry, null
                )
            ).also { list ->
                if (gymMembership == GymMembership.ACTIVE) {
                    list.add(
                        EnquiryType(
                            "ADD_ACTIVE_MEMBERSHIP", 1, R.string.enquiry_level_two_header,
                            R.string.enquiry_add_active_membership, null,
                            listOf(
                                R.string.enquiry_active_placeholder1,
                                R.string.enquiry_active_placeholder2
                            )
                        )
                    )
                } else if (gymMembership == GymMembership.ACTIVE_PLUS) {
                    list.add(
                        EnquiryType(
                            "ADD_ACTIVE_MEMBERSHIP", 1, R.string.enquiry_level_two_header,
                            R.string.enquiry_add_active_membership, null,
                            listOf(
                                R.string.enquiry_active_plus_placeholder1,
                                R.string.enquiry_active_plus_placeholder2
                            )
                        )
                    )
                }
            }
        }

        isAccountBarred = predefinedInquiryType == MEMBERSHIP_CHANGES_ENQUIRY
        fetchEnquiryMap(preselectedEnquiryTypes)
    }
}