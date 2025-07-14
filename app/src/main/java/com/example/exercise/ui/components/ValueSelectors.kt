package com.example.exercise.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exercise.data.model.ExerciseType
import kotlin.math.abs
import kotlin.math.min

/**
 * 自定义滚轮选择器
 */
@Composable
fun CustomWheelPicker(
    items: List<String>,
    selectedIndex: Int,
    onSelectionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    visibleItemsCount: Int = 5
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)

    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val firstVisibleOffset = listState.firstVisibleItemScrollOffset
            val itemHeight = 48 // 估算的项目高度

            val centerIndex = if (firstVisibleOffset > itemHeight / 2) {
                firstVisibleIndex + 1
            } else {
                firstVisibleIndex
            }

            val clampedIndex = centerIndex.coerceIn(0, items.size - 1)
            if (clampedIndex != selectedIndex) {
                onSelectionChanged(clampedIndex)
            }
        }
    }

    Box(
        modifier = modifier.height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(items) { index, item ->
                val offset = (listState.firstVisibleItemIndex - index).toFloat() +
                        listState.firstVisibleItemScrollOffset / 48f

                val alpha = 1f - min(1f, abs(offset) * 0.3f)
                val scale = 1f - min(0.2f, abs(offset) * 0.1f)

                Text(
                    text = item,
                    fontSize = 18.sp,
                    fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (index == selectedIndex) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .graphicsLayer {
                            this.alpha = alpha
                            scaleX = scale
                            scaleY = scale
                        }
                )
            }
        }

        // 选择指示器
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
    }
}

/**
 * 重量选择器组件
 */
@Composable
fun WeightSelector(
    exerciseType: ExerciseType,
    currentWeight: Float,
    onWeightChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // 根据动作类型生成重量选项
    val weightOptions = remember(exerciseType) {
        when (exerciseType) {
            ExerciseType.MACHINE -> {
                // 器械型：15-100kg，每次增加1kg
                (15..100).map { it.toFloat() }
            }
            ExerciseType.FREE_WEIGHT -> {
                // 自由重量：2.5kg起，每次增加2.5kg
                generateSequence(2.5f) { it + 2.5f }
                    .takeWhile { it <= 100f }
                    .toList()
            }
            ExerciseType.BODYWEIGHT -> {
                // 自重训练：不显示重量选择器
                emptyList()
            }
        }
    }

    if (exerciseType == ExerciseType.BODYWEIGHT) {
        // 自重训练不显示重量选择器
        return
    }

    // 查找当前重量在选项中的索引
    val currentIndex = remember(currentWeight, weightOptions) {
        weightOptions.indexOfFirst { it == currentWeight }.takeIf { it >= 0 } ?: 0
    }

    val weightTexts = remember(weightOptions) {
        weightOptions.map { weight ->
            if (weight == weight.toInt().toFloat()) {
                "${weight.toInt()}kg"
            } else {
                "${weight}kg"
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "重量",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomWheelPicker(
            items = weightTexts,
            selectedIndex = currentIndex,
            onSelectionChanged = { index ->
                if (index < weightOptions.size) {
                    onWeightChange(weightOptions[index])
                }
            }
        )
    }
}

/**
 * 次数选择器组件
 */
@Composable
fun RepsSelector(
    currentReps: Int,
    onRepsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // 次数选项：1-50次
    val repsOptions = remember { (1..50).toList() }

    // 查找当前次数在选项中的索引
    val currentIndex = remember(currentReps, repsOptions) {
        repsOptions.indexOfFirst { it == currentReps }.takeIf { it >= 0 } ?: 0
    }

    val repsTexts = remember(repsOptions) {
        repsOptions.map { "$it 次" }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "次数",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomWheelPicker(
            items = repsTexts,
            selectedIndex = currentIndex,
            onSelectionChanged = { index ->
                if (index < repsOptions.size) {
                    onRepsChange(repsOptions[index])
                }
            }
        )
    }
}
