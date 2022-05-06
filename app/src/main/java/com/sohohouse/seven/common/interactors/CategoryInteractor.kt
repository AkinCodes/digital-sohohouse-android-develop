package com.sohohouse.seven.common.interactors

import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.categorylist.CategoryDataItem
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.ContentCategory
import com.sohohouse.seven.network.core.models.EventCategory
import com.sohohouse.seven.network.core.request.GetContentCategoriesRequest
import io.reactivex.Single
import io.reactivex.SingleTransformer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryInteractor @Inject constructor(
    private val userManager: UserManager,
    private val zipRequestsUtil: ZipRequestsUtil,
    private val accountInteractor: AccountInteractor
) {

    private var contentCategories: List<ContentCategory>? = null
    private var eventCategory: List<EventCategory>? = null


    fun getContentCategories(preselectedCategories: List<String>? = null): Single<Either<ServerError, Pair<List<CategoryDataItem>, List<String>>>> {
        return accountInteractor.getAccount()
            .flatMap { either ->
                either.fold(
                    ifValue = {
                        userManager.favouriteContentCategories =
                            it.favoriteCategoriesResource?.map { categoriesIdentifier -> categoriesIdentifier.id }
                                ?: listOf()
                        contentCategories?.let { contentCategories ->
                            Single.just(value(contentCategories))
                                .compose(selectedContentCategories(preselectedCategories))
                        } ?: zipRequestsUtil.issueApiCall(GetContentCategoriesRequest())
                            .compose(selectedContentCategories(preselectedCategories))
                    },
                    ifError = { Single.just(Either.Error(it)) },
                    ifEmpty = { Single.just(Either.Empty()) }
                )
            }
    }

    private fun selectedContentCategories(preselectedCategories: List<String>? = null): SingleTransformer<Either<ServerError, List<ContentCategory>>, Either<ServerError, Pair<List<CategoryDataItem>, List<String>>>> {
        return SingleTransformer { single ->
            return@SingleTransformer single.flatMap { either ->
                either.fold(
                    ifValue = {
                        contentCategories = it
                        val categoryData = getSelectedCategoryData(it, preselectedCategories)
                        Single.just(value(categoryData))
                    },
                    ifError = { Single.just(Either.Error(it)) },
                    ifEmpty = { Single.just(Either.Empty()) }
                )
            }
        }
    }

    private fun getSelectedCategoryData(
        dataList: List<ContentCategory>,
        preselectedCategories: List<String>? = null
    ): Pair<List<CategoryDataItem>, List<String>> {

        val selectedCategories: List<String> = preselectedCategories
            ?: (userManager.favouriteContentCategories ?: listOf())

        val categoryList: List<CategoryDataItem> = dataList.map { item ->
            CategoryDataItem(
                item.id,
                item.categoryName,
                item.categoryIcon?.png,
                selectedCategories.contains(item.id)
            )
        }

        return Pair(categoryList, selectedCategories)
    }

    fun clearData() {
        contentCategories = null
        eventCategory = null
    }
}