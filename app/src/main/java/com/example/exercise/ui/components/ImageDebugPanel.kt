package com.example.exercise.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.exercise.data.database.ExerciseDatabase
import com.example.exercise.utils.ImageUtils
import kotlinx.coroutines.launch

/**
 * 调试组件 - 用于测试图片保存和加载功能
 */
@Composable
fun ImageDebugPanel() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = ExerciseDatabase.getInstance(context)

    var debugInfo by remember { mutableStateOf("点击按钮开始调试...") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "图片调试面板",
            style = MaterialTheme.typography.headlineSmall
        )

        Button(
            onClick = {
                scope.launch {
                    try {
                        debugInfo = "开始检查图片存储状态...\n"

                        // 检查图片目录
                        val imageDir = context.filesDir.resolve("exercise_images")
                        debugInfo += "图片目录路径: ${imageDir.absolutePath}\n"
                        debugInfo += "目录是否存在: ${imageDir.exists()}\n"

                        if (imageDir.exists()) {
                            val files = imageDir.listFiles()
                            debugInfo += "目录中的文件数量: ${files?.size ?: 0}\n"
                            files?.forEach { file ->
                                debugInfo += "- ${file.name} (${file.length()} bytes)\n"
                            }
                        } else {
                            debugInfo += "图片目录不存在！\n"
                        }

                        // 检查数据库中的动作
                        database.exerciseDao().getAllExercises().collect { exercises ->
                            debugInfo += "\n=== 数据库中的动作 ===\n"
                            debugInfo += "动作总数: ${exercises.size}\n"

                            exercises.forEach { exercise ->
                                debugInfo += "\n动作: ${exercise.name}\n"
                                debugInfo += "图片文件名: '${exercise.imageUrl}'\n"

                                if (exercise.imageUrl.isNotEmpty()) {
                                    val imageFile = ImageUtils.getExerciseImageFile(context, exercise.imageUrl)
                                    debugInfo += "图片文件路径: ${imageFile?.absolutePath ?: "null"}\n"
                                    debugInfo += "文件是否存在: ${imageFile?.exists() ?: false}\n"
                                    debugInfo += "文件大小: ${imageFile?.length() ?: 0} bytes\n"
                                } else {
                                    debugInfo += "没有关联图片\n"
                                }
                            }
                        }

                    } catch (e: Exception) {
                        debugInfo += "检查过程中发生错误: ${e.message}\n"
                        e.printStackTrace()
                    }
                }
            }
        ) {
            Text("检查图片状态")
        }

        Button(
            onClick = {
                scope.launch {
                    try {
                        debugInfo = "开始测试图片保存功能...\n"

                        // 创建一个测试图片（使用应用图标作为测试）
                        val testImageUri = android.net.Uri.parse("android.resource://${context.packageName}/${android.R.drawable.ic_menu_gallery}")
                        debugInfo += "测试图片URI: $testImageUri\n"

                        val savedFileName = ImageUtils.saveExerciseImage(context, testImageUri)
                        debugInfo += "保存结果: $savedFileName\n"

                        if (savedFileName != null) {
                            val imageFile = ImageUtils.getExerciseImageFile(context, savedFileName)
                            debugInfo += "保存的文件路径: ${imageFile?.absolutePath}\n"
                            debugInfo += "文件是否存在: ${imageFile?.exists()}\n"
                            debugInfo += "文件大小: ${imageFile?.length()} bytes\n"
                        } else {
                            debugInfo += "图片保存失败！\n"
                        }

                    } catch (e: Exception) {
                        debugInfo += "测试过程中发生错误: ${e.message}\n"
                        e.printStackTrace()
                    }
                }
            }
        ) {
            Text("测试图片保存")
        }

        Button(
            onClick = {
                scope.launch {
                    try {
                        debugInfo = "开始修复图片关联...\n"

                        // 获取所有图片文件
                        val imageDir = context.filesDir.resolve("exercise_images")
                        val imageFiles = imageDir.listFiles()?.toList() ?: emptyList()
                        debugInfo += "发现 ${imageFiles.size} 个图片文件\n"

                        // 获取所有动作
                        database.exerciseDao().getAllExercises().collect { exercises ->
                            debugInfo += "数据库中有 ${exercises.size} 个动作\n"

                            exercises.forEach { exercise ->
                                if (exercise.imageUrl.startsWith("content://")) {
                                    debugInfo += "\n修复动作: ${exercise.name}\n"
                                    debugInfo += "当前错误URI: ${exercise.imageUrl}\n"

                                    // 找到一个可用的图片文件
                                    val availableFile = imageFiles.firstOrNull()
                                    if (availableFile != null) {
                                        // 更新动作记录
                                        val updatedExercise = exercise.copy(imageUrl = availableFile.name)
                                        database.exerciseDao().updateExercise(updatedExercise)
                                        debugInfo += "已关联图片文件: ${availableFile.name}\n"
                                    } else {
                                        debugInfo += "没有可用的图片文件进行关联\n"
                                    }
                                }
                            }

                            debugInfo += "\n修复完成！\n"
                        }

                    } catch (e: Exception) {
                        debugInfo += "修复过程中发生错误: ${e.message}\n"
                        e.printStackTrace()
                    }
                }
            }
        ) {
            Text("修复图片关联")
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 500.dp)
        ) {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.padding(12.dp)
            ) {
                item {
                    Text(
                        text = debugInfo,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
