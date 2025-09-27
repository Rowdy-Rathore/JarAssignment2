package com.example.jarassignment.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.jarassignment.data.model.EducationCard
import com.example.jarassignment.data.model.ManualBuyEducationData
import com.example.jarassignment.ui.components.AnimatedEducationCard
import com.example.jarassignment.util.CardState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainEducationScreen(
    cards: List<EducationCard>,
    apiConfig: ManualBuyEducationData,
    ctaLottie: String,
    onNavigateLanding: () -> Unit
) {
    var allCardsAnimated by remember { mutableStateOf(false) }

    // States for each card
    val cardStatesState: MutableState<List<CardState>> = remember {
        mutableStateOf(List(cards.size) { CardState.Offscreen })
    }

    var bgIndex by remember { mutableIntStateOf(0) }

    //Animate background colors
    val bgStart by animateColorAsState(
        targetValue = if (bgIndex >= 0) {
            Color(cards[bgIndex].startGradient?.toColorInt() ?: 0)
        } else Color.Black,
        animationSpec = tween(300),
        label = "bgStart"
    )
    val bgEnd by animateColorAsState(
        targetValue = if (bgIndex >= 0) {
            Color(cards[bgIndex].endGradient?.toColorInt() ?: 0)
        } else Color.Black,
        animationSpec = tween(300),
        label = "bgEnd"
    )

    LaunchedEffect(Unit) {
        val staggerDelay = 150L // ms per card index
        for (index in cards.indices) {
            if (index == 0) {
                // First card directly expands
                cardStatesState.updateCardStateAtIndex(index, CardState.Expanding)
                bgIndex = index
                delay(apiConfig.expandCardStayInterval.toLong())

                // Collapse tilted
                cardStatesState.updateCardStateAtIndex(index, CardState.CollapsingTilted)

                // Overlap: start next card Halfscreen while current is collapsing
                if (index + 1 < cards.size) {
                    launch {
                        delay(apiConfig.collapseCardTiltInterval.toLong() / 3 + staggerDelay * (index + 1))
                        cardStatesState.updateCardStateAtIndex(index + 1, CardState.Halfscreen)
                    }
                }

                delay(apiConfig.collapseCardTiltInterval.toLong())
                cardStatesState.updateCardStateAtIndex(index, CardState.Collapsed)
            } else if (index < cards.lastIndex) {
                // Halfscreen → Expanding
                cardStatesState.updateCardStateAtIndex(index, CardState.Expanding)
                bgIndex = index
                delay(apiConfig.expandCardStayInterval.toLong() + staggerDelay * index)

                // Collapse tilted
                cardStatesState.updateCardStateAtIndex(index, CardState.CollapsingTilted)

                // Overlap trigger for next card
                if (index + 1 < cards.size) {
                    launch {
                        delay(apiConfig.collapseCardTiltInterval.toLong() / 3 + staggerDelay * (index + 1))
                        cardStatesState.updateCardStateAtIndex(index + 1, CardState.Halfscreen)
                    }
                }

                delay(apiConfig.collapseCardTiltInterval.toLong())
                cardStatesState.updateCardStateAtIndex(index, CardState.Collapsed)
            } else {
                // Last card stays expanded
                cardStatesState.updateCardStateAtIndex(index, CardState.Expanding)
                bgIndex = index
                allCardsAnimated = true
            }
        }
    }

    // === UI ===
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(apiConfig.toolBarText ?: "", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(listOf(bgStart, bgEnd))
                    )
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.background(Brush.verticalGradient(listOf(bgStart, bgEnd)))
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()

                .padding(16.dp)
        ) {
            // --- Cards ---
            cards.forEachIndexed { index, card ->
                AnimatedEducationCard(
                    card = card,
                    cardState = cardStatesState.value[index],
                    blockTouches = !allCardsAnimated,
                    index = index
                )
            }

            // --- CTA Button ---
            if (allCardsAnimated) {
                FloatingActionButton(
                    onClick = onNavigateLanding,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                        .border(
                            1.dp,
                            Color(apiConfig.saveButtonCta?.strokeColor?.toColorInt() ?: 0),
                            shape = RoundedCornerShape(50)
                        ),
                    shape = RoundedCornerShape(50),
                    containerColor = Color(
                        apiConfig.saveButtonCta?.backgroundColor?.toColorInt() ?: 0
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = apiConfig.saveButtonCta?.text ?: "",
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                            color = Color(apiConfig.saveButtonCta?.textColor?.toColorInt() ?: 1),
                        )

                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.Url(ctaLottie)
                        )
                        LottieAnimation(
                            composition = composition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier
                                .size(32.dp)
                                .graphicsLayer {
                                    scaleY = -1f // Flip vertically
                                }
                        )
                    }
                }
            }

            // --- Block gestures until animation completes ---
            if (!allCardsAnimated) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInput(Unit) {} // consume touches
                )
            }
        }
    }
}


// Extension function for List<CardState>:
fun MutableState<List<CardState>>.updateCardStateAtIndex(index: Int, newState: CardState) {
    if (index >= 0 && index < this.value.size) {
        val newList = this.value.toMutableList()
        newList[index] = newState
        this.value = newList
    }
}
