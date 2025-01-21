package com.vkasurinen.woltmobile.presentation.favorites

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vkasurinen.woltmobile.presentation.venueList.VenueState
import com.vkasurinen.woltmobile.presentation.venueList.components.VenueItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteScreenRoot(
    navController: NavController,
    viewModel: FavoriteViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState().value
    FavoriteScreen(
        state = state,
        onToggleFavorite = viewModel::toggleFavorite
    )
}

@Composable
fun FavoriteScreen(
    state: VenueState,
    onToggleFavorite: (String) -> Unit
) {
    Crossfade(targetState = state) { currentState ->
        if (currentState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (currentState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = currentState.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                items(currentState.venues.filter { it.isFavorite }) { venue ->
                    VenueItem(
                        venue = venue,
                        onToggleFavorite = { onToggleFavorite(venue.id) }
                    )
                }
            }
        }
    }
}