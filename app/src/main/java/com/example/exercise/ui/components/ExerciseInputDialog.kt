package com.example.exercise.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.exercise.data.model.Exercise
import com.example.exercise.data.model.ExerciseType
import com.example.exercise.data.model.LastExerciseRecord

/**
 * 训练数据输入对话框
 * 点击动作时弹出，用于输入重量和次数
 */
@Composable
fun ExerciseInputDialog(
    exercise: Exercise,
    lastRecord: LastExerciseRecord?,
    onDismiss: () -> Unit,
    onConfirm: (weight: Float, reps: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentWeight by remember(exercise) {
        mutableFloatStateOf(exercise.defaultWeight)
    }

    var currentReps by remember(exercise) {
        mutableIntStateOf(exercise.defaultReps)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = exercise.muscleGroup,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 显示上次记录
                lastRecord?.let { record ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "上次记录",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (exercise.exerciseType != ExerciseType.BODYWEIGHT) {
                                    Text(
                                        text = "${record.lastWeight}kg",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = "${record.lastReps}次",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 重量和次数选择器 - 水平排列
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (exercise.exerciseType != ExerciseType.BODYWEIGHT) {
                        WeightSelector(
                            exerciseType = exercise.exerciseType,
                            currentWeight = currentWeight,
                            onWeightChange = { currentWeight = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    RepsSelector(
                        currentReps = currentReps,
                        onRepsChange = { currentReps = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 确认和取消按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("取消")
                    }

                    Button(
                        onClick = {
                            onConfirm(currentWeight, currentReps)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("确认")
                    }
                }
            }
        }
    }
}
