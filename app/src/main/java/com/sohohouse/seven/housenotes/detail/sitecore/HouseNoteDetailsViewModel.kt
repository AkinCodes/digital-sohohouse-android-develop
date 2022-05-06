package com.sohohouse.seven.housenotes.detail.sitecore

import android.view.Gravity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewModel
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewModelImpl
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.book.eventdetails.EventDetailsPresenter
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.addTo
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.extensions.getFormattedDate
import com.sohohouse.seven.common.extensions.isNotEmpty
import com.sohohouse.seven.discover.housenotes.HouseNotesRepository
import com.sohohouse.seven.housenotes.detail.sitecore.HouseNoteDetailHeaderTextBlockItem.HeadingStyle
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.common.extensions.nullIfEmpty
import com.sohohouse.seven.network.sitecore.models.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import org.joda.time.format.ISODateTimeFormat
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class HouseNoteDetailsViewModel @Inject constructor(
    private val houseNotesRepository: HouseNotesRepository,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorViewStateViewModel by ErrorViewStateViewModelImpl() {

    private val itemsList: ArrayList<HouseNoteDetailItem> = ArrayList()

    private val _items = MutableLiveData<List<HouseNoteDetailItem>>()
    val items: LiveData<List<HouseNoteDetailItem>> get() = _items

    private val _header = MutableLiveData<HouseNoteDetailItem>()
    val header: LiveData<HouseNoteDetailItem> get() = _header

    var articleSlug = ""

    fun init(articleSlug: String) {
        this.articleSlug = articleSlug
        fetchHouseNote(articleSlug)
    }

    private fun fetchHouseNote(articleSlug: String) {
        houseNotesRepository.getHouseNoteDetails(articleSlug /*"f962f805-cd3f-46af-9b44-6a662c1d9969"*/)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Error -> {
                        Timber.tag(EventDetailsPresenter.TAG).d(it.error.toString())
                    }

                    is Either.Value -> {
                        createAdapterItems(it.value)
                    }
                }
            }).addTo(compositeDisposable)
    }

    private fun createAdapterItems(data: SitecoreRoute?) {
        data?.placeholders?.digitalHouseMain?.forEach {
            buildSections(it)
        }
        emitItems()
    }

    private fun emitItems() {
        _items.value = itemsList
    }

    private fun appendItem(item: HouseNoteDetailItem) {
        itemsList.add(item)
    }

    private fun buildSections(it: SitecoreComponent) {
        Timber.d("Building ${it.javaClass.simpleName}")
        when (it) {
            is SitecoreNoteDetailsComponent -> {
                buildHeader(it)
                buildSection(it)
            }
            is SitecoreBodyEditorialBlockComponent -> buildSection(it)
            is SitecoreBodyEditorialVideoComponent -> buildSection(it)
            is SitecoreCarouselGalleryComponent -> buildSection(it)
            is SitecoreGridGalleryComponent -> buildSection(it)
            is SitecoreEditorialRichTextComponent -> buildSection(it)
            is SitecoreEditorialBodyThreeComponent -> buildSection(it)
            is SitecoreEditorialFullImageComponent -> buildSection(it)
            is SitecoreTwoImageGalleryComponent -> buildSection(it)
            is SitecoreThreeImageGalleryComponent -> buildSection(it)
            is SitecoreEditorialLandscapeComponent -> buildSection(it)
            is SitecoreEditorialPortraitComponent -> buildSection(it)
            is HeroComponent -> buildSection(it)
            is GalleryStackComponent -> buildSection(it)
            is LandscapeImageComponent -> buildSection(it)
            is TileImageComponent -> buildSection(it)
            is TileImageContainerComponent -> buildSection(it)
            is EditorialQuoteComponent -> buildSection(it)
            is EditorialHeaderComponent -> buildSection(it)
        }
    }

    private fun buildSection(it: SitecoreNoteDetailsComponent) {
        appendItem(
            HouseNoteDetailHeaderItem(
                it.fields.region.value,
                it.fields.title.value,
                it.fields.subheader.value,
                it.fields.author.value.toUpperCase(Locale.US),
                ISODateTimeFormat.dateTimeParser().parseDateTime(it.fields.publishDate.value)
                    .toDate().getFormattedDate()
            )
        )

        it.placeholders.editorial.forEach {
            buildSections(it)
        }

        if (it.fields.footer.value.isNotEmpty()) {
            appendItem(HouseNoteDetailInformationItem(it.fields.footer.value))
        }

        if (it.fields.credits.value.isNotEmpty()) {
            appendItem(HouseNoteDetailCreditItem(it.fields.credits.value))
        }
    }

    private fun buildHeader(it: SitecoreNoteDetailsComponent) {
        when (it.params.mode) {
            "image" -> {
                _header.value =
                    (HouseNoteDetailHeaderImageItem(it.fields.mainImage?.value?.imageLarge ?: ""))
            }
            "video" -> {
                _header.value = buildHeaderVideoItem(it)
            }
        }
    }

    private fun buildSection(it: GalleryStackComponent) {

        appendItem(
            HouseNoteDetailImageCarouselBlockItem(credit = "",
                collaboration = "",
                images = it.fields.images.map { it.toAdapterItem() })
        )
    }

    private fun buildSection(component: LandscapeImageComponent) {
        component.fields.image.value.src.isNotEmpty {
            appendItem(
                HouseNoteDetailIBodyImageBlockItem(
                    it,
                    component.fields.image.value.aspectRatio,
                    component.fields.description.value
                )
            )
        }
    }

    private fun buildSection(component: HeroComponent) {
        component.fields.mainVideo.value.isNotEmpty {
            appendVideo(component, it)
        }

        component.fields.mainImage.value.src.isNotEmpty {
            appendItem(
                HouseNoteDetailIBodyImageBlockItem(
                    it,
                    component.fields.mainImage.value.aspectRatio, ""
                )
            )
        }

        component.fields.title.value.isNotEmpty {
            appendItem(
                HouseNoteDetailHeaderTextBlockItem(
                    it,
                    component.fields.headingSize.value.toLowerCase(Locale.US)
                        .asEnumOrDefault(HouseNoteDetailHeaderTextBlockItem.HeadingStyle.h1)!!,
                    Gravity.CENTER
                )
            )
        }
    }

    private fun appendVideo(component: HeroComponent, videoUrl: String) {
        val isSoundlessLoop = component.fields.loop && component.fields.muted

        val isVimeo = videoUrl.contains("vimeo.")

        val videoBlockItem = if (isVimeo)
            HouseNoteDetailVimeoVideoBlockItem(
                videoUrl = component.fields.mainVideo.value,
                startTime = 0,
                thumbnail = null,
                videoTime = 0,
                layout = if (isSoundlessLoop) "" else "cropped",
                isSoundlessLoop = isSoundlessLoop
            ) else {
            HouseNoteDetailYoutubeVideoBlockItem(
                videoUrl = component.fields.mainVideo.value,
                youtubeVideoId = videoUrl.substringAfterLast('/'),
                startTime = 0,
                showControls = false,
                thumbnail = null,
                videoTime = 0,
                layout = if (isSoundlessLoop) "" else "cropped",
                isSoundlessLoop = isSoundlessLoop
            )
        }

        appendItem(videoBlockItem)
    }

    private fun buildSection(component: SitecoreEditorialPortraitComponent) {
        component.fields.title.value.isNotEmpty {
            appendItem(HouseNoteDetailTitleBlockItem(it))
        }
        component.fields.image.value.src?.isNotEmpty {
            appendItem(
                HouseNoteDetailIBodyImageBlockItem(
                    it,
                    component.fields.image.value.aspectRatio,
                    component.fields.tag.value
                )
            )
        }

    }

    private fun buildSection(component: SitecoreEditorialLandscapeComponent) {
        component.fields.title.value.isNotEmpty {
            appendItem(HouseNoteDetailTitleBlockItem(it))
        }
        component.fields.image.value.src?.isNotEmpty {
            appendItem(
                HouseNoteDetailIBodyImageBlockItem(
                    it,
                    component.fields.image.value.aspectRatio,
                    component.fields.description.value
                )
            )
        }
    }

    private fun buildSection(component: TileImageComponent) {
        component.fields.image.value.src.isNotEmpty {
            appendItem(
                HouseNoteDetailIBodyImageBlockItem(
                    it,
                    component.fields.image.value.aspectRatio,
                    component.fields.tag.value
                )
            )
        }
    }

    private fun buildSection(it: TileImageContainerComponent) {
        it.placeholders.tileImages?.nullIfEmpty()?.let { components ->
            val images = components.map {
                HouseNoteDetailImageCarouselBlockItem.Image(
                    it.fields.title.value,
                    imageCredit = "",
                    imageLarge = it.fields.image.value.src,
                    inverseAspectRatio = it.fields.image.value.inverseAspectRatio,
                    tag = it.fields.tag.value
                )
            }
            appendItem(
                HouseNoteDetailImageCarouselBlockItem(
                    credit = "",
                    collaboration = "",
                    images = images
                )
            )
        }
    }

    private fun buildSection(component: EditorialQuoteComponent) {
        component.fields.title.value.isNotEmpty {
            appendItem(HouseNoteDetailTitleBlockItem(it))
        }

        component.fields.description.value.isNotEmpty {
            appendItem(HouseNoteDetailHeaderTextBlockItem(it, HeadingStyle.h2, Gravity.START))
        }

        component.fields.tag.value.isNotEmpty {
            appendItem(HouseNoteDetailQuoteBlockItem(it))
        }
    }

    private fun buildSection(it: EditorialHeaderComponent) {
        appendItem(
            HouseNoteDetailHeaderTextBlockItem(
                it.fields.title.value,
                it.fields.headingSize.value.toLowerCase(Locale.US)
                    .asEnumOrDefault(HouseNoteDetailHeaderTextBlockItem.HeadingStyle.h1)!!,
                Gravity.CENTER
            )
        )
    }

    private fun buildSection(item: SitecoreThreeImageGalleryComponent) {
        item.images.forEach { image ->
            val url = image.src
            val aspectRatio =
                (image.width.toFloatOrNull() ?: 1f) / (image.height.toFloatOrNull() ?: 1f)
            url.isNotEmpty {
                appendItem(HouseNoteDetailIBodyImageBlockItem(url, aspectRatio, ""))
            }
        }
    }

    private fun buildSection(component: SitecoreTwoImageGalleryComponent) {
        component.images.forEach { image ->
            val url = image.src
            val aspectRatio =
                (image.width.toFloatOrNull() ?: 1f) / (image.height.toFloatOrNull() ?: 1f)
            url.isNotEmpty {
                appendItem(HouseNoteDetailIBodyImageBlockItem(url, aspectRatio, ""))
            }
        }
    }

    private fun buildSection(component: SitecoreEditorialRichTextComponent) {
        component.fields.text.value.isNotEmpty {
            appendItem(HouseNoteDetailTextBlockItem(component.fields.text.value))
        }
    }

    private fun buildSection(component: SitecoreEditorialBodyThreeComponent) {
        component.fields.title.value.isNotEmpty {
            appendItem(HouseNoteDetailTitleBlockItem(component.fields.title.value))
        }
        component.fields.description.value.isNotEmpty {
            appendItem(HouseNoteDetailTextBlockItem(component.fields.description.value))
        }
    }

    private fun buildSection(component: SitecoreEditorialFullImageComponent) {

        val mobileImage = component.fields.mobileImage

        val width: Float = mobileImage.value.width.toFloatOrNull() ?: 1f
        val height: Float = mobileImage.value.height.toFloatOrNull() ?: 1f

        val aspectRatio = width / height
        mobileImage.value.src.isNotEmpty {
            appendItem(HouseNoteDetailIBodyImageBlockItem(it, aspectRatio, ""))
        }
    }

    private fun buildHeaderVideoItem(item: SitecoreNoteDetailsComponent): HouseNoteDetailYoutubeVideoBlockItem {
        return (HouseNoteDetailYoutubeVideoBlockItem(
            videoUrl = item.fields.mainVideo.value,
            youtubeVideoId = item.fields.mainVideo.value.substringAfterLast('/'),
            startTime = item.fields.startTime.value.toIntOrNull() ?: 0,
            showControls = false,
            thumbnail = item.fields.customImage?.value?.headerImageLarge,
            layout = "",
            isSoundlessLoop = item.params.loop && item.params.muted
        ))
    }

    private fun buildSection(it: SitecoreCarouselGalleryComponent) {
        appendItem(
            HouseNoteDetailImageCarouselBlockItem(it.fields.credit.value,
                it.fields.collaboration.value,
                it.fields.images.map { it.toAdapterItem() })
        )
    }

    private fun SitecoreCarouselGalleryComponent.CarouselImageWrapper.toAdapterItem(): HouseNoteDetailImageCarouselBlockItem.Image {
        return HouseNoteDetailImageCarouselBlockItem.Image(
            fields.image.imageCaption,
            fields.image.imageCredit,
            fields.image.imageLarge,
            fields.image.inverseAspectRatio
        )
    }

    private fun buildSection(it: SitecoreGridGalleryComponent) {
        it.fields.images.forEach { image ->

            val caption = when {
                (image.fields.gridImage.imageCredit.isNotEmpty() && image.fields.gridImage.imageCaption.isNotEmpty()) -> {
                    "${image.fields.gridImage.imageCaption} ${image.fields.gridImage.imageCredit}"
                }
                image.fields.gridImage.imageCaption.isNotEmpty() -> image.fields.gridImage.imageCaption
                else -> image.fields.gridImage.imageCredit
            }

            val width = image.fields.gridImage.width.toFloatOrNull() ?: 1f
            val height = image.fields.gridImage.height.toFloatOrNull() ?: 1f
            val aspectRatio = width / height

            appendItem(
                HouseNoteDetailGridImageBlockItem(
                    image.fields.gridImage.imageLarge,
                    caption,
                    aspectRatio
                )
            )
        }

    }

    private fun buildSection(it: SitecoreBodyEditorialVideoComponent) {
        val isSoundlessLoop = it.params.loop && it.params.muted

        val thumbnail = it.fields.customImage?.value?.headerVideoImageLarge

        val url = it.fields.mainVideo.value

        val startsAt = it.fields.starTime.value.toIntOrNull() ?: 0

        val youtubeVideoId = url.substringAfterLast("/")

        appendItem(
            HouseNoteDetailYoutubeVideoBlockItem(
                videoUrl = url,
                youtubeVideoId = youtubeVideoId,
                startTime = startsAt,
                showControls = true,
                thumbnail = thumbnail,
                layout = it.params.videoWidth,
                isSoundlessLoop = isSoundlessLoop
            )
        )

    }

    private fun buildSection(component: SitecoreBodyEditorialBlockComponent) {
        if (component.fields.paragraph.value.isNotEmpty()) {
            appendItem(HouseNoteDetailTextBlockItem(component.fields.paragraph.value))
        }
        if (component.fields.imageTitle.value.isNotEmpty()) {
            appendItem(HouseNoteDetailTitleBlockItem(component.fields.imageTitle.value))
        }
        component.fields.image?.value?.let { image ->
            val aspectRatio = image.width.toFloatOrNull()?.div(image.height.toFloatOrNull() ?: 1f)
                ?: 1f //TODO aspectRatio func
            image.imageLarge.isNotEmpty {
                appendItem(
                    HouseNoteDetailIBodyImageBlockItem(
                        it,
                        aspectRatio,
                        component.fields.imageCaption.value
                    )
                )
            }
        }
        if (component.fields.imageCaption.value.isNotEmpty()) {
            appendItem(HouseNoteDetailImageCaptionBlockItem(component.fields.imageCaption.value))
        }
        if (component.fields.quote.value.isNotEmpty()) {
            appendItem(HouseNoteDetailQuoteBlockItem(component.fields.quote.value))
        }
    }

    override fun reloadDataAfterError() {
        fetchHouseNote(articleSlug)
    }

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.HouseNotesDetail.name)
    }
}

private fun GalleryStackComponent.ImageWrapper.toAdapterItem(): HouseNoteDetailImageCarouselBlockItem.Image {
    with(fields.image.value) {
        return HouseNoteDetailImageCarouselBlockItem.Image(
            imageCaption = imageCaption,
            imageCredit = imageCredit,
            imageLarge = src,
            inverseAspectRatio = inverseAspectRatio
        )
    }
}
