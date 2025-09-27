package com.example.jarassignment.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import com.example.jarassignment.data.model.EducationCard
import com.example.jarassignment.util.CardState


@Composable
fun AnimatedEducationCard(
    card: EducationCard,
    cardState: CardState,
    blockTouches: Boolean,
    index: Int
) {
    val collapsedCardHeight = 64.dp
    val collapsedSpacing = 16.dp
    val expandedTopOffset = 110.dp
    val offscreenOffset = 1000.dp

    val expandedIndexOffset = index * (collapsedCardHeight + collapsedSpacing)
    val collapsedIndexOffset = expandedTopOffset + expandedIndexOffset

    val transition = updateTransition(targetState = cardState, label = "cardTransition")

    val offsetY by transition.animateDp(
        transitionSpec = {
            when (targetState) {
                CardState.Expanding -> tween(500, easing = FastOutSlowInEasing)
                CardState.CollapsingTilted -> tween(500, easing = LinearOutSlowInEasing)
                CardState.Halfscreen -> tween(500, easing = FastOutLinearInEasing)
                CardState.Collapsed -> spring(stiffness = Spring.StiffnessMediumLow)
                else -> tween(600)
            }
        }
    ) { state ->
        when (state) {
            CardState.Offscreen -> offscreenOffset
            CardState.Halfscreen -> 600.dp - index * 20.dp
            CardState.Expanding -> expandedTopOffset + expandedIndexOffset
            CardState.CollapsingTilted -> expandedTopOffset + expandedIndexOffset
            CardState.Collapsed -> collapsedIndexOffset
        }
    }

    val scale by transition.animateFloat(
        transitionSpec = {
            when (targetState) {
                CardState.Collapsed -> spring(stiffness = Spring.StiffnessMediumLow)
                else -> tween(500)
            }
        }
    ) {
        when (it) {
            CardState.Collapsed -> 0.95f
            CardState.Halfscreen -> 0.92f
            else -> 1f
        }
    }

    val rotation by transition.animateFloat(
        transitionSpec = { tween(400) }
    ) {
        when (it) {
            CardState.CollapsingTilted -> -10f
            else -> 0f
        }
    }

    val cardHeight by transition.animateDp(
        transitionSpec = { tween(400) }
    ) {
        when (it) {
            CardState.Expanding, CardState.Halfscreen -> 450.dp
            CardState.CollapsingTilted, CardState.Collapsed -> collapsedCardHeight
            else -> 300.dp
        }
    }

    val cardShape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .offset(y = offsetY)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            }
            .fillMaxWidth()
            .height(cardHeight)
            .clip(cardShape)
            .background(
                color = Color(card.backGroundColor?.toColorInt() ?: 0)
            )
            .border(
                2.dp,
                Brush.verticalGradient(
                    listOf(
                        Color(card.strokeStartColor?.toColorInt() ?: 0),
                        Color(card.strokeEndColor?.toColorInt() ?: 0)
                    )
                ),
                cardShape
            )
            .then(
                if (blockTouches) Modifier.pointerInput(Unit) {}
                else Modifier.clickable { /* TODO expand/collapse */ }
            )
    ) {
        Crossfade(
            targetState = cardState,
            animationSpec = tween(400)
        ) { currentPhase ->
            when (currentPhase) {
                CardState.Expanding, CardState.Halfscreen -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = card.image,
                            contentDescription = card.expandStateText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(cardShape)
                                .padding(16.dp)
                        )
                        Text(
                            text = card.expandStateText ?: "Buy gold anytime, anywhere",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                        )
                    }
                }

                CardState.Collapsed, CardState.CollapsingTilted -> {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = card.image,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = card.collapsedStateText ?: "Buy gold anytime, anywhere",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                else -> Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}