package com.example.exercise.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.exercise.utils.ImageUtils
import java.io.File

/**
 * 动作图片显示组件
 * 支持显示本地保存的动作图片，如果没有图片则显示默认图标
 */
@Composable
fun ExerciseImage(
    imageFileName: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val context = LocalContext.current
    val imageFile = remember(imageFileName) {
        if (imageFileName.isNotEmpty()) {
            ImageUtils.getExerciseImageFile(context, imageFileName)
        } else null
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (imageFile?.exists() == true) {
            // 显示保存的图片 - 使用URI方式加载更可靠
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(android.net.Uri.fromFile(imageFile)) // 使用URI加载
                    .crossfade(true)
                    .placeholder(android.R.drawable.ic_menu_gallery) // 加载占位符
                    .error(android.R.drawable.ic_menu_report_image) // 错误占位符
                    .build(),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onError = { error ->
                    // 调试信息
                    println("Failed to load image: ${imageFile.absolutePath}, error: $error")
                },
                onSuccess = {
                    // 成功加载的调试信息
                    println("Successfully loaded image: ${imageFile.absolutePath}")
                }
            )
        } else {
            // 显示默认图标
            Icon(
                imageVector = Icons.Outlined.FitnessCenter,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * 小尺寸的动作图片组件，用于列表显示
 */
@Composable
fun ExerciseImageSmall(
    imageFileName: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    ExerciseImage(
        imageFileName = imageFileName,
        modifier = modifier.size(48.dp),
        contentDescription = contentDescription
    )
}

/**
 * 中等尺寸的动作图片组件，用于卡片显示
 */
@Composable
fun ExerciseImageMedium(
    imageFileName: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    ExerciseImage(
        imageFileName = imageFileName,
        modifier = modifier.size(80.dp),
        contentDescription = contentDescription
    )
}

/**
 * 大尺寸的动作图片组件，用于详情显示
 */
@Composable
fun ExerciseImageLarge(
    imageFileName: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    ExerciseImage(
        imageFileName = imageFileName,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentDescription = contentDescription
    )
}
