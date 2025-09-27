package com.example.jarassignment.data.api

import com.example.jarassignment.data.model.EducationResponse
import retrofit2.http.GET

interface EducationApiService {
    @GET("_assets/shared/education-metadata.json")
    suspend fun fetchEducationMetadata(): EducationResponse
}
