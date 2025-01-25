package com.vkasurinen.woltmobile.presentation.venueList

import WoltRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.woltmobile.presentation.SharedState
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

    private val _state = MutableStateFlow(SharedState())
    val state: StateFlow<SharedState> = _state.asStateFlow()

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
                val (latitude, longitude) = coordinates[currentCoordinateIndex]  // Get the current latitude and longitude from the list of coordinates
                loadVenues(latitude, longitude)

                // Update the current coordinate index to point to the next coordinate
                // Use modulo to loop back to the start when reaching the end of the list
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
            repository.getFavoriteVenues().collectLatest { result -> // Check if the result is a successful response
                if (result is Resource.Success) {
                    val favoriteVenues = result.data ?: emptyList()  // Get the list of favorite venues from the result; use an empty list if null

                    // Update the venues in the current state by mapping through them
                    // For each venue, check if it exists in the favorite venues list and update its isFavorite property
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
                val currentVenues = _state.value.venues.toMutableList() // Get the current list of venues from the state and make a mutable copy
                val venueIndex = currentVenues.indexOfFirst { it.id == venueId }
                if (venueIndex != -1) {
                    val venue = currentVenues[venueIndex] // Retrieve the venue at the found index
                    val updatedVenue = venue.copy(isFavorite = !venue.isFavorite) // Create an updated venue with the `isFavorite` status toggled
                    currentVenues[venueIndex] = updatedVenue // Update the mutable list with the updated venue at the same index
                    _state.update { it.copy(venues = currentVenues) }  // Update the ViewModel's state with the modified list of venues
                    repository.updateFavoriteStatus(venueId, updatedVenue.isFavorite)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error updating favorite status: ${e.message}") }
            }
        }
    }
}