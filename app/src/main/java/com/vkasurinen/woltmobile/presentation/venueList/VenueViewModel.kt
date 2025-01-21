package com.vkasurinen.woltmobile.presentation.venueList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.woltmobile.domain.repository.WoltRepository
import com.vkasurinen.woltmobile.util.Resource
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
    val state = _state.asStateFlow()
//    val state: StateFlow<VenueState> = _state


    init {
        loadVenues()
    }

    fun onAction(event: VenueUiEvent) {
        when (event) {
            is VenueUiEvent.LoadVenues -> {
                loadVenues()
            }
            is VenueUiEvent.UpdateFavoriteStatus -> {
                updateFavoriteStatus(event.id, event.isFavorite)
            }
        }
    }

    private fun loadVenues() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.getVenues(60.169418, 24.931618, false).collectLatest { resource ->
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
                        _state.update { it.copy(isLoading = false) }
                    }

                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = resource.isLoading) }
                    }
                }
            }
        }
    }

    private fun updateFavoriteStatus(id: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(id, isFavorite)
        }
    }
}