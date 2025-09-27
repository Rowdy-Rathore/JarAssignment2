package com.example.jarassignment.ui

import android.R.attr.translationZ
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jarassignment.data.model.EducationCard
import com.example.jarassignment.data.model.ManualBuyEducationData
import kotlinx.coroutines.delay
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.jarassignment.ui.components.AnimatedEducationCard
import kotlinx.coroutines.delay
import com.example.jarassignment.util.CardState
import kotlinx.coroutines.launch
import kotlin.apply
import androidx.core.graphics.toColorInt


@Composable
fun MainEducationScreen(
    cards: List<EducationCard>,
    apiConfig: ManualBuyEducationData,
    ctaLottie: String,
    onNavigateLanding: () -> Unit
) {
    var allCardsAnimated by remember { mutableStateOf(false) }

    // States for each card
//    var cardStates by remember {
//        mutableStateOf(List(cards.size) { CardState.Offscreen })
//    }

    val cardStatesState: MutableState<List<CardState>> = remember {
        mutableStateOf(List(cards.size) { CardState.Offscreen })
    }

    LaunchedEffect(Unit) {
        for (index in cards.indices) {
            if (index == 0) {
                // First card directly expands
                cardStatesState.updateCardStateAtIndex(index, CardState.Expanding)
                delay(apiConfig.expandCardStayInterval.toLong())

                // Collapse
                cardStatesState.updateCardStateAtIndex(index, CardState.CollapsingTilted)

                // 🚀 Overlap: start next card Halfscreen while current is collapsing
                if (index + 1 < cards.size) {
                    launch {
                        delay(apiConfig.collapseCardTiltInterval.toLong() / 3) // small overlap delay
                        cardStatesState.updateCardStateAtIndex(index+1, CardState.Halfscreen)
                    }
                }

                delay(apiConfig.collapseCardTiltInterval.toLong())
                cardStatesState.updateCardStateAtIndex(index, CardState.Collapsed)
            } else if (index < cards.lastIndex) {
                // Already in Halfscreen (triggered earlier), now expand
                cardStatesState.updateCardStateAtIndex(index, CardState.Expanding)
                delay(apiConfig.expandCardStayInterval.toLong())

                // Collapse
                cardStatesState.updateCardStateAtIndex(index, CardState.CollapsingTilted)

                // 🚀 Overlap trigger for next card
                launch {
                    delay(apiConfig.collapseCardTiltInterval.toLong() / 3)
                    cardStatesState.updateCardStateAtIndex(index + 1, CardState.Halfscreen )
                }

                delay(apiConfig.collapseCardTiltInterval.toLong())
                cardStatesState.updateCardStateAtIndex(index, CardState.Collapsed )
            } else {
                // === Last Card ===
                // Already put in Halfscreen (by previous overlap), now expand and stay
                cardStatesState.updateCardStateAtIndex(index, CardState.Expanding)
                allCardsAnimated = true
            }
        }
    }

    // === UI ===
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        cards.forEachIndexed { index, card ->
            AnimatedEducationCard(
                card = card,
                isCurrent = cardStatesState.value[index] ==  CardState.Expanding || cardStatesState.value[index] is CardState.CollapsingTilted,
                isLast = index == cards.lastIndex,
                cardState = cardStatesState.value[index],
                blockTouches = !allCardsAnimated,
                index = index
            )
        }

        // CTA button after last card
        if (allCardsAnimated) {
            FloatingActionButton(
                onClick = onNavigateLanding,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .border(1.dp, Color(apiConfig.saveButtonCta?.strokeColor?.toColorInt() ?: 0), shape = RoundedCornerShape(50)),
                shape = RoundedCornerShape(50),
                containerColor = Color(apiConfig.saveButtonCta?.backgroundColor?.toColorInt() ?: 0)
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
                        modifier = Modifier.size(32.dp)
                            .graphicsLayer {
                            scaleY = -1f // Flip vertically
                        }
                    )
                }
            }
        }

        // Block gestures until last card animation completes
        if (!allCardsAnimated) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {} // consume all touches
            )
        }
    }
}

// If you specifically want it only for List<CardState>:
fun MutableState<List<CardState>>.updateCardStateAtIndex(index: Int, newState: CardState) {
    if (index >= 0 && index < this.value.size) {
        val newList = this.value.toMutableList()
        newList[index] = newState
        this.value = newList
    }
}

/*@Composable
fun MainEducationScreen(
    cards: List<EducationCard>,
    apiConfig: ManualBuyEducationData,
    actionText: String,
    ctaLottie: String,
    onNavigateLanding: () -> Unit
){
    var currentIndex by remember { mutableIntStateOf(0) }
    var phase by remember { mutableIntStateOf(0) }
    var allCardsAnimated by remember { mutableStateOf(false) }

    // === Disable touches until CTA appears ===
    val blockTouches = !allCardsAnimated

    LaunchedEffect(currentIndex, phase) {
        if (allCardsAnimated) return@LaunchedEffect

        when (phase) {
            0 -> { // Bottom → Center (expand)
                delay(apiConfig.bottomToCenterTranslationInterval.toLong())
                phase = 1
            }
            1 -> { // Stay Expanded
                delay(apiConfig.expandCardStayInterval.toLong())
                phase = 2
            }
            2 -> { // Collapse Tilt
                delay(apiConfig.collapseCardTiltInterval.toLong())

                if (currentIndex < cards.lastIndex) {
                    currentIndex++
                    phase = 0
                } else {
                    // Last card stays expanded
                    allCardsAnimated = true
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()
        .padding(16.dp)) {

        // === Card Stack ===
        cards.forEachIndexed { index, card ->
            val isCurrent = index == currentIndex
            val isLast = index == cards.lastIndex

            AnimatedEducationCard(
                card = card,
                isCurrent = isCurrent,
                isLast = isLast,
                phase = if (isCurrent) phase else -1,
                blockTouches = blockTouches,
                index = index
            )
        }

        // === CTA Button appears after animation ===
        if (allCardsAnimated) {
            FloatingActionButton(
                onClick = onNavigateLanding,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                shape = RoundedCornerShape(50),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // CTA Lottie from API
//                    val composition by rememberLottieComposition(
//                        LottieCompositionSpec.Url(ctaLottie)
//                    )
//                    LottieAnimationView(
//                        composition = composition,
//                        iterations = LottieConstants.IterateForever,
//                        modifier = Modifier.size(32.dp)
//                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = actionText,
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                    )
                }
            }
        }

        // === Block gestures until animation cycle finishes ===
        if (blockTouches) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {} // consume all touches
            )
        }
    }
}*/
