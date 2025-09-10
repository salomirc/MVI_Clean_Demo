package com.example.mvi_clean_demo.common.ui_components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * A shimmer-style placeholder composable for loading states.
 *
 * - Scales shimmer width to match actual layout size
 * - Hidden from accessibility services (decorative only)
 * - Uses a single transition if shared via `shimmerProgress`
 */
@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 8,
    shimmerProgress: Float? = null // optional external progress to avoid redundant transitions
) {
    val baseColor: Color = MaterialTheme.colorScheme.surfaceVariant
    val highlight: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.08f)

//    val isInPreview = LocalInspectionMode.current
    val isInPreview = false

    // Use shared progress if provided, otherwise create a local one
    val localProgress by if (shimmerProgress == null && !isInPreview) {
        shimmerPlaceholderAnimation()
    } else remember { mutableFloatStateOf(0f) }
    val progress = shimmerProgress ?: localProgress

    var size by remember { mutableStateOf(IntSize.Zero) }

    val brush = if (isInPreview) {
        Brush.horizontalGradient(listOf(baseColor, baseColor, baseColor))
    } else {
        Brush.horizontalGradient(
            colors = listOf(baseColor, highlight, baseColor),
            startX = progress * size.width,
            endX = (progress + 1f) * size.width
        )
    }

    Box(
        modifier = modifier
            .semantics { invisibleToUser() } // accessibility: hide
            .onGloballyPositioned { size = it.size }
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(brush)
    )
}

@Composable
fun PlaceholderCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // thumbnail
        ShimmerPlaceholder(
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            cornerRadius = 12
        )
        Spacer(Modifier.height(12.dp))
        // title
        ShimmerPlaceholder(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(0.7f)
        )
        Spacer(Modifier.height(8.dp))
        // subtitle
        ShimmerPlaceholder(modifier = Modifier
            .height(14.dp)
            .fillMaxWidth(0.45f)
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerPlaceholder(
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth(0.45f)
            )
            ShimmerPlaceholder(
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth(0.25f)
            )
        }
    }
}

@Composable
fun shimmerPlaceholderAnimation(): State<Float> {
    val transition = rememberInfiniteTransition(label = "sharedShimmer")
    val progress: State<Float> = transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sharedShimmerAnim"
    )
    return progress
}

@Composable
fun PlaceholderList(count: Int = 5) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(count) {
            PlaceholderCard()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPlaceholders() {
    MaterialTheme {
        Surface {
            PlaceholderList(3)
        }
    }
}
