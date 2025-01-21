package com.vkasurinen.woltmobile.presentation.venueList

sealed interface VenueUiEvent {
    data object LoadVenues : VenueUiEvent
    data class UpdateFavoriteStatus(val id: String, val isFavorite: Boolean) : VenueUiEvent
    data object LoadMoreVenues : VenueUiEvent
}