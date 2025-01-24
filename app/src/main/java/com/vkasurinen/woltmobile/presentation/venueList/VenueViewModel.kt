package com.vkasurinen.woltmobile.presentation.venueList

import WoltRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.woltmobile.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VenueViewModel(
    private val repository: WoltRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VenueState())
    val state: StateFlow<VenueState> = _state.asStateFlow()

    private val coordinates = listOf(
        Pair(60.169418, 24.931618),
        Pair(60.169818, 24.932906),
        Pair(60.170005, 24.935105),
        Pair(60.169108, 24.936210),
        Pair(60.168355, 24.934869),
        Pair(60.167560, 24.932562),
        Pair(60.168254, 24.931532),
        Pair(60.169012, 24.930341),
        Pair(60.170085, 24.929569)
    )
    private var currentCoordinateIndex = 0

    init {
        Log.d("VenueViewModel", "Initializing ViewModel")
        startLocationUpdates()
        observeFavoriteVenues()
    }

    private fun startLocationUpdates() {
        if (coordinates.isEmpty()) {
            Log.e("VenueViewModel", "Coordinates list is empty")
            return
        }

        viewModelScope.launch {
            while (true) {
                val (latitude, longitude) = coordinates[currentCoordinateIndex]
                loadVenues(latitude, longitude)
                currentCoordinateIndex = (currentCoordinateIndex + 1) % coordinates.size
                delay(10000) // 10 seconds
            }
        }
    }

    private fun loadVenues(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getVenues(latitude = latitude, longitude = longitude, forceFetchFromRemote = false).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }

                    is Resource.Success -> {
                        result.data?.let { venueList ->
                            val limitedVenues = venueList.take(15)
                            _state.update {
                                it.copy(
                                    venues = limitedVenues,
                                    isLoading = false
                                )
                            }
                        }
                    }

                    is Resource.Loading -> {
                        _state.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }

    private fun observeFavoriteVenues() {
        viewModelScope.launch {
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
    }

    fun refreshVenues() {
        val (latitude, longitude) = coordinates[currentCoordinateIndex]
        loadVenues(latitude, longitude)
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