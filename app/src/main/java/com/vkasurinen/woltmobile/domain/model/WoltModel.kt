package com.vkasurinen.woltmobile.domain.model

data class WoltModel(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val address: String,
    val isFavorite: Boolean,
    val rating: Double?,
    val shortDescription: String?,
    val venuePreviewItems: List<VenuePreviewItemModel>?
)

data class VenuePreviewItemModel(
    val id: String?,
    val name: String?,
    val price: Int?,
    val imageUrl: String?,
    val currency: String?
)