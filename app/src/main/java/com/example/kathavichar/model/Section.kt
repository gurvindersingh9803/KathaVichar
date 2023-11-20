package com.example.kathavichar.model

data class Section(
    val data: ArrayList<Artist>,
    val sectionName: String
)
data class Artist(
    val image: String,
    val name: String
)

data class DataModel(
    val items: ArrayList<Section>
)