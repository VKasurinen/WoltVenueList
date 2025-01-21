package com.vkasurinen.woltmobile.data.remote

data class WoltResponse(
    val sections: List<Section>
)

data class Section(
    val items: List<Venue>
)

data class Venue(
    val content_id: String?,
    val title: String?,
    val image: Image?,
    val description: String?
)

data class Image(
    val url: String?
)