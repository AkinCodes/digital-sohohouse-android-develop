package com.sohohouse.seven.memberonboarding.induction.booking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.OnboardingIntroAppointmentLayoutBinding
import com.sohohouse.seven.databinding.OnboardingIntroRequestFollowupLayoutBinding
import com.sohohouse.seven.memberonboarding.induction.booking.views.*

interface IntroductionAdapterListener {
    fun appointmentSelected(eventId: String)
    fun changeConfirmedAppointmentClicked()
    fun requestFollowupClicked()
    fun onBackButtonPressed()
}

class IntroductionAdapter(
    private val dataItems: List<BaseInductItem>,
    val listener: IntroductionAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), AppointmentClickListener,
    RequestFollowupListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = { layoutResId: Int, intoParent: ViewGroup ->
            LayoutInflater.from(parent.context).inflate(layoutResId, intoParent, false)
        }
        return when (InductItemType.values()[viewType]) {
            InductItemType.HEADER -> HeaderInductViewHolder(
                inflate(
                    HeaderInductViewHolder.LAYOUT,
                    parent
                )
            )
            InductItemType.SECTION_WEEK -> SectionWeekInductViewHolder(
                inflate(
                    SectionWeekInductViewHolder.LAYOUT,
                    parent
                )
            )
            InductItemType.SECTION_MORE_WEEK -> SectionMoreWeekInductViewHolder(
                inflate(
                    SectionMoreWeekInductViewHolder.LAYOUT,
                    parent
                )
            )
            InductItemType.APPOINTMENT -> AppointmentIntroViewHolder(
                OnboardingIntroAppointmentLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            InductItemType.APPOINTMENTS_NONE -> AppointmentsNoneInductViewHolder(
                inflate(
                    AppointmentsNoneInductViewHolder.LAYOUT,
                    parent
                )
            )
            InductItemType.REQUEST_FOLLOWUP -> RequestFollowupIntroViewHolder(
                OnboardingIntroRequestFollowupLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            InductItemType.NONE_SCHEDULED -> NoneScheduledInductViewHolder(
                inflate(
                    NoneScheduledInductViewHolder.LAYOUT,
                    parent
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (InductItemType.values()[getItemViewType(position)]) {
            InductItemType.HEADER -> (holder as HeaderInductViewHolder).bind(
                dataItems[position] as HeaderInductItem,
                listener
            )
            InductItemType.APPOINTMENT -> (holder as AppointmentIntroViewHolder).bindData(
                dataItems[position] as AppointmentInductItem,
                this
            )
            InductItemType.REQUEST_FOLLOWUP -> (holder as RequestFollowupIntroViewHolder).bind(this)
            else -> {
                //no binding needed as views are static
            }
        }
    }

    override fun appointmentClicked(position: Int) {
        val item = dataItems[position] as AppointmentInductItem
        listener.appointmentSelected(item.eventID)
    }

    fun updateSelectedDate(eventId: String) {
        dataItems.forEachIndexed { index, item ->
            when (item) {
                is AppointmentInductItem -> {
                    val isSelected = item.eventID == eventId
                    if (isSelected != item.isClicked) {
                        item.isClicked = isSelected
                        notifyItemChanged(index)
                    }
                }
            }
        }
    }

    override fun requestFollowupClicked() {
        listener.requestFollowupClicked()
    }

    override fun getItemCount(): Int {
        return dataItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return dataItems[position].type.ordinal
    }
}