package com.example.exercise.ui.screens.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.exercise.R
import com.example.exercise.data.model.Exercise
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab

// 从AnalysisViewModel中导入这些类型
import com.example.exercise.ui.screens.analysis.TimeRange
import com.example.exercise.ui.screens.analysis.TimeUnit
import com.example.exercise.ui.screens.analysis.ChartPoint
import com.example.exercise.ui.screens.analysis.AnalysisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    navController: NavController,
    viewModel: AnalysisViewModel = viewModel(factory = AnalysisViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.analysis)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 动作选择下拉框
            ExerciseSelector(
                exercises = uiState.allExercises,
                selectedExercise = uiState.selectedExercise,
                onExerciseSelected = { viewModel.selectExercise(it) }
            )

            // 进步曲线图
            ProgressChart(
                title = stringResource(R.string.progress_chart),
                chartData = uiState.progressChartData,
                timeRange = uiState.selectedTimeRange,
                onTimeRangeChanged = { viewModel.setTimeRange(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelector(
    exercises: List<Exercise>,
    selectedExercise: Exercise?,
    onExerciseSelected: (Exercise) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.select_exercise),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedExercise?.name ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                exercises.forEach { exercise ->
                    DropdownMenuItem(
                        text = { Text(exercise.name) },
                        onClick = {
                            onExerciseSelected(exercise)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressChart(
    title: String,
    chartData: List<ChartPoint>,
    timeRange: TimeRange,
    onTimeRangeChanged: (TimeRange) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 时间范围选择器
            TimeRangeSelector(
                selectedTimeRange = timeRange,
                onTimeRangeSelected = onTimeRangeChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 简化版图表
            if (chartData.isNotEmpty()) {
                SimpleLineChart(
                    data = chartData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    lineColor = MaterialTheme.colorScheme.primary
                )

                // 添加简单的图例
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${selectedExerciseName(chartData)} 重量",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "暂无数据")
                }
            }
        }
    }
}

/**
 * 简化版折线图实现，不依赖外部库
 */
@Composable
fun SimpleLineChart(
    data: List<ChartPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) return

    val maxValue = remember(data) { data.maxByOrNull { it.value }?.value ?: 0f }
    val minValue = 0f // 设置Y轴起始点为0
    val dataRange = remember(maxValue, minValue) { max(maxValue - minValue, 1f) }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            val leftPadding = 80f    // 增加左边距，为Y轴标签留更多空间
            val bottomPadding = 40f  // 增加底边距，为X轴标签留空间
            val topPadding = 20f     // 顶部边距
            val rightPadding = 20f   // 右边距

            // 计算图表绘制区域
            val chartWidth = width - leftPadding - rightPadding
            val chartHeight = height - topPadding - bottomPadding

            // 绘制Y轴 - 从顶部边距开始，到底部边距结束
            drawLine(
                color = Color.Gray,
                start = Offset(leftPadding, topPadding),
                end = Offset(leftPadding, height - bottomPadding),
                strokeWidth = 2f
            )

            // 绘制X轴 - 从左边距开始，到右边距结束
            drawLine(
                color = Color.Gray,
                start = Offset(leftPadding, height - bottomPadding),
                end = Offset(width - rightPadding, height - bottomPadding),
                strokeWidth = 2f
            )

            // 绘制水平辅助线
            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            for (i in 1..4) {
                val y = topPadding + (i * chartHeight / 5)
                drawLine(
                    color = Color.LightGray,
                    start = Offset(leftPadding, y),
                    end = Offset(width - rightPadding, y),
                    strokeWidth = 1f,
                    pathEffect = dashPathEffect
                )
            }

            // 绘制数据点和线
            if (data.size > 1) {
                val xStep = chartWidth / (data.size - 1)

                for (i in 0 until data.size - 1) {
                    val x1 = leftPadding + i * xStep
                    val y1 = height - bottomPadding - ((data[i].value - minValue) / dataRange) * chartHeight

                    val x2 = leftPadding + (i + 1) * xStep
                    val y2 = height - bottomPadding - ((data[i + 1].value - minValue) / dataRange) * chartHeight

                    // 绘制线段
                    drawLine(
                        color = lineColor,
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = 3f
                    )

                    // 绘制数据点
                    if (i == 0) {
                        drawCircle(
                            color = lineColor,
                            radius = 5f,
                            center = Offset(x1, y1)
                        )
                    }
                    drawCircle(
                        color = lineColor,
                        radius = 5f,
                        center = Offset(x2, y2)
                    )
                }
            } else if (data.size == 1) {
                // 只有一个数据点的情况
                val x = leftPadding + chartWidth / 2
                val y = height - bottomPadding - ((data[0].value - minValue) / dataRange) * chartHeight

                drawCircle(
                    color = lineColor,
                    radius = 5f,
                    center = Offset(x, y)
                )
            }

            // 绘制垂直辅助线（可选）
            if (data.size > 1) {
                val xStep = chartWidth / (data.size - 1)
                for (i in 0 until data.size) {
                    val x = leftPadding + i * xStep
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        start = Offset(x, topPadding),
                        end = Offset(x, height - bottomPadding),
                        strokeWidth = 1f,
                        pathEffect = dashPathEffect
                    )
                }
            }
        }

        // 绘制值标签 - 调整位置，左移避免遮挡Y轴
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(end = 16.dp), // 增加右边距，避免遮挡Y轴
            horizontalAlignment = Alignment.End
        ) {
            val step = dataRange / 5
            for (i in 5 downTo 1) {
                Text(
                    text = String.format("%.1f", minValue + i * step),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            Text(
                text = String.format("%.1f", minValue), // 显示0作为Y轴起始点
                style = MaterialTheme.typography.bodySmall
            )
        }

        // 绘制日期标签
        if (data.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val format = SimpleDateFormat("MM/dd", Locale.getDefault())
                val labelCount = minOf(data.size, 5)
                val labelInterval = data.size / labelCount

                for (i in 0 until labelCount) {
                    val index = i * labelInterval
                    if (index < data.size) {
                        Text(
                            text = format.format(data[index].date),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(40.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRangeSelector(
    selectedTimeRange: TimeRange,
    onTimeRangeSelected: (TimeRange) -> Unit
) {
    val options = listOf(
        TimeRange.THREE_DAYS to "3天",
        TimeRange.ONE_WEEK to "1周",
        TimeRange.ONE_MONTH to "1月",
        TimeRange.THREE_MONTHS to "3月",
        TimeRange.SIX_MONTHS to "6月",
        TimeRange.ONE_YEAR to "1年"
    )

    // 使用可滚动的标签行来适应更多选项
    ScrollableTabRow(
        selectedTabIndex = options.indexOfFirst { it.first == selectedTimeRange },
        modifier = Modifier.fillMaxWidth(),
        edgePadding = 0.dp
    ) {
        options.forEach { (range, label) ->
            Tab(
                selected = selectedTimeRange == range,
                onClick = { onTimeRangeSelected(range) },
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeUnitSelector(
    selectedTimeUnit: TimeUnit,
    onTimeUnitSelected: (TimeUnit) -> Unit
) {
    val options = listOf(
        TimeUnit.WEEKLY to stringResource(R.string.weekly),
        TimeUnit.MONTHLY to stringResource(R.string.monthly)
    )

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (unit, label) ->
            SegmentedButton(
                selected = selectedTimeUnit == unit,
                onClick = { onTimeUnitSelected(unit) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = label)
            }
        }
    }
}

// 工具函数

/**
 * 根据图表数据获取选中的动作名称
 */
private fun selectedExerciseName(chartData: List<ChartPoint>): String {
    return if (chartData.isNotEmpty()) {
        "动作"  // 在实际应用中，可以从ViewModel中获取
    } else {
        ""
    }
}

/**
 * 格式化日期（用于图表轴标签）
 */
private fun formatDate(date: Date): String {
    val format = SimpleDateFormat("MM/dd", Locale.getDefault())
    return format.format(date)
}
