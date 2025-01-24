package com.vkasurinen.woltmobile.presentation.venueList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.vkasurinen.woltmobile.presentation.venueList.components.VenueItem
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import com.vkasurinen.woltmobile.presentation.SharedState
import com.vkasurinen.woltmobile.util.Screen


@Composable
fun VenueScreenRoot(
    navController: NavController,
    viewModel: VenueViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.refreshVenues()
    }

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
    state: SharedState,
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
        state = SharedState(),
        onToggleFavorite = {},
        onVenueClick = {}
        //onAction = {}
    )
}