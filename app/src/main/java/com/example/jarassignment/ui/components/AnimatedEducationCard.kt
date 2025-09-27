package com.example.jarassignment.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import coil.compose.AsyncImage
import com.example.jarassignment.R
import com.example.jarassignment.data.model.EducationCard
import com.example.jarassignment.util.CardState


@Composable
fun AnimatedEducationCard(
    card: EducationCard,
    isCurrent: Boolean,
    isLast: Boolean,
    cardState: CardState,
    blockTouches: Boolean,
    index: Int
) {
    val collapsedCardHeight = 64.dp
    val collapsedSpacing = 16.dp
    val expandedTopOffset = 20.dp
    val offscreenOffset = 1000.dp

    // Final stacked Y offset for collapsed cards
    val expandedIndexOffset = (index * (collapsedCardHeight + collapsedSpacing))
    val collapsedIndexOffset = expandedTopOffset + expandedIndexOffset

    val transition = updateTransition(targetState = cardState, label = "cardTransition")

    val offsetY by transition.animateDp(
        transitionSpec = { tween(500) },
        label = "offsetY"
    ) { state ->
        when (state) {
            CardState.Offscreen -> offscreenOffset
            CardState.Halfscreen -> 600.dp
            CardState.Expanding -> expandedTopOffset + expandedIndexOffset
            CardState.CollapsingTilted -> expandedTopOffset + expandedIndexOffset
            CardState.Collapsed -> collapsedIndexOffset
        }
    }

    val scale by transition.animateFloat(
        transitionSpec = { tween(400) },
        label = "scale"
    ) { state ->
        when (state) {
            CardState.Expanding -> 1f
            CardState.CollapsingTilted -> 1f
            CardState.Collapsed -> 1f
            CardState.Halfscreen -> 1f
            else -> 0.7f
        }
    }

    val rotation by transition.animateFloat(
        transitionSpec = { tween(400) },
        label = "rotation"
    ) { state ->
        when (state) {
            CardState.CollapsingTilted -> -10f
            else -> 0f
        }
    }

    val cardHeight by transition.animateDp(
        transitionSpec = { tween(400) },
        label = "cardHeight"
    ) { state ->
        when (state) {
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
                brush = Brush.verticalGradient(
                    listOf(
                        Color(android.graphics.Color.parseColor(card.startGradient)),
                        Color(android.graphics.Color.parseColor(card.endGradient))
                    )
                )
            )
            .border(
                2.dp,
                Brush.verticalGradient(
                    listOf(
                        Color(android.graphics.Color.parseColor(card.strokeStartColor)),
                        Color(android.graphics.Color.parseColor(card.strokeEndColor))
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
            animationSpec = tween(400),
            label = "contentCrossfade"
        ) { currentPhase ->
            when (currentPhase) {
                CardState.Expanding, CardState.Halfscreen -> {
                    // Expanded State
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
                    // Final stacked collapsed state
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = card.image,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape)
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

                else -> {
                    // Placeholder for other states
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

/*@Composable
fun AnimatedEducationCard(
    card: EducationCard,
    isCurrent: Boolean,
    isLast: Boolean,
    phase: Int,
    blockTouches: Boolean,
    index: Int
) {
    val transition = updateTransition(targetState = phase, label = "cardTransition")

    val collapsedCardHeight = 64.dp
    val collapsedSpacing = 16.dp
    val expandedTopOffset = 100.dp
    val offscreenOffset = 700.dp

    // Where collapsed cards should stack
    val collapsedIndexOffset = expandedTopOffset + (index * (collapsedCardHeight + collapsedSpacing))


    val offsetY by transition.animateDp(
        transitionSpec = { tween(500) },
        label = "offsetY"
    ) { state ->
        when (state) {
            0 -> offscreenOffset   // offscreen bottom
            1 -> expandedTopOffset      // expanded center
            2 -> collapsedIndexOffset     // collapsed center
            else -> if (!isCurrent) collapsedIndexOffset else offscreenOffset
        }
    }

    val scale by transition.animateFloat(
        transitionSpec = { tween(400) },
        label = "scale"
    ) { state ->
        when (state) {
            1 -> 1f
            2 -> 1f
            else -> 0.7f
        }
    }

    val rotation by transition.animateFloat(
        transitionSpec = { tween(400) },
        label = "rotation"
    ) { state ->
        if ( state != 2) 0f else -10f
    }

    val cardHeight by transition.animateDp(
        transitionSpec = { tween(400) },
        label = "cardHeight"
    ) { state ->
        when (state) {
            1 -> 450.dp // Adjusted height for phase 1 to accommodate image and text
            2 -> 64.dp
            else -> 300.dp
        }
    }

    val cardPaddingHorizontal by transition.animateDp(
        transitionSpec = { tween(400) },
        label = "cardPaddingHorizontal"
    ) { state ->
        when (state) {
            1 -> 0.dp // No horizontal padding for phase 1 image to fill
            2 -> 8.dp
            else -> 0.dp
        }
    }

    val cardPaddingVertical by transition.animateDp(
        transitionSpec = { tween(400) },
        label = "cardPaddingVertical"
    ) { state ->
        when (state) {
            1 -> 0.dp // No vertical padding for phase 1 image to fill
            2 -> 8.dp
            else -> 0.dp
        }
    }

    val cardShape = RoundedCornerShape(24.dp) // Define shape for reuse

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
//            .animateContentSize(animationSpec = tween(400, easing = LinearEasing))
            .clip(cardShape) // Clip outer box
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(android.graphics.Color.parseColor(card.startGradient)),
                        Color(android.graphics.Color.parseColor(card.endGradient))
                    )
                )
            )
            .border(
                2.dp,
                Brush.verticalGradient(
                    listOf(
                        Color(android.graphics.Color.parseColor(card.strokeStartColor)),
                        Color(android.graphics.Color.parseColor(card.strokeEndColor))
                    )
                ),
                cardShape
            )
            // Apply padding to the Box that contains Crossfade if needed for phase 2
            // For phase 1, we want the image to fill, so padding is handled internally
            .then(
                if (blockTouches) Modifier.pointerInput(Unit) {}
                else Modifier.clickable { *//* expand/collapse later *//* }
            )
    ) {
        Crossfade(
            targetState = phase,
            animationSpec = tween(400),
            label = "contentCrossfade"
        ) { currentPhase ->
            when (currentPhase) {
                1 -> { // Expanded State (like your new image)
                    Column(modifier = Modifier) { // Box to allow overlay
                        // Image takes the whole card space, clipped to shape
                            Column(modifier = Modifier.clip(cardShape)) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row {  AsyncImage(
                                        model = card.image,
                                        contentDescription = card.expandStateText ?: "Education image",
                                        modifier = Modifier
                                            .fillMaxWidth()
//                                            .fillMaxWidth() // Image takes the whole card space
                                            .clip(cardShape) // Crop to fill bounds
                                        .padding(16.dp), // Clip image to the card shape if background doesn't show through
                                    ) }

                                // Text overlaid at the bottom center
                                Text(
                                    text = card.expandStateText ?: "Buy gold anytime, anywhere",
                                    color = Color.White,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontSize = 20.sp, // Adjust font size as needed
                                        fontWeight = FontWeight.Bold
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
//                                        .align(Alignment.BottomCenter)
                                        .padding(
                                            bottom = 16.dp,
                                            start = 16.dp,
                                            end = 16.dp
//                                        16.dp
                                        ) // Adjust padding

//                                    .padding(
//                                        vertical = 4.dp,
//                                        horizontal = 8.dp
//                                    ) // Padding for text background
                                )
                                }
                            }

                    }


                    //------
                        *//*Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.fillMaxWidth()
                                .border(width = 1.dp, color = Color(color = 0x33FFFFFFF), shape = RoundedCornerShape(28.dp))
                                .height(444.dp)
                                .background(color = Color(color = 0x4D28085C), shape = RoundedCornerShape(28.dp))
                                .padding(16.dp)
                        ) {
                            AsyncImage(
                                model = card.image,
                                contentDescription = card.expandStateText ?: "Education image",
                                modifier = Modifier
                                    .width(296.dp)
                                    .height(340.dp)// Image takes the whole card space
                                    .clip(RoundedCornerShape(16.dp)*//**//*cardShape*//**//*), // Clip image to the card shape if background doesn't show through
                                contentScale = ContentScale.Crop // Crop to fill bounds
                            )

                            Text(
                                text =  card.expandStateText ?: "Buy gold anytime, anywhere",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = 20.sp, // Adjust font size as needed
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(
                                        16.dp,
                                        bottom = 24.dp,
                                        end = 16.dp
                                    ) // Adjust padding
                                    .background(
                                        Color.Black.copy(alpha = 0.0f) // Optional: slight scrim if text is hard to read
                                    )
                                    .fillMaxWidth()
//                .padding(
//                    vertical = 4.dp,
//                    horizontal = 8.dp
//                ) // Padding for text background
                            )

                        }*//*
                }

                2 -> { // Collapsed State (like the image)
                    Row(
                        modifier = Modifier
//                            .fillMaxSize() // Fill the animated height/width of the parent Box
                            .padding(
                                horizontal = cardPaddingHorizontal,
                                vertical = cardPaddingVertical
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
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
                                text = card.collapsedStateText
                                    ?: "Buy gold anytime, anywhere",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                else -> { // Default content for other phases
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Placeholder
                    }
                }
            }
        }
    }
}*/

