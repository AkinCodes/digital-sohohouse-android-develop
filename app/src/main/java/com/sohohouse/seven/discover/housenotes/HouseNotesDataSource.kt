package com.sohohouse.seven.discover.housenotes

import androidx.paging.PageKeyedDataSource
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.discover.housenotes.model.HouseNoteCard
import com.sohohouse.seven.discover.housenotes.model.HouseNoteListItem
import com.sohohouse.seven.discover.housenotes.model.HouseNoteSmallHeading
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.core.request.GetHouseNotesSitecoreRequest.Companion.MAX_HOUSE_NOTES_PER_PAGE
import com.sohohouse.seven.network.sitecore.SitecoreResourceFactory
import com.sohohouse.seven.network.sitecore.models.template.Template

class HouseNotesDataSource(
    private val repo: HouseNotesRepo,
    loadable: Loadable.ViewModel,
    errorable: Errorable.ViewModel
) : PageKeyedDataSource<Int, DiffItem>(),
    Loadable.ViewModel by loadable,
    Errorable.ViewModel by errorable {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, DiffItem>
    ) {
        setLoadingState(LoadingState.Loading)
        repo.getAll(MAX_HOUSE_NOTES_PER_PAGE, 0).fold(
            ::onError,
            { mapLoadInitial(it, callback, 0, 1) },
            {})
        setLoadingState(LoadingState.Idle)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, DiffItem>) {
        setLoadingState(LoadingState.Loading)
        repo.getAll(MAX_HOUSE_NOTES_PER_PAGE, params.key * MAX_HOUSE_NOTES_PER_PAGE)
            .fold(
                ::onError,
                { mapLoadAfter(it, callback, params.key + 1) },
                {})
        setLoadingState(LoadingState.Idle)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, DiffItem>) {
        // Do nothing
    }

    private fun mapLoadInitial(
        templates: List<Template>,
        callback: LoadInitialCallback<Int, DiffItem>,
        adjacentPageKey: Int,
        nextPageKey: Int
    ) {
        val items = mutableListOf<DiffItem>()
        templates.firstOrNull()?.let { template ->
            items.add(
                HouseNoteCard(
                    id = template.id,
                    title = template.fieldValue.title,
                    imageUrl = if (template.fieldValue.thumbnailImageUrl.isNotEmpty()) template.fieldValue.thumbnailImageUrl else template.fieldValue.mainImageUrl,
                    videoUrl = template.fieldValue.mainVideo
                )
            )
        }

        if (templates.size > 1) {
            items.add(HouseNoteSmallHeading(R.string.content_all_header))
            for (pos in 1 until templates.size) {
                val template = templates[pos]
                items.add(mapToSmallContent(template))
            }
        }

        callback.onResult(items, adjacentPageKey, nextPageKey)
    }

    private fun mapLoadAfter(
        templates: List<Template>,
        callback: LoadCallback<Int, DiffItem>,
        adjacentPageKey: Int
    ) {
        callback.onResult(templates.map { mapToSmallContent(it) }, adjacentPageKey)
    }

    private fun mapToSmallContent(template: Template): DiffItem {
        return HouseNoteListItem(
            id = template.id,
            title = template.fieldValue.title,
            imageUrl = buildImageUrl(template)
        )
    }

    private fun buildImageUrl(template: Template): String? {
        val url =
            if (template.fieldValue.thumbnailImageUrl.isNotEmpty()) template.fieldValue.thumbnailImageUrl else template.fieldValue.mainImageUrl
        return SitecoreResourceFactory.getImageUrl(url)
    }

    private fun onError(error: ServerError) {
        FirebaseCrashlytics.getInstance().log(error.toString())
        showError()

    }

}