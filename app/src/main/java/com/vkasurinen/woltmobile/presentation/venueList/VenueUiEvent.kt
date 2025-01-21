package com.vkasurinen.woltmobile.presentation.venueList

sealed class VenueUiEvent {
    object LoadVenues : VenueUiEvent()
    data class UpdateFavoriteStatus(val id: String, val isFavorite: Boolean) : VenueUiEvent()
}