package com.example.jarassignment.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.jarassignment.data.model.EducationCard
import com.example.jarassignment.data.model.ManualBuyEducationData
import com.example.jarassignment.ui.screens.IntroScreen
import com.example.jarassignment.ui.viewmodel.EducationViewModel
import com.example.jarassignment.util.UiState

@Composable
fun EducationScreen(
    viewModel: EducationViewModel = hiltViewModel(),
    onIntroFinished: (ManualBuyEducationData) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Start loading once when composable is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadEducationMetadata()
    }

    when (uiState) {
        is UiState.Idle, UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
            IntroScreen(
                title = data.introTitle ?: "",
                subtitle = data.introSubtitle ?: "",
                onFinished = { onIntroFinished(data) }
            )
            val cardList = data.educationCardList
            EducationCardList(cardList = cardList)
        }
    }
}

@Composable
private fun EducationCardList(cardList: List<EducationCard>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cardList) { card ->
            EducationCardItem(card = card)
        }
    }
}

@Composable
private fun EducationCardItem(card: EducationCard) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // image
            val painter = rememberAsyncImagePainter(model = card.image)
            Image(
                painter = painter,
                contentDescription = card.collapsedStateText,
                modifier = Modifier
                    .size(56.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.collapsedStateText ?: card.expandStateText.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = card.expandStateText ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
