package com.sohohouse.seven.housepay.home

import android.text.format.DateFormat
import com.sohohouse.seven.housepay.CheckRepo
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.split
import javax.inject.Inject

class GetReceiptsUseCase @Inject constructor(
    private val checkRepository: CheckRepo
) {
    suspend operator fun invoke(): Map<CharSequence, List<Check>> {
        return checkRepository.getChecks(
            page = 1,
            perPage = 100,
            include = CheckRepo.INCLUDE_CHECK_CLOSED_FILTER
        ).split(
            ifSuccess = { transformCheckList(it) },
            ifError = { emptyMap() }
        )
    }

    private fun transformCheckList(list: List<Check>): Map<CharSequence, List<Check>> {
        return list.filter { it.paidAt != null }
            .sortedByDescending { it.paidAt }
            .groupBy { DateFormat.format("MMMM yyyy", it.paidAt) }
    }

}
