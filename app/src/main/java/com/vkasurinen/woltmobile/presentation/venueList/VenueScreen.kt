package com.vkasurinen.woltmobile.presentation.venueList

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vkasurinen.woltmobile.presentation.venueList.components.VenueItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun VenueScreenRoot(
    navController: NavController,
    viewModel: VenueViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState().value
    VenueScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun VenueScreen(
    state: VenueState,
    onAction: (VenueUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {

        if (state.venues.isNotEmpty()) {
            LazyColumn {
                items(state.venues) { venue ->
                    VenueItem(venue = venue)
                }
            }
        } else if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error)
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
        onAction = {}
    )
}