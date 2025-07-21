package com.mobdeve.s18.group9.dinosync.network

import com.mobdeve.s18.group9.dinosync.model.UniversityDto
import retrofit2.http.GET
import retrofit2.http.Query

interface UniversityApi {
    @GET("search")
    suspend fun getUniversities(@Query("country") country: String): List<UniversityDto>
}