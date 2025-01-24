package com.vkasurinen.woltmobile.presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
        state = state
    )
}

//TEE ENSIN NAVIGAATIO
//TESTAA ETTÄ VOIDAAN NAVIGOIDA DETAILLS
//SITTEN TESTAA KUVA JOKA YULEE YLÄKOHTAAN
//TEE MYÖS TRANSPARENT TOPAPPBAR JOSSA ON ONBACK CLICK JA EHKÄ ONFAVORIT
//KATSO WOLT SOVELLUKSESTA INSPIRAATIOTA SEN JÄLKEEN



@Composable
private fun DetailsScreen(
    state: DetailsState,
) {
    state.venue?.let { venue ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = venue.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){

            }


            Text(text = "Description: ${venue.description}")
            Text(text = "Address: ${venue.address}")


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
        isFavorite = false
    )
    val sampleState = DetailsState(venue = sampleVenue)
    DetailsScreen(
        state = sampleState
    )
}
