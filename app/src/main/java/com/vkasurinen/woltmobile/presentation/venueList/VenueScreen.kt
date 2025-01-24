package com.vkasurinen.woltmobile.presentation.venueList

import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.vkasurinen.woltmobile.presentation.venueList.components.VenueItem
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import com.vkasurinen.woltmobile.util.Screen
import kotlinx.coroutines.delay
import java.nio.file.WatchEvent


@Composable
fun VenueScreenRoot(
    navController: NavController,
    viewModel: VenueViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState().value
    VenueScreen(
        state = state,
        onToggleFavorite = viewModel::toggleFavorite,
        onVenueClick = { venueId ->
            navController.navigate("${Screen.Details.route}/$venueId")
        }
    )
}

@Composable
fun VenueScreen(
    state: VenueState,
    onToggleFavorite: (String) -> Unit,
    onVenueClick: (String) -> Unit
) {
    val listState = rememberLazyListState()

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (state.error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = state.error)
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            items(state.venues) { venue ->
                VenueItem(
                    venue = venue,
                    onToggleFavorite = { onToggleFavorite(venue.id) },
                    onClick = { onVenueClick(venue.id) }
                )
                Divider(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .height(1.dp)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun VenueScreenPreview() {
    VenueScreen(
        state = VenueState(),
        onToggleFavorite = {},
        onVenueClick = {}
        //onAction = {}
    )
}