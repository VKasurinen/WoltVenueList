package com.vkasurinen.woltmobile.data.mappers

import com.vkasurinen.woltmobile.data.local.VenuePreviewItemEntity
import com.vkasurinen.woltmobile.data.local.WoltEntity
import com.vkasurinen.woltmobile.data.remote.VenueItem
import com.vkasurinen.woltmobile.data.remote.VenuePreviewItem
import com.vkasurinen.woltmobile.domain.model.VenuePreviewItemModel
import com.vkasurinen.woltmobile.domain.model.WoltModel


fun WoltEntity.toWoltModel(): WoltModel {
    return WoltModel(
        id = id,
        name = name,
        imageUrl = imageUrl,
        description = description,
        isFavorite = isFavorite,
        address = address,
        rating = rating,
        shortDescription = shortDescription,
        venuePreviewItems = venuePreviewItems?.map { it.toVenuePreviewItem() }
    )
}

fun VenueItem.toWoltEntity(): WoltEntity {
    return WoltEntity(
        id = venue?.id ?: "",
        name = venue?.name ?: "",
        imageUrl = image?.url ?: "",
        description = venue?.short_description ?: "",
        address = venue?.address ?: "",
        rating = venue?.rating?.score,
        shortDescription = venue?.short_description,
        venuePreviewItems = venue?.venue_preview_items?.map { it.toVenuePreviewItemEntity() } ?: emptyList()
    )
}

fun VenuePreviewItemEntity.toVenuePreviewItem(): VenuePreviewItemModel {
    return VenuePreviewItemModel(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl,
        currency = currency
    )
}

fun VenuePreviewItem.toVenuePreviewItemEntity(): VenuePreviewItemEntity {
    return VenuePreviewItemEntity(
        id = id,
        name = name,
        price = price,
        imageUrl = image?.url,
        currency = currency
    )
}