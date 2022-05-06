package com.sohohouse.seven.memberonboarding.induction.booking.views

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.OnboardingIntroAppointmentLayoutBinding
import com.sohohouse.seven.memberonboarding.induction.booking.AppointmentInductItem

interface AppointmentClickListener {
    fun appointmentClicked(position: Int)
}

class AppointmentIntroViewHolder(private val binding: OnboardingIntroAppointmentLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindData(item: AppointmentInductItem, listener: AppointmentClickListener) = with(binding) {
        introTimeText.text = item.dateString
        root.clicks { listener.appointmentClicked(adapterPosition) }
        introTimeCheck.setVisible(item.isClicked)
    }
}