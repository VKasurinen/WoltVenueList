// WoltDto.kt
data class WoltResponse(
    val sections: List<Section>
)

data class Section(
    val items: List<VenueItem>
)

data class VenueItem(
    val venue: Venue?,
    val image: Image?
)

data class Venue(
    val id: String?,
    val name: String?,
    val short_description: String?,
    val address: String?
)

data class Image(
    val url: String?
)