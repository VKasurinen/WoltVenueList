package com.vkasurinen.woltmobile.presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.vkasurinen.woltmobile.domain.model.WoltModel
import com.vkasurinen.woltmobile.presentation.details.DetailsViewModel
import com.vkasurinen.woltmobile.presentation.details.DetailsState
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.vkasurinen.woltmobile.domain.model.VenuePreviewItemModel
import com.vkasurinen.woltmobile.presentation.details.components.DetailsTopBar
import com.vkasurinen.woltmobile.presentation.details.components.FoodCard
import com.vkasurinen.woltmobile.presentation.venueList.VenueState

@Composable
fun DetailsScreenRoot(
    navController: NavController,
    venueId: String,
    viewModel: DetailsViewModel = koinViewModel(),
) {
    LaunchedEffect(venueId) {
        viewModel.loadVenue(venueId)
    }
    val state = viewModel.state.collectAsState().value
    DetailsScreen(
        state = state,
        onShareClick = { /* Handle share click */ },
        onToggleFavorite = { viewModel.toggleFavorite(venueId) },
        onBackClick = { navController.popBackStack() }
    )
}

//TEE ENSIN NAVIGAATIO
//TESTAA ETTÄ VOIDAAN NAVIGOIDA DETAILLS
//SITTEN TESTAA KUVA JOKA YULEE YLÄKOHTAAN
//TEE MYÖS TRANSPARENT TOPAPPBAR JOSSA ON ONBACK CLICK JA EHKÄ ONFAVORIT

@Composable
private fun DetailsScreen(
    state: VenueState,
    onShareClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit
) {
    state.venue?.let { venue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val painter = rememberAsyncImagePainter(
                model = venue.imageUrl
            )
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(ArchShape())
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 300.dp), // Add padding to avoid overlap
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = venue.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 25.sp,
                    color = MaterialTheme.colorScheme.primary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Divider(
                    modifier = Modifier
                        .padding(horizontal = 50.dp, vertical = 5.dp)
                        .height(1.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = venue.description,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = venue.rating.toString(),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(4.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Min. order 10,00€",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(4.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = venue.address,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(venue.venuePreviewItems ?: emptyList()) { item ->
                        FoodCard(item = item)
                    }
                }
            }

            DetailsTopBar(
                isFavorite = venue.isFavorite,
                onShareClick = onShareClick,
                onToggleFavorite = onToggleFavorite,
                onBackClick = onBackClick,
                modifier = Modifier.align(Alignment.TopCenter) // Align TopAppBar to the top
            )
        }
    } ?: run {
        Text(text = "No venue details available")
    }
}



@Stable
fun ArchShape(): Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f) // Top-left corner
            lineTo(0f, size.height - 50f) // Bottom-left corner before the arch
            quadraticBezierTo(
                size.width / 2, size.height - 100f, // Control point for the arch
                size.width, size.height - 50f // Bottom-right corner of the arch
            )
            lineTo(size.width, 0f) // Top-right corner
            close()
        }
        return Outline.Generic(path)
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun DetailsScreenPreview() {
    val sampleVenue = WoltModel(
        id = "1",
        name = "Sample Venue",
        imageUrl = "https://example.com/image.jpg",
        description = "This is a sample venue description.",
        address = "123 Sample Street",
        isFavorite = false,
        rating = 2.0,
        shortDescription = "Yes",
        venuePreviewItems = listOf(
            VenuePreviewItemModel(
                id = "1",
                name = "Sample Item",
                price = 100,
                imageUrl = "https://example.com/item.jpg",
                currency = "USD"
            )
        )
    )
    val sampleState = VenueState(venue = sampleVenue)
    DetailsScreen(
        state = sampleState,
        onToggleFavorite = {},
        onBackClick = {},
        onShareClick = {}
    )
}
