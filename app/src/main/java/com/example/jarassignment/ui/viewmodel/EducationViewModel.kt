package com.example.jarassignment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jarassignment.data.model.EducationResponse
import com.example.jarassignment.data.repository.EducationRepository
import com.example.jarassignment.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class EducationViewModel @Inject constructor(
    private val repository: EducationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<EducationResponse>>(UiState.Idle)
    val uiState: StateFlow<UiState<EducationResponse>> = _uiState

    fun loadEducationMetadata() {
        // don't block if already loading
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.fetchEducationMetadata()
                _uiState.value = UiState.Success(response)
            } catch (e: IOException) {
                // network / timeout
                _uiState.value = UiState.Error("Network error. Check your connection.", e)
            } catch (e: HttpException) {
                // non-2xx http response
                val code = e.code()
                _uiState.value = UiState.Error("Server error ($code). Please try again.", e)
            } catch (e: Exception) {
                // fallback
                _uiState.value = UiState.Error("Unexpected error occurred.", e)
            }
        }
    }

    fun retry() {
        loadEducationMetadata()
    }
}