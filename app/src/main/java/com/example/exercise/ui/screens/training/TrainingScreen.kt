package com.example.exercise.ui.screens.training

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.exercise.data.database.ExerciseDatabase
import com.example.exercise.data.model.*
import com.example.exercise.ui.components.*
import com.example.exercise.ui.viewmodel.TrainingViewModel
import com.example.exercise.ui.viewmodel.TrainingViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * 训练执行主界面
 * 直接显示今天的训练，无需手动开始
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen() {
    val context = LocalContext.current
    val database = ExerciseDatabase.getInstance(context)

    val viewModel: TrainingViewModel = viewModel(
        factory = TrainingViewModelFactory(
            exerciseDao = database.exerciseDao(),
            trainingSessionDao = database.trainingSessionDao(),
            exerciseSetDao = database.exerciseSetDao(),
            personalRecordDao = database.personalRecordDao()
        )
    )

    val currentSession by viewModel.currentSession.collectAsStateWithLifecycle()
    val currentExerciseSets by viewModel.currentExerciseSets.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle(initialValue = emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val showPersonalRecordDialog by viewModel.showPersonalRecordDialog.collectAsStateWithLifecycle()

    // 自动创建或加载今天的训练会话
    LaunchedEffect(Unit) {
        viewModel.createTodaySessionIfNeeded()
    }

    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showExerciseInputDialog by remember { mutableStateOf<Exercise?>(null) }
    var sessionNameEditMode by remember { mutableStateOf(false) }
    var sessionNameText by remember { mutableStateOf("") }

    // 个人记录祝贺对话框
    showPersonalRecordDialog?.let { record ->
        PersonalRecordDialog(
            personalRecord = record,
            onDismiss = { viewModel.dismissPersonalRecordDialog() }
        )
    }

    // 动作输入对话框
    showExerciseInputDialog?.let { exercise ->
        var lastRecord by remember { mutableStateOf<LastExerciseRecord?>(null) }

        LaunchedEffect(exercise.id) {
            lastRecord = viewModel.getLastExerciseRecord(exercise.id)
        }

        ExerciseInputDialog(
            exercise = exercise,
            lastRecord = lastRecord,
            onDismiss = { showExerciseInputDialog = null },
            onConfirm = { weight, reps ->
                viewModel.addExerciseSet(exercise.id, weight, reps)
                showExerciseInputDialog = null
            }
        )
    }

    // 添加动作对话框
    if (showAddExerciseDialog) {
        AddExerciseDialog(
            exercises = allExercises,
            onDismiss = { showAddExerciseDialog = false },
            onExerciseSelected = { exercise ->
                showAddExerciseDialog = false
                showExerciseInputDialog = exercise
            },
            onCreateNewExercise = { name, muscleGroup, exerciseType, defaultWeight, defaultReps, imageUri ->
                viewModel.createNewExercise(context, name, muscleGroup, exerciseType, defaultWeight, defaultReps, imageUri)
                showAddExerciseDialog = false
            }
        )
    }

    // 临时添加调试面板 - 用于诊断图片问题
    var showDebugPanel by remember { mutableStateOf(false) }
    if (showDebugPanel) {
        Dialog(onDismissRequest = { showDebugPanel = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.9f),
                shape = MaterialTheme.shapes.medium
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "调试面板",
                            style = MaterialTheme.typography.titleLarge
                        )
                        IconButton(onClick = { showDebugPanel = false }) {
                            Icon(Icons.Default.Close, contentDescription = "关闭")
                        }
                    }
                    ImageDebugPanel()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (sessionNameEditMode && currentSession != null) {
                        OutlinedTextField(
                            value = sessionNameText,
                            onValueChange = { sessionNameText = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                    } else {
                        Text(
                            text = currentSession?.name ?: "今日训练",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    if (currentSession != null) {
                        if (sessionNameEditMode) {
                            IconButton(
                                onClick = {
                                    viewModel.updateSessionName(sessionNameText)
                                    sessionNameEditMode = false
                                }
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "保存")
                            }
                            IconButton(
                                onClick = { sessionNameEditMode = false }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "取消")
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    sessionNameText = currentSession?.name ?: ""
                                    sessionNameEditMode = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "编辑名称")
                            }
                        }
                    }

                    // 临时调试按钮
                    IconButton(
                        onClick = { showDebugPanel = true }
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "调试")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddExerciseDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加动作")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // 直接显示训练界面���不需要"开始训练"步骤
                currentSession?.let { session ->
                    ActiveTrainingContent(
                        session = session,
                        exerciseSets = currentExerciseSets,
                        onDeleteSet = { exerciseSet ->
                            viewModel.deleteExerciseSet(exerciseSet)
                        },
                        onFinishTraining = {
                            viewModel.finishSession()
                        }
                    )
                }
            }
        }
    }
}

/**
 * 活跃训练会话的内容界面
 */
@Composable
private fun ActiveTrainingContent(
    session: TrainingSession,
    exerciseSets: List<ExerciseSetWithDetails>,
    onDeleteSet: (ExerciseSet) -> Unit,
    onFinishTraining: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 训练会话信息卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = dateFormat.format(session.date),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "已完成 ${exerciseSets.size} 组",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    TextButton(
                        onClick = onFinishTraining
                    ) {
                        Text("结束训练")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (exerciseSets.isEmpty()) {
            // 空状态
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "点击右下角 + 号添加���一个动作",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // 按动作分组显示训练组列表 - 优化版本
            val groupedSets = exerciseSets.groupBy { it.exercise.id }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(groupedSets.entries.toList()) { (exerciseId, sets) ->
                    GroupedExerciseSetItem(
                        exerciseName = sets.first().exercise.name,
                        exerciseImageUri = null, // 后续可以添加图片支持
                        sets = sets,
                        onDeleteSet = { setWithDetails ->
                            onDeleteSet(setWithDetails.set)
                        }
                    )
                }
            }
        }
    }
}
