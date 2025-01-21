package com.vkasurinen.woltmobile.presentation.venueList

import WoltRepository
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.woltmobile.domain.model.WoltModel
import com.vkasurinen.woltmobile.presentation.venueList.VenueState
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

    private val _favoriteVenueIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteVenueIds: StateFlow<Set<String>> = _favoriteVenueIds.asStateFlow()

    private var currentCoordinateIndex = 0
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

    init {
        Log.d("VenueViewModel", "Initializing ViewModel")
        loadFavoriteVenues()
        updateLocationPeriodically()
    }

    private fun loadFavoriteVenues() {
        viewModelScope.launch {
            Log.d("VenueViewModel", "Loading favorite venues...")
            repository.getFavoriteVenues().collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { venues ->
                            Log.d("VenueViewModel", "Favorite venues loaded: ${venues.map { it.name }}")
                            _favoriteVenueIds.value = venues.map { it.id }.toSet()
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(error = resource.message) }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun updateLocationPeriodically() {
        viewModelScope.launch {
            Log.d("VenueViewModel", "Starting periodic location updates")
            while (true) {
                val (latitude, longitude) = coordinates[currentCoordinateIndex]
                Log.d("VenueViewModel", "Fetching venues for coordinates: ($latitude, $longitude)")
                loadVenues(latitude, longitude)
                currentCoordinateIndex = (currentCoordinateIndex + 1) % coordinates.size
                delay(10000) // 10 seconds
            }
        }
    }

    private fun loadVenues(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getVenues(latitude, longitude, forceFetchFromRemote = false).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { venues ->
                            val updatedVenues = venues.map { venue ->
                                venue.copy(isFavorite = _favoriteVenueIds.value.contains(venue.id))
                            }
                            _state.update {
                                it.copy(venues = updatedVenues.take(15), isLoading = false)
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

    fun toggleFavorite(venueId: String) {
        viewModelScope.launch {
            val isNowFavorite = !_favoriteVenueIds.value.contains(venueId)
            _favoriteVenueIds.value = if (isNowFavorite) {
                _favoriteVenueIds.value + venueId
            } else {
                _favoriteVenueIds.value - venueId
            }

            repository.updateFavoriteStatus(venueId, isNowFavorite)
            Log.d("VenueViewModel", "Favorite status updated for venueId: $venueId, isFavorite: $isNowFavorite")

            _state.update { currentState ->
                val updatedVenues = currentState.venues.map { venue ->
                    if (venue.id == venueId) venue.copy(isFavorite = isNowFavorite) else venue
                }
                currentState.copy(venues = updatedVenues)
            }
        }
    }
}
