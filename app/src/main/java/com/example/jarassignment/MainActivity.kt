package com.example.jarassignment

import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jarassignment.ui.EducationScreen
import com.example.jarassignment.ui.navigation.EducationNavHost
import com.example.jarassignment.ui.screens.IntroScreen
import com.example.jarassignment.ui.theme.JarAssignmentTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.jarassignment.ui.viewmodel.EducationViewModel
import com.example.jarassignment.util.UiState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: EducationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JarAssignmentTheme {
                val navController = rememberNavController()

                // Start loading once when composable is first displayed
//                LaunchedEffect(Unit) {
//                    viewModel.loadEducationMetadata()
//                }

                val uiState by viewModel.uiState.collectAsState()

                when (uiState) {
                    is UiState.Idle, UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
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
//                        IntroScreen(
//                            introTitle = data.introTitle ?: "",
//                            introSubtitle = data.introSubtitle ?: "",
//                            onFinished = { onIntroFinished(data) }
//                        )


/*                        EducationScreen(
                            viewModel = viewModel,
                            onIntroFinished = { data ->
                                navController.navigate("education_screen")
                            }
                        )*/

                        EducationNavHost(
                            navController = navController,
                            educationData = data,
                            onExitFlow = { finish() } // exit or navigate elsewhere
                        )
//            val cardList = data.data.manualBuyEducationData.educationCardList
//            EducationCardList(cardList = cardList)
                    }
                }

                // Observe ViewModel data
//                val educationResponse by viewModel.educationData.observeAsState()

//                educationResponse?.let { response ->
//                    val educationData = response.data.manualBuyEducationData
//
//                    EducationNavHost(
//                        navController = navController,
//                        educationData = educationData,
//                        onExitFlow = { finish() } // exit or navigate elsewhere
//                    )
//                }
            }
        }

        // Trigger API fetch
        viewModel.loadEducationMetadata()
    }
}