package com.example.exercise.ui.screens.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.exercise.R
import com.example.exercise.data.model.ExerciseSet
import com.example.exercise.data.model.ExerciseSetWithDetails
import com.example.exercise.data.model.TrainingSession
import com.example.exercise.data.model.TrainingSessionWithSets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)
) {
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val sessionsThisMonth by viewModel.sessionsThisMonth.collectAsStateWithLifecycle()
    val selectedSession by viewModel.selectedSession.collectAsStateWithLifecycle()

    val currentDate = remember { Calendar.getInstance() }
    var currentYear by rememberSaveable { mutableIntStateOf(currentDate.get(Calendar.YEAR)) }
    var currentMonth by rememberSaveable { mutableIntStateOf(currentDate.get(Calendar.MONTH)) }

    val monthFormat = SimpleDateFormat("yyyy年MM月", Locale.getDefault())
    val calendarForTitle = Calendar.getInstance()
    calendarForTitle.set(Calendar.YEAR, currentYear)
    calendarForTitle.set(Calendar.MONTH, currentMonth)
    val monthTitle = monthFormat.format(calendarForTitle.time)

    // 详情对话框
    selectedSession?.let { session ->
        TrainingDetailDialog(
            sessionWithSets = session,
            onDismiss = { viewModel.clearSelectedSession() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 月份选择器
            MonthSelector(
                currentYear = currentYear,
                currentMonth = currentMonth,
                monthTitle = monthTitle,
                onMonthChange = { year, month ->
                    currentYear = year
                    currentMonth = month
                    val newCalendar = Calendar.getInstance()
                    newCalendar.set(Calendar.YEAR, year)
                    newCalendar.set(Calendar.MONTH, month)
                    viewModel.selectMonth(newCalendar)
                }
            )

            // 日历视图
            TrainingCalendar(
                year = currentYear,
                month = currentMonth,
                trainingSessions = sessionsThisMonth,
                onDateSelected = { sessionId ->
                    viewModel.selectSession(sessionId)
                }
            )
        }
    }
}

@Composable
fun MonthSelector(
    currentYear: Int,
    currentMonth: Int,
    monthTitle: String,
    onMonthChange: (Int, Int) -> Unit
) {
    // 上下文和回调省略，保持不变
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (currentMonth == 0) {
                    onMonthChange(currentYear - 1, 11)
                } else {
                    onMonthChange(currentYear, currentMonth - 1)
                }
            }
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "上个月")
        }

        Text(
            text = monthTitle,
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(
            onClick = {
                if (currentMonth == 11) {
                    onMonthChange(currentYear + 1, 0)
                } else {
                    onMonthChange(currentYear, currentMonth + 1)
                }
            }
        ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "下个月")
        }
    }
}

@Composable
fun TrainingCalendar(
    year: Int,
    month: Int,
    trainingSessions: List<TrainingSession>, // 修正类型
    onDateSelected: (Long) -> Unit // 修正参数类型
) {
    // 日历网格的实现保持不变
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)

    // 计算这个月的第一天是星期几
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

    // 计算这个月有多少天
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // 创建日期列表，包含空白和实际日期
    val dates = List(firstDayOfWeek) { null } + List(daysInMonth) { index ->
        val cal = Calendar.getInstance()
        cal.set(year, month, index + 1)
        cal.time
    }

    // 转换训练日期为简单格式字符串，用于比较
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val trainingDatesMap = trainingSessions.associateBy { dateFormat.format(it.date) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(dates) { date ->
            if (date == null) {
                // 空白格子
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                )
            } else {
                // 日期格子
                calendar.time = date
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val dateStr = dateFormat.format(date)

                // 检查是否为训练日
                val trainingSession = trainingDatesMap[dateStr]
                val isTrainingDay = trainingSession != null

                CalendarDay(
                    day = dayOfMonth,
                    isTrainingDay = isTrainingDay,
                    onClick = {
                        trainingSession?.let { session ->
                            onDateSelected(session.id) // 传递session ID
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CalendarDay(
    day: Int,
    isTrainingDay: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                if (isTrainingDay) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent
            )
            .clickable(enabled = isTrainingDay, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = if (isTrainingDay) MaterialTheme.colorScheme.onPrimaryContainer
                   else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun TrainingDetailDialog(
    sessionWithSets: TrainingSessionWithSets,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f) // 限制对话框高度
        ) {
            Column {
                // 标题栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "训练详情",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }

                HorizontalDivider()

                // 内容区域
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 训练名称
                    if (sessionWithSets.session.name.isNotEmpty()) {
                        item {
                            Text(
                                text = sessionWithSets.session.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 训练笔记
                    if (sessionWithSets.session.notes.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "训练笔记",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = sessionWithSets.session.notes,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    // 训练动作分组
                    val groupedSets = sessionWithSets.sets.groupBy { it.exercise.id }

                    groupedSets.forEach { (_, sets) ->
                        val exercise = sets.first().exercise

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // 动作名称
                                    Text(
                                        text = exercise.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // 动作分组信息
                                    Text(
                                        text = exercise.muscleGroup,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // 组数据表头
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "组数",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = stringResource(R.string.weight),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1.5f)
                                        )
                                        Text(
                                            text = stringResource(R.string.reps),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1.5f)
                                        )
                                    }

                                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                                    // 组数据
                                    sets.sortedBy { it.set.setNumber }.forEach { setWithDetails ->
                                        ExerciseSetItem(
                                            exerciseSet = setWithDetails.set,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseSetItem(
    exerciseSet: ExerciseSet,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${exerciseSet.setNumber}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1.5f)
        ) {
            Text(
                text = "${exerciseSet.weight} kg",
                style = MaterialTheme.typography.bodyLarge
            )

            if (exerciseSet.isPersonalRecord) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                )
            }
        }
        Text(
            text = "${exerciseSet.reps}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1.5f)
        )
    }
}
