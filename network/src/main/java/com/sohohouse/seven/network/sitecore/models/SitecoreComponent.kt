package com.sohohouse.seven.network.sitecore.models

import com.squareup.moshi.Json

sealed class SitecoreComponent(@Json(name = "componentName") val componentName: String) {

    enum class Type {
        NoteDetails,
        BodyEditorialBlock,
        CarouselGallery,
        BodyEditorialVideo,
        GridGallery,
        EditorialRichText,
        EditorialBodyThree,
        EditorialFullImage,
        TwoImageGallery,
        ThreeImageGallery,
        EditorialLandscapeImage,
        EditorialPortraitImage,
        Hero,
        GalleryStack,
        LandscapeImage,
        Empty,
        TileImage,
        TileImageContainer,
        EditorialHeader,
        EditorialQuote,
        Unknown;
    }

}

class EmptyComponent(val componentType: String = Type.Unknown.name) :
    SitecoreComponent(Type.Empty.name)

data class SitecoreBodyEditorialBlockComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.BodyEditorialBlock.name) {

    data class Fields(
        val imageCaption: Value<String> = Value(""),
        val imageTitle: Value<String> = Value(""),
        val image: Value<BodyImage>? = null,
        val quote: Value<String> = Value(""),
        val paragraph: Value<String> = Value(""),
    )

    data class BodyImage(
        val src: String = "",
        @Json(name = "cloudinary_body_image_url_small") val imageBdySmall: String = "",
        @Json(name = "cloudinary_body_image_url_large") val imageBodyLarge: String = "",
        @Json(name = "cloudinary_image_url_small") val imageSmall: String = "",
        @Json(name = "cloudinary_image_url_large") val imageLarge: String = "",
        val id: String = "",
        val alt: String = "",
        val width: String = "",
        val height: String = "",
    )

}

data class SitecoreBodyEditorialVideoComponent(
    val uid: String = "",
    val dataSource: String = "",
    val params: Params = Params(),
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.BodyEditorialVideo.name) {


    data class Params(
        val videoWidth: String = "",
        val playSettings: String = "",
        @Json(name = "muted") private val _muted: String = "0",
        @Json(name = "loop") private val _loop: String = "0",
    ) {
        val muted: Boolean get() = "1" == _muted
        val loop: Boolean get() = "1" == _loop
    }

    data class Fields(
        val hiddenVideoTitle: Value<String> = Value(""),
        val mainVideo: Value<String> = Value(""),
        val starTime: Value<String> = Value(""),
        val customImage: Value<ImageInfo>? = null,
    )

    data class ImageInfo(
        val src: String = "",
        @Json(name = "cloudinary_header_video_image_small") val headerVideoImageSmall: String = "",
        @Json(name = "cloudinary_header_video_image_large") val headerVideoImageLarge: String = "",
        @Json(name = "cloudinary_video_image_small") val videoImageSmall: String = "",
        @Json(name = "cloudinary_video_image_large") val videoImageLarge: String = "",
        val id: String = "",
        val alt: String = "",
        val width: String = "",
        val height: String = "",
    )

}

data class SitecoreCarouselGalleryComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.CarouselGallery.name) {

    data class Fields(
        val credit: Value<String> = Value(""),
        val collaboration: Value<String> = Value(""),
        val images: List<CarouselImageWrapper> = emptyList(),
    )

    data class CarouselImageWrapper(val fields: Fields = Fields()) {
        data class Fields(val image: CarouselImage = CarouselImage())
    }

    data class CarouselImage(
        val alt: String = "",
        val imageCaption: String = "",
        val imageCredit: String = "",
        val src: String = "",
        @Json(name = "cloudinary_image_url_small") val imageSmall: String = "",
        @Json(name = "cloudinary_image_url_large") val imageLarge: String = "",
        val id: String = "",
        @Json(name = "width") private val _width: String = "",
        @Json(name = "height") private val _height: String = "",
    ) {
        val width: Float? = _width.toFloatOrNull()
        val height: Float? = _height.toFloatOrNull()

        val inverseAspectRatio: Float
            get() {
                if (width == null || height == null) return 1f
                return height / width
            }
    }

}

data class SitecoreGridGalleryComponent(
    val uid: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.GridGallery.name) {

    data class Fields(
        val credit: Value<String> = Value(""),
        val images: List<GridImageWrapper> = emptyList(),
    )

    data class GridImageWrapper(val fields: Fields = Fields()) {

        data class Fields(
            @Json(name = "image") private val _gridImage: Value<GridImage> = Value(GridImage()),
        ) {
            val gridImage get() = _gridImage.value
        }

    }

    data class GridImage(
        val alt: String = "",
        val width: String = "",
        val height: String = "",
        val imageCaption: String = "",
        val imageCredit: String = "",
        val src: String = "",
        @Json(name = "cloudinary_image_url_small") val imageSmall: String = "",
        @Json(name = "cloudinary_image_url_large") val imageLarge: String = "",
        val id: String = "",
    )

}

data class SitecoreNoteDetailsComponent(
    val uid: String = "",
    val dataSource: String = "",
    val params: Params,
    val fields: Fields,
    val placeholders: Placeholders,
) : SitecoreComponent(Type.NoteDetails.name) {

    data class Params(
        val mode: String = "",
        val playSettings: String = "",
        @Json(name = "muted") private val _muted: String = "0",
        @Json(name = "loop") private val _loop: String = "0",
    ) {
        val muted: Boolean = "1" == _muted
        val loop: Boolean = "1" == _loop
    }

    data class Fields(
        val subheader: Value<String> = Value(""),
        val author: Value<String> = Value(""),
        val region: Value<String> = Value(""),
        val venues: List<SitecoreRelation>,
        val mainImage: Value<HeaderImage>? = null,
        val mainVideo: Value<String> = Value(""),
        val customImage: Value<ThumbnailImage>? = null,
        val startTime: Value<String> = Value(""),
        val footer: Value<String> = Value(""),
        val publishDate: Value<String> = Value(""),
        val title: Value<String> = Value(""),
        val categories: List<SitecoreCategory> = emptyList(),
        val hidden: Value<Boolean> = Value(false),
        val credits: Value<String> = Value(""),
        val shortDescription: Value<String> = Value(""),
    )

    data class HeaderImage(
        val src: String = "",
        @Json(name = "cloudinary_header_image_small") val imageSmall: String = "",
        @Json(name = "cloudinary_header_image_medium") val imageMedium: String = "",
        @Json(name = "cloudinary_header_image_large") val imageLarge: String = "",
        @Json(name = "cloudinary_header_image_xlarge") val imageExtraLarge: String = "",
        val id: String = "",
        val alt: String = "",
        val width: Int = 0,
        val height: Int = 0,
    )

    data class ThumbnailImage(
        val src: String = "",
        @Json(name = "cloudinary_header_video_image_large") val headerImageLarge: String = "",
        @Json(name = "cloudinary_header_video_image_small") val headerImageSmall: String = "",
        @Json(name = "cloudinary_video_image_large") val imageLarge: String = "",
        @Json(name = "cloudinary_video_image_small") val imageSmall: String = "",
        val id: String = "",
        val alt: String = "",
        val width: String = "",
        val height: String = "",
    )

    data class Placeholders(@Json(name = "digital-house-editorial-block") val editorial: List<SitecoreComponent>)

}

data class SitecoreEditorialRichTextComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.EditorialRichText.name) {

    data class Fields(
        val text: Value<String> = Value(""),
        val ctaAppearance: Value<String> = Value(""),
        val cta1: Value<Cta> = Value(Cta("")),
        val cta2: Value<Cta> = Value(Cta("")),
        val button1Color: Value<String> = Value(""),
        val button2Color: Value<String> = Value(""),
    ) {
        data class Cta(val href: String = "")

    }

}

data class SitecoreEditorialBodyThreeComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.EditorialBodyThree.name) {
    data class Fields(
        val title: Value<String> = Value(""),
        val description: Value<String> = Value(""),
        val titleAlignmentDesktop: Value<String> = Value(""),
        val titleAlignmentMobile: Value<String> = Value(""),
        val ctaAppearance: Value<String> = Value(""),
        val cta1: Value<String> = Value(""),
        val cta2: Value<String> = Value(""),
        val button1color: Value<String> = Value(""),
        val button2color: Value<String> = Value(""),
    )
}

data class SitecoreEditorialFullImageComponent(
    val id: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.EditorialFullImage.name) {

    data class Fields(
        val mobileImage: Value<Image> = Value(Image()),
        val desktopImage: Value<Image> = Value(Image()),
    )

    data class Image(
        val src: String = "",
        @Json(name = "cloudinary_3x4_fill_crop") val portraitFillCrop: String = "",
        @Json(name = "cloudinary_4x3_fill_crop") val landscapeFillCrop: String = "",
        val id: String = "",
        val alt: String = "",
        val width: String = "",
        val height: String = "",
    )

}

data class SitecoreTwoImageGalleryComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.TwoImageGallery.name) {

    data class Fields(
        val imagesAlignment: Value<String> = Value(""),
        val landscapeImage: Value<Image> = Value(Image()),
        val portraitImage: Value<Image> = Value(Image()),
    )

    data class Image(
        val src: String = "",
        @Json(name = "cloudinary_3x4_fill_crop") val portraitFillCrop: String = "",
        @Json(name = "cloudinary_4x3_fill_crop") val landscapeFillCrop: String = "",
        val id: String = "",
        val alt: String = "",
        val width: String = "",
        val height: String = "",
    )

    val images: List<Image> = listOf(fields.landscapeImage.value, fields.portraitImage.value)

}

data class SitecoreThreeImageGalleryComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.ThreeImageGallery.name) {
    data class Fields(
        val imagesAlignment: Value<String> = Value(""),
        val firstImage: Value<Image> = Value(Image()),
        val secondImage: Value<Image> = Value(Image()),
        val thirdImage: Value<Image> = Value(Image()),
    )

    data class Image(
        val src: String = "",
        @Json(name = "cloudinary_3x4_fill_crop") val portraitFillCrop: String = "",
        @Json(name = "cloudinary_4x3_fill_crop") val landscapeFillCrop: String = "",
        val id: String = "",
        val alt: String = "",
        val width: String = "",
        val height: String = "",
    )

    val images: List<Image> =
        listOf(fields.firstImage.value, fields.secondImage.value, fields.thirdImage.value)
}

data class SitecoreEditorialLandscapeComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.EditorialLandscapeImage.name) {

    data class Fields(
        val headingSize: Value<String> = Value(""),
        val image: Value<Image> = Value(Image()),
        val tag: Value<String> = Value(""),
        val style: Value<String> = Value(""),
        val hideDescriptionForMobile: Value<String> = Value(""),
        val cta: Value<CallToAction> = Value(CallToAction()),
        val description: Value<String> = Value(""),
        val title: Value<String> = Value(""),
    ) {

        data class CallToAction(val href: String = "")
    }

    data class Image(
        val src: String? = "",
        @Json(name = "cloudinary_4x3_fill_crop") val landscapeFillCrop: String = "",
        val id: String = "",
        val alt: String = "",
        @Json(name = "width") private val _width: String = "",
        @Json(name = "height") private val _height: String = "",
    ) {
        val width: Float? = _width.toFloatOrNull()
        val height: Float? = _height.toFloatOrNull()

        val aspectRatio: Float
            get() {
                if (width == null || height == null) return 1f
                return width / height
            }
    }

}

data class SitecoreEditorialPortraitComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.EditorialPortraitImage.name) {

    data class Fields(
        val headingSize: Value<String> = Value(""),
        val link: Value<Link> = Value(Link()),
        val title: Value<String> = Value(""),
        val image: Value<Image> = Value(Image()),
        val tag: Value<String> = Value(""),
        val imagePosition: Value<String> = Value(""),
        val style: Value<String> = Value(""),
        val textColor: Value<ContentColor> = Value(ContentColor()),
        val textBlockBackground: Value<ContentColor> = Value(ContentColor()),
    )

    data class Link(
        val href: String = "",
        val id: String = "",
        @Json(name = "querystring") val queryString: String = "",
        @Json(name = "data-hidden-text") val dataHiddenText: String = "",
        val linkType: String = "",
        val text: String = "",
        val anchor: String = "",
        val url: String = "",
        val title: String = "",
        @Json(name = "class") val linkClass: String = "",
        val target: String = "",
    )

    data class Image(
        val src: String? = "",
        @Json(name = "cloudinary_3x4_fill_crop") val portraitFillCrop: String = "",
        val id: String = "",
        val alt: String = "",
        @Json(name = "width") private val _width: String = "",
        @Json(name = "height") private val _height: String = "",
    ) {
        val width: Float? = _width.toFloatOrNull()
        val height: Float? = _height.toFloatOrNull()

        val aspectRatio: Float
            get() {
                if (width == null || height == null) return 1f
                return width / height
            }
    }

}

data class HeroComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.Hero.name) {
    data class Fields(
        val ctaAppearance: Value<String> = Value(""),
        val cta1: Value<SitecoreCTALink> = Value(SitecoreCTALink()),
        val cta2: Value<SitecoreCTALink> = Value(SitecoreCTALink()),
        val button1Color: Value<String> = Value(""),
        val button2Color: Value<String> = Value(""),
        val mainImage: Value<MainImage> = Value(MainImage()),
        val headingSize: Value<String> = Value(""),
        val textPosition: Value<String> = Value(""),
        val title: Value<String> = Value(""),
        val textColor: Value<ContentColor> = Value(ContentColor()),
        val autoplay: Value<Boolean> = Value(false),
        @Json(name = "loop") private val _loop: Value<Boolean> = Value(false),
        @Json(name = "mute") private val _muted: Value<Boolean> = Value(false),
        val hiddenVideoTitle: Value<String> = Value(""),
        val mainVideo: Value<String> = Value(""),
    ) {
        val muted: Boolean = _muted.value
        val loop: Boolean = _loop.value
    }


    data class MainImage(
        val src: String = "",
        @Json(name = "cloudinary_5x3_fill_crop") val fillCrop5x3: String = "",
        @Json(name = "cloudinary_3x4_fill_crop") val fillCrop3x4: String = "",
        @Json(name = "cloudinary_16x9_fill_crop") val fillCrop16x9: String = "",
        @Json(name = "cloudinary_1x1_fill_crop") val fillCrop1x1: String = "",
        @Json(name = "cloudinary_4x3_fill_crop") val fillCrop4x3: String = "",
        val id: String = "",
        val alt: String = "",
        @Json(name = "width") private val _width: String = "",
        @Json(name = "height") private val _height: String = "",
    ) {
        val width: Float? = _width.toFloatOrNull()
        val height: Float? = _height.toFloatOrNull()

        val aspectRatio: Float
            get() {
                if (width == null || height == null) return 1f
                return width / height
            }
    }


}

data class GalleryStackComponent(
    val id: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.GalleryStack.name) {
    data class Fields(val images: List<ImageWrapper> = emptyList())

    data class ImageWrapper(val fields: Fields = Fields()) {
        data class Fields(val image: Value<Image> = Value(Image()))
    }

    data class Image(
        val id: String = "",
        val src: String = "",
        @Json(name = "cloudinary_5x3_fill_crop") val fillCrop5x3: String = "",
        @Json(name = "cloudinary_3x4_fill_crop") val fillCrop3x4: String = "",
        @Json(name = "cloudinary_16x9_fill_crop") val fillCrop16x9: String = "",
        @Json(name = "cloudinary_1x1_fill_crop") val fillCrop1x1: String = "",
        @Json(name = "cloudinary_4x3_fill_crop") val fillCrop4x3: String = "",
        val alt: String = "",
        val imageCaption: String = "",
        val imageCredit: String = "",
        @Json(name = "width") private val _width: String = "",
        @Json(name = "height") private val _height: String = "",
    ) {
        val width: Float? = _width.toFloatOrNull()
        val height: Float? = _height.toFloatOrNull()

        val inverseAspectRatio: Float
            get() {
                if (width == null || height == null) return 1f
                return height / width
            }
    }
}

data class LandscapeImageComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.LandscapeImage.name) {
    data class Fields(
        val image: Value<Image> = Value(Image()),
        val textPosition: Value<String> = Value(""),
        val description: Value<String> = Value(""),
        val title: Value<String> = Value(""),
        val link: Value<Link> = Value(Link()),
    ) {
        data class Link(
            val href: String = "",
            val linkType: String = "",
            val text: String = "",
            val querystring: String = "",
            val target: String = "",
            val id: String = "",
        )
    }

    data class Image(
        val src: String = "",
        @Json(name = "cloudinary_4x3_fill_crop") val landscapeFillCrop: String = "",
        val id: String = "",
        val alt: String = "",
        @Json(name = "width") private val _width: String = "",
        @Json(name = "height") private val _height: String = "",
    ) {
        val width: Float? = _width.toFloatOrNull()
        val height: Float? = _height.toFloatOrNull()

        val aspectRatio: Float
            get() {
                if (width == null || height == null) return 1f
                return width / height
            }
    }
}

data class TileImageComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.TileImage.name) {
    data class Fields(
        val ctaAppearance: Value<String> = Value(""),
        val cta: Value<SitecoreCTALink> = Value(SitecoreCTALink()),
        val tag: Value<String> = Value(""),
        val largeHeader: Value<String> = Value(""),
        val title: Value<String> = Value(""),
        val image: Value<Image> = Value(Image()),
    )

    data class Image(
        val src: String = "",
        @Json(name = "cloudinary_5x3_fill_crop") val fillCrop5x3: String = "",
        @Json(name = "cloudinary_3x4_fill_crop") val fillCrop3x4: String = "",
        @Json(name = "cloudinary_16x9_fill_crop") val fillCrop16x9: String = "",
        @Json(name = "cloudinary_1x1_fill_crop") val fillCrop1x1: String = "",
        @Json(name = "cloudinary_4x3_fill_crop") val fillCrop4x3: String = "",
        val id: String = "",
        val alt: String = "",
        @Json(name = "width") private val _width: String = "",
        @Json(name = "height") private val _height: String = "",
    ) {
        val width: Float? = _width.toFloatOrNull()
        val height: Float? = _height.toFloatOrNull()

        val aspectRatio: Float
            get() {
                if (width == null || height == null) return 1f
                return width / height
            }

        val inverseAspectRatio: Float
            get() {
                if (width == null || height == null) return 1f
                return height / width
            }
    }
}

data class TileImageContainerComponent(
    val uid: String = "",
    val dataSource: String = "",
    val placeholders: Placeholders = Placeholders(),
) : SitecoreComponent(Type.TileImageContainer.name) {
    data class Placeholders(
        @Json(name = "digital-house-2h-container-left-tile") val leftTile: List<SitecoreComponent> = emptyList(),
        @Json(name = "digital-house-2h-container-right-tile") val rightTile: List<SitecoreComponent> = emptyList(),
    ) {
        val tileImages: List<TileImageComponent>?
            get() {
                if (leftTile.firstOrNull() is TileImageComponent && rightTile.firstOrNull() is TileImageComponent) {
                    return listOf(leftTile.first() as TileImageComponent,
                        rightTile.first() as TileImageComponent)
                }
                return null
            }
    }
}

data class EditorialQuoteComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.EditorialQuote.name) {
    data class Fields(
        val button1Color: Value<String> = Value(""),
        val button2Color: Value<String> = Value(""),
        val ctaAppearance: Value<String> = Value(""),
        val cta1: Value<Cta> = Value(Cta()),
        val cta2: Value<Cta> = Value(Cta()),
        val description: Value<String> = Value(""),
        val tag: Value<String> = Value(""),
        val title: Value<String> = Value(""),
    ) {
        data class Cta(val href: String = "")
    }
}

data class EditorialHeaderComponent(
    val uid: String = "",
    val dataSource: String = "",
    val fields: Fields = Fields(),
) : SitecoreComponent(Type.EditorialHeader.name) {
    data class Fields(
        val title: Value<String> = Value(""),
        val position: Value<String> = Value(""),
        val headingSize: Value<String> = Value(""),
        val ctaAppearance: Value<String> = Value(""),
        val cta1: Value<SitecoreCTALink> = Value(SitecoreCTALink()),
        val cta2: Value<SitecoreCTALink> = Value(SitecoreCTALink()),
        val button1Color: Value<String> = Value(""),
        val button2Color: Value<String> = Value(""),
    )
}

data class UnknownComponent(val json: String) : SitecoreComponent(Type.Unknown.name)