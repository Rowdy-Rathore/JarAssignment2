package com.example.jarassignment.data.repository

import com.example.jarassignment.data.api.EducationApiService
import com.example.jarassignment.data.model.EducationResponse
import javax.inject.Inject
import javax.inject.Singleton

interface EducationRepository {
    suspend fun fetchEducationMetadata(): EducationResponse
}


