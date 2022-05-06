package com.sohohouse.seven.connect.match.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getAttributeColor
import com.sohohouse.seven.connect.trafficlights.controlpanel.OverlappedFrameLayout
import com.sohohouse.seven.connect.trafficlights.controlpanel.VenueMemberViewProvider
import com.sohohouse.seven.databinding.ViewComeBackSoonBinding

class ComeBackSoonView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {

    init {
        val binding = ViewComeBackSoonBinding.inflate(LayoutInflater.from(context), this, true)

        val list = listOf(
            VenueMemberViewProvider.NotBlurred(imageRes = R.drawable.img_come_back_soon_1),
            VenueMemberViewProvider.NotBlurred(imageRes = R.drawable.img_come_back_soon_2),
            VenueMemberViewProvider.NotBlurred(imageRes = R.drawable.img_come_back_soon_3),
            VenueMemberViewProvider.NotBlurred(imageRes = R.drawable.img_come_back_soon_4),
            VenueMemberViewProvider.NotBlurred(imageRes = R.drawable.img_come_back_soon_5)
        )

        list.forEachIndexed { index, connectMemberViewProvider ->
            val inflatedViewParent = OverlappedFrameLayout(
                context = context,
                currentIndex = index,
                totalCount = list.size
            ).apply {
                backgroundTintList =
                    ColorStateList.valueOf(context.getAttributeColor(R.attr.colorLayer2))
                setPadding(
                    resources.getDimensionPixelOffset(R.dimen.dp_4),
                    resources.getDimensionPixelOffset(R.dimen.dp_4),
                    resources.getDimensionPixelOffset(R.dimen.dp_4),
                    resources.getDimensionPixelOffset(R.dimen.dp_4)
                )
            }
            connectMemberViewProvider.inflate(inflatedViewParent)
            binding.comeBackConnectionsContainer.addView(inflatedViewParent)
        }
    }

}