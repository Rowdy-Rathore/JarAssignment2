package com.example.jarassignment.data.repository

import com.example.jarassignment.data.model.EducationResponse
import com.example.jarassignment.data.api.EducationApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EducationRepositoryImpl @Inject constructor(
    private val api: EducationApiService
) : EducationRepository {
    override suspend fun fetchEducationMetadata(): EducationResponse {
        return api.fetchEducationMetadata()
    }
}
