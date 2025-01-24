package com.vkasurinen.woltmobile.presentation.favorites

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

class FavoriteViewModel(
    private val repository: WoltRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SharedState())
    val state: StateFlow<SharedState> = _state.asStateFlow()

    init {
        loadFavoriteVenues()
    }

    private fun loadFavoriteVenues() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getFavoriteVenues().collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { venues ->
                            _state.update {
                                it.copy(
                                    venues = venues,
                                    isLoading = false
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = resource.message) }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = resource.isLoading) }
                    }
                }
            }
        }
    }

    fun refreshFavoriteVenues() {
        loadFavoriteVenues()
    }

    fun toggleFavorite(venueId: String) {
        viewModelScope.launch {
            try {
                val currentVenues = _state.value.venues.toMutableList()
                val venueIndex = currentVenues.indexOfFirst { it.id == venueId }
                if (venueIndex != -1) {
                    val venue = currentVenues[venueIndex]
                    val updatedVenue = venue.copy(isFavorite = !venue.isFavorite)
                    currentVenues[venueIndex] = updatedVenue
                    _state.update { it.copy(venues = currentVenues) }
                    repository.updateFavoriteStatus(venueId, updatedVenue.isFavorite)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error updating favorite status: ${e.message}") }
            }
        }
    }
}