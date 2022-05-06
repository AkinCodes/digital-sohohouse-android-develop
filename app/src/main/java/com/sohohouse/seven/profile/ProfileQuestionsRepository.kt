package com.sohohouse.seven.profile

import com.sohohouse.seven.R
import com.sohohouse.seven.common.utils.StringProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileQuestionsRepository @Inject constructor(private val stringProvider: StringProvider) {

    val questions: List<String>
        get() = stringProvider.getStringArray(R.array.profile_questions).toList()

}