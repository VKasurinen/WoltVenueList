package com.vkasurinen.woltmobile.presentation.details

import WoltRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.woltmobile.presentation.SharedState
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

    private val _state = MutableStateFlow(SharedState())
    val state: StateFlow<SharedState> = _state.asStateFlow()

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
                val currentVenue = _state.value.venue // Retrieve the currently selected venue from the state
                if (currentVenue != null && currentVenue.id == venueId) {
                    val updatedVenue = currentVenue.copy(isFavorite = !currentVenue.isFavorite) // Create an updated venue with the `isFavorite` status toggled
                    _state.update { it.copy(venue = updatedVenue) } // Update the ViewModel's state with the updated venue
                    repository.updateFavoriteStatus(venueId, updatedVenue.isFavorite)
                    // Notify other view models about the change
                    repository.getFavoriteVenues().collectLatest { result ->
                        if (result is Resource.Success) {
                            val favoriteVenues = result.data ?: emptyList()
                            val updatedVenues = _state.value.venues.map { venue ->  // Update the list of venues in the state to reflect the new favorite status
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