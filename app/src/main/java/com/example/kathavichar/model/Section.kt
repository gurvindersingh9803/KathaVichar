package com.example.kathavichar.model

data class Item(var image: String = "", var name: String = "")
data class SectionData(var sectionName: String = "", var data: List<Item> = listOf())
