package com.example.kathavichar.model
import com.google.gson.annotations.SerializedName

data class Item(var image: String = "", var name: String = "")
data class SectionData(var sectionName: String = "", var data: List<Item> = listOf())
data class Section(
    @SerializedName("sectionName") val sectionName: String,
    @SerializedName("data") val data: ArtistData
)

sealed class ArtistData {
    data class ArtistsMap(
        @SerializedName("map") val artists: Map<String, ArtistSummary>
    ) : ArtistData()

    data class OthersList(
        @SerializedName("list") val others: List<ArtistSummary>
    ) : ArtistData()
}

data class ArtistSummary(
    @SerializedName("image") val image: String,
    @SerializedName("name") val name: String? = null // For maps, `name` may be optional
)



