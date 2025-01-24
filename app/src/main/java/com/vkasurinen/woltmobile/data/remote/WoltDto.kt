package com.vkasurinen.woltmobile.data.remote

data class WoltResponse(
    val sections: List<Section>
)

data class Section(
    val items: List<VenueItem>
)

data class VenueItem(
    val venue: Venue?,
    val image: Image?
)

data class Venue(
    val id: String?,
    val name: String?,
    val short_description: String?,
    val address: String?,
    val rating: Rating?,
    val venue_preview_items: List<VenuePreviewItem>?
)

data class Image(
    val url: String?
)

data class Rating(
    val rating: Int?,
    val score: Double?,
    val volume: Int?
)

data class VenuePreviewItem(
    val id: String?,
    val name: String?,
    val price: Int?,
    val image: Image?,
    val currency: String?
)