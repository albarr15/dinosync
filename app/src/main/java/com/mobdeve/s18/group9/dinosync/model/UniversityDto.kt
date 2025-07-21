package com.mobdeve.s18.group9.dinosync.model
import com.google.gson.annotations.SerializedName

data class UniversityDto(
    val name: String,
    val country: String,
    val domain: String,
    @SerializedName("web_page") val webPage: String
)
