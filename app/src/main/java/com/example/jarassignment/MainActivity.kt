package com.example.jarassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.jarassignment.ui.navigation.EducationNavHost
import com.example.jarassignment.ui.theme.JarAssignmentTheme
import com.example.jarassignment.ui.viewmodel.EducationViewModel
import com.example.jarassignment.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: EducationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JarAssignmentTheme {
                val navController = rememberNavController()

                val uiState by viewModel.uiState.collectAsState()

                when (uiState) {
                    is UiState.Idle, UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is UiState.Error -> {
                        val err = uiState as UiState.Error
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = err.message, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.retry() }) {
                                Text("Retry")
                            }
                        }
                    }

                    is UiState.Success -> {
                        val data = (uiState as UiState.Success).data.data.manualBuyEducationData
                        EducationNavHost(
                            navController = navController,
                            educationData = data,
                            onExitFlow = { finish() } // exit or navigate elsewhere
                        )
                    }
                }
            }
        }

        // Trigger API fetch
        viewModel.loadEducationMetadata()
    }
}