package com.vkasurinen.woltmobile.presentation.details

import WoltRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.woltmobile.presentation.venueList.VenueState
import com.vkasurinen.woltmobile.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val repository: WoltRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VenueState())
    val state: StateFlow<VenueState> = _state.asStateFlow()

    fun loadVenue(venueId: String) {
        viewModelScope.launch {
            repository.getVenue(venueId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = result.isLoading) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(venue = result.data, isLoading = false) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(error = result.message, isLoading = false) }
                    }
                }
            }
        }
    }

    fun toggleFavorite(venueId: String) {
        viewModelScope.launch {
            try {
                val currentVenue = _state.value.venue
                if (currentVenue != null && currentVenue.id == venueId) {
                    val updatedVenue = currentVenue.copy(isFavorite = !currentVenue.isFavorite)
                    _state.update { it.copy(venue = updatedVenue) }
                    repository.updateFavoriteStatus(venueId, updatedVenue.isFavorite)
                    // Notify other view models about the change
                    repository.getFavoriteVenues().collectLatest { resource ->
                        if (resource is Resource.Success) {
                            val favoriteVenues = resource.data ?: emptyList()
                            val updatedVenues = _state.value.venues.map { venue ->
                                venue.copy(isFavorite = favoriteVenues.any { it.id == venue.id })
                            }
                            _state.update { it.copy(venues = updatedVenues) }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error updating favorite status: ${e.message}") }
            }
        }
    }
}