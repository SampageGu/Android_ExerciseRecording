package com.example.exercise.ui.screens.exercises

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.exercise.R
import com.example.exercise.data.model.Exercise
import com.example.exercise.data.model.ExerciseSet
import com.example.exercise.data.model.ExerciseType
import com.example.exercise.data.model.LastExerciseRecord
import com.example.exercise.ui.components.ExerciseInputDialog
import com.example.exercise.ui.components.ExerciseImage
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    navController: NavController,
    viewModel: ExercisesViewModel = viewModel(factory = ExercisesViewModel.Factory)
) {
    val context = LocalContext.current
    val muscleGroups = listOf(
        "全部",
        stringResource(R.string.chest),
        stringResource(R.string.back),
        stringResource(R.string.legs),
        stringResource(R.string.shoulders),
        stringResource(R.string.arms),
        stringResource(R.string.core)
    )

    var selectedTabIndex by remember { mutableStateOf(0) }
    val selectedMuscleGroup = muscleGroups[selectedTabIndex]

    val exercises by if (selectedMuscleGroup == "全部") {
        viewModel.getAllExercises().collectAsState(initial = emptyList())
    } else {
        viewModel.getExercisesByMuscleGroup(selectedMuscleGroup).collectAsState(initial = emptyList())
    }

    // 添加动作弹窗状态
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    // 编辑动作弹窗状态
    var showEditExerciseDialog by remember { mutableStateOf(false) }
    var editingExercise by remember { mutableStateOf<Exercise?>(null) }
    // 长按动作选择对话框状态
    var showActionSelectionDialog by remember { mutableStateOf(false) }
    var longPressedExercise by remember { mutableStateOf<Exercise?>(null) }
    // 删除确认对话框状态
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    // 添加记录组弹窗状态
    var showAddSetDialog by remember { mutableStateOf(false) }
    // 当前���中的动作（用于添加记录组）
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.exercises)) },
                actions = {
                    // 添加动作按钮
                    IconButton(onClick = { showAddExerciseDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加新动作"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp
            ) {
                muscleGroups.forEachIndexed { index, muscleGroup ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(muscleGroup) }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(exercises) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onClick = {
                            // 点击动作时显示添加组记录弹窗
                            selectedExercise = exercise
                            showAddSetDialog = true
                        },
                        onLongClick = {
                            // 长按动作时显示选择对话框
                            longPressedExercise = exercise
                            showActionSelectionDialog = true
                        }
                    )
                }
            }
        }

        // 添加动作弹窗
        if (showAddExerciseDialog) {
            AddExerciseDialog(
                muscleGroups = muscleGroups.filter { it != "全部" }, // 排除"全部"选项
                onDismiss = { showAddExerciseDialog = false },
                onAddExercise = { exercise ->
                    viewModel.addExercise(exercise, context)
                    showAddExerciseDialog = false
                }
            )
        }

        // 编辑动作弹窗
        if (showEditExerciseDialog && editingExercise != null) {
            AddExerciseDialog(
                muscleGroups = muscleGroups.filter { it != "全部" }, // 排除"全部"选项
                onDismiss = { showEditExerciseDialog = false },
                onAddExercise = { exercise ->
                    viewModel.updateExercise(exercise, context)
                    showEditExerciseDialog = false
                },
                initialExercise = editingExercise
            )
        }

        // 添加组记录弹窗
        if (showAddSetDialog && selectedExercise != null) {
            // 使用改进的���练数据输入对话框，支持滚轮选择器
            var lastRecord by remember { mutableStateOf<LastExerciseRecord?>(null) }

            LaunchedEffect(selectedExercise!!.id) {
                // 获取该动作的��次训练记录作为默认值
                lastRecord = viewModel.getLastExerciseRecord(selectedExercise!!.id)
            }

            ExerciseInputDialog(
                exercise = selectedExercise!!,
                lastRecord = lastRecord,
                onDismiss = { showAddSetDialog = false },
                onConfirm = { weight, reps ->
                    // 添加训���记��
                    viewModel.recordExerciseSet(selectedExercise!!.id, weight, reps)
                    showAddSetDialog = false
                }
            )
        }

        // 删除确认对话框
        if (showDeleteConfirmDialog && longPressedExercise != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("确认删除动作") },
                text = { Text("您确定要删除 ${longPressedExercise!!.name} 吗？") },
                confirmButton = {
                    Button(
                        onClick = {
                            // 执行删除操作
                            viewModel.deleteExercise(longPressedExercise!!.id)
                            showDeleteConfirmDialog = false
                        }
                    ) {
                        Text("删除")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteConfirmDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }

        // 动作选择对话框（编辑/删除）- 修复取消按钮位置和名称
        if (showActionSelectionDialog && longPressedExercise != null) {
            Dialog(onDismissRequest = { showActionSelectionDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        // 标题栏带返回按钮
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "选择操作",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            // 返回按钮移到右上角
                            TextButton(onClick = { showActionSelectionDialog = false }) {
                                Text("返回")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "请选择您要对 ${longPressedExercise!!.name} 执行的操作：",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 操作按钮
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    // 编辑动作
                                    editingExercise = longPressedExercise
                                    showEditExerciseDialog = true
                                    showActionSelectionDialog = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("编辑")
                            }

                            Button(
                                onClick = {
                                    // 删除动作
                                    showDeleteConfirmDialog = true
                                    showActionSelectionDialog = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("删除")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseDialog(
    muscleGroups: List<String>,
    onDismiss: () -> Unit,
    onAddExercise: (Exercise) -> Unit,
    initialExercise: Exercise? = null // 新增初始动作参数，用于编辑动作
) {
    var name by remember { mutableStateOf(initialExercise?.name ?: "") }
    var selectedMuscleGroup by remember { mutableStateOf(initialExercise?.muscleGroup ?: muscleGroups[0]) }
    var description by remember { mutableStateOf(initialExercise?.description ?: "") }
    var selectedExerciseType by remember { mutableStateOf(initialExercise?.exerciseType ?: ExerciseType.FREE_WEIGHT) }
    var isCompound by remember { mutableStateOf(initialExercise?.isCompound ?: false) }
    var muscleGroupExpanded by remember { mutableStateOf(false) }
    var exerciseTypeExpanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf(initialExercise?.imageUrl ?: "") }

    // 添加默认重量和次数的状态
    var defaultWeight by remember { mutableStateOf("") }
    var defaultReps by remember { mutableStateOf("10") }

    // 图片选择器
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it.toString()
        }
    }

    // 初始化默认值 - 修复新动作时默认次数不显示的问题
    LaunchedEffect(initialExercise, selectedExerciseType) {
        if (initialExercise != null) {
            // 编辑模式：使用现有动作的值
            defaultWeight = if (initialExercise.exerciseType == ExerciseType.BODYWEIGHT) "0" else initialExercise.defaultWeight.toString()
            defaultReps = initialExercise.defaultReps.toString()
        } else {
            // 新增模式：根据动作类型设置默认值
            defaultWeight = when (selectedExerciseType) {
                ExerciseType.BODYWEIGHT -> "0"
                ExerciseType.MACHINE -> "20"
                ExerciseType.FREE_WEIGHT -> "15"
            }
            defaultReps = when (selectedExerciseType) {
                ExerciseType.BODYWEIGHT -> if (selectedMuscleGroup == "核心") "30" else "12"
                else -> "10"
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // 标题栏 - 修复取消按钮位置和名称
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (initialExercise == null) "添加新动作" else "编辑动作",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        // 返回按钮移到右上角
                        TextButton(onClick = onDismiss) {
                            Text("返回")
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // 动作名称
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("动作名称") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // 目标肌群选择
                item {
                    ExposedDropdownMenuBox(
                        expanded = muscleGroupExpanded,
                        onExpandedChange = { muscleGroupExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedMuscleGroup,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("目标肌群") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = muscleGroupExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = muscleGroupExpanded,
                            onDismissRequest = { muscleGroupExpanded = false }
                        ) {
                            muscleGroups.forEach { muscleGroup ->
                                DropdownMenuItem(
                                    text = { Text(muscleGroup) },
                                    onClick = {
                                        selectedMuscleGroup = muscleGroup
                                        muscleGroupExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // 动作类型选择
                item {
                    ExposedDropdownMenuBox(
                        expanded = exerciseTypeExpanded,
                        onExpandedChange = { exerciseTypeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedExerciseType.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("动作类型") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = exerciseTypeExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = exerciseTypeExpanded,
                            onDismissRequest = { exerciseTypeExpanded = false }
                        ) {
                            ExerciseType.values().forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.displayName) },
                                    onClick = {
                                        selectedExerciseType = type
                                        exerciseTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // 默认重量输入（自重训练不显示）
                if (selectedExerciseType != ExerciseType.BODYWEIGHT) {
                    item {
                        OutlinedTextField(
                            value = defaultWeight,
                            onValueChange = { value ->
                                // 只允许输入数字和小数点
                                if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    defaultWeight = value
                                }
                            },
                            label = { Text("默认重量 (kg)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            supportingText = {
                                Text("${selectedExerciseType.weightRange.first}kg - ${selectedExerciseType.weightRange.second}kg")
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // 默认次数输入
                item {
                    OutlinedTextField(
                        value = defaultReps,
                        onValueChange = { value ->
                            // 只允许输入数字
                            if (value.isEmpty() || value.matches(Regex("^\\d+$"))) {
                                defaultReps = value
                            }
                        },
                        label = {
                            Text(if (selectedExerciseType == ExerciseType.BODYWEIGHT && selectedMuscleGroup == "核心") "默认时长 (秒)" else "默认次数")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = {
                            Text(if (selectedExerciseType == ExerciseType.BODYWEIGHT && selectedMuscleGroup == "核心") "建议30-60秒" else "建议8-15次")
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // 动作描述
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("描述（可选）") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // 图片选择区域
                item {
                    Column {
                        Text(
                            text = "动作封面图片（可选）",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable {
                                    imagePickerLauncher.launch("image/*")
                                },
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        ) {
                            if (selectedImageUri.isNotEmpty()) {
                                // 显示选择的图片
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(selectedImageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "动作封面图片",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // 显示占位符
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "点击选择图片",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        if (selectedImageUri.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { selectedImageUri = "" },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("移除图片")
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // 按钮区域
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onDismiss
                        ) {
                            Text("取消")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val weightValue = if (selectedExerciseType == ExerciseType.BODYWEIGHT) 0f else (defaultWeight.toFloatOrNull() ?: 0f)
                                    val repsValue = defaultReps.toIntOrNull() ?: 10
                                    val exercise = if (initialExercise != null) {
                                        // 编辑模式：保留原有的ID
                                        initialExercise.copy(
                                            name = name.trim(),
                                            muscleGroup = selectedMuscleGroup,
                                            exerciseType = selectedExerciseType,
                                            description = description.trim(),
                                            imageUrl = selectedImageUri,
                                            isCompound = isCompound,
                                            defaultWeight = weightValue,
                                            defaultReps = repsValue
                                        )
                                    } else {
                                        // 添加模式：创建新动作
                                        Exercise(
                                            name = name.trim(),
                                            muscleGroup = selectedMuscleGroup,
                                            exerciseType = selectedExerciseType,
                                            description = description.trim(),
                                            imageUrl = selectedImageUri,
                                            isCompound = isCompound,
                                            defaultWeight = weightValue,
                                            defaultReps = repsValue
                                        )
                                    }
                                    onAddExercise(exercise)
                                }
                            },
                            enabled = name.isNotBlank() && selectedMuscleGroup.isNotBlank() &&
                                     defaultReps.isNotBlank() &&
                                     (selectedExerciseType == ExerciseType.BODYWEIGHT || defaultWeight.isNotBlank())
                        ) {
                            Text(if (initialExercise == null) "添加" else "保存")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddSetDialog(
    exercise: Exercise,
    onDismiss: () -> Unit,
    onAddSet: (weight: Float, reps: Int) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加 ${exercise.name} 的训练记录") },
        text = {
            Column {
                Text("请输入此次���练的重量和次数")

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("重量 (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("次数") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val weightFloat = weight.toFloatOrNull() ?: 0f
                    val repsInt = reps.toIntOrNull() ?: 0

                    if (weightFloat > 0 && repsInt > 0) {
                        onAddSet(weightFloat, repsInt)
                    }
                },
                enabled = weight.isNotEmpty() && reps.isNotEmpty()
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}  // 添加长按回调
) {
    val context = LocalContext.current

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 动作图片或占位符
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (exercise.imageUrl.isNotEmpty()) {
                    // 使用专门的图片组件来正确加载本地图片
                    ExerciseImage(
                        imageFileName = exercise.imageUrl,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = "${exercise.name}封面图片"
                    )
                } else {
                    // 显示文字占位符
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = exercise.name.first().toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = exercise.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = exercise.muscleGroup,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}
