package com.example.exercise.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

/**
 * 图片管理工具类
 * 负责处理动作图片的保存、加载和管理
 */
object ImageUtils {

    private const val EXERCISE_IMAGES_DIR = "exercise_images"
    private const val MAX_IMAGE_SIZE = 800 // 最大图片尺寸
    private const val JPEG_QUALITY = 85 // JPEG压缩质量

    /**
     * 保存图片到应用内部存储（使用更可靠的方式）
     * @param context 上下文
     * @param imageUri 原始图片URI
     * @return 保存后的文件名，如果保存失败返回null
     */
    suspend fun saveExerciseImage(context: Context, imageUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                println("开始保存图片: $imageUri")

                // 使用应用的内部存储目录，这个目录在应用卸载前不会被清理
                val imageDir = File(context.filesDir, EXERCISE_IMAGES_DIR)
                if (!imageDir.exists()) {
                    val created = imageDir.mkdirs()
                    println("创建图片目录: ${imageDir.absolutePath}, 成功: $created")
                }

                // 生成唯一文件名
                val fileName = "exercise_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.jpg"
                val targetFile = File(imageDir, fileName)
                println("目标文件路径: ${targetFile.absolutePath}")

                // 读取原始图片
                val inputStream = context.contentResolver.openInputStream(imageUri)
                if (inputStream == null) {
                    println("无法读取图片流")
                    return@withContext null
                }

                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                if (originalBitmap == null) {
                    println("图片解码失败")
                    return@withContext null
                }

                println("原始图片尺寸: ${originalBitmap.width} x ${originalBitmap.height}")

                // 压缩图片
                val compressedBitmap = compressBitmap(originalBitmap)
                println("压缩后图片尺寸: ${compressedBitmap.width} x ${compressedBitmap.height}")

                // 保存压缩后的图片
                val outputStream = FileOutputStream(targetFile)
                val saved = compressedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
                outputStream.flush()
                outputStream.close()

                println("图片保存结果: $saved")
                println("保存的文件大小: ${targetFile.length()} bytes")

                // 释放内存
                originalBitmap.recycle()
                if (compressedBitmap != originalBitmap) {
                    compressedBitmap.recycle()
                }

                // 验证文件确实被保存
                if (targetFile.exists() && targetFile.length() > 0) {
                    println("图片保存成功: $fileName")
                    fileName
                } else {
                    println("图片保存失败: 文件不存在或为空")
                    null
                }
            } catch (e: Exception) {
                println("保存图片时发生错误: ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * 获取动作图片文件
     * @param context 上下文
     * @param fileName 文件名
     * @return 图片文件，如果不存在返回null
     */
    fun getExerciseImageFile(context: Context, fileName: String): File? {
        if (fileName.isEmpty()) return null

        val imageDir = File(context.filesDir, EXERCISE_IMAGES_DIR)
        val imageFile = File(imageDir, fileName)

        return if (imageFile.exists()) imageFile else null
    }

    /**
     * 获取动作图片URI
     * @param context 上下文
     * @param fileName 文件名
     * @return 图片URI，如果不存在返回null
     */
    fun getExerciseImageUri(context: Context, fileName: String): Uri? {
        val file = getExerciseImageFile(context, fileName)
        return file?.let { Uri.fromFile(it) }
    }

    /**
     * 删除动作图片
     * @param context 上下文
     * @param fileName 文件名
     * @return 是否删除成功
     */
    fun deleteExerciseImage(context: Context, fileName: String): Boolean {
        if (fileName.isEmpty()) return true

        val file = getExerciseImageFile(context, fileName)
        return file?.delete() ?: true
    }

    /**
     * 压缩图片
     * @param original 原始位图
     * @return 压缩后的位图
     */
    private fun compressBitmap(original: Bitmap): Bitmap {
        val width = original.width
        val height = original.height

        // 如果图片已经很小，直接返回
        if (width <= MAX_IMAGE_SIZE && height <= MAX_IMAGE_SIZE) {
            return original
        }

        // 计算缩放比例
        val scale = if (width > height) {
            MAX_IMAGE_SIZE.toFloat() / width
        } else {
            MAX_IMAGE_SIZE.toFloat() / height
        }

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true)
    }

    /**
     * 清理所有未使用的图片
     * @param context 上下文
     * @param usedFileNames 正在使用的文件名列表
     */
    suspend fun cleanupUnusedImages(context: Context, usedFileNames: Set<String>) {
        withContext(Dispatchers.IO) {
            try {
                val imageDir = File(context.filesDir, EXERCISE_IMAGES_DIR)
                if (!imageDir.exists()) return@withContext

                imageDir.listFiles()?.forEach { file ->
                    if (file.isFile && !usedFileNames.contains(file.name)) {
                        file.delete()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 测试图片保存和加载功能
     * @param context 上下文
     * @param imageUri 测试图片URI
     */
    suspend fun testImageSaveAndLoad(context: Context, imageUri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                println("=== 开始测试图片保存 ===")

                // 保存图片
                val savedFileName = saveExerciseImage(context, imageUri)
                println("保存结果: $savedFileName")

                if (savedFileName.isNullOrEmpty()) {
                    println("图片保存失败")
                    return@withContext false
                }

                // 检查文件是否存在
                val imageDir = File(context.filesDir, EXERCISE_IMAGES_DIR)
                val savedFile = File(imageDir, savedFileName)
                println("保存路径: ${savedFile.absolutePath}")
                println("文件是否存在: ${savedFile.exists()}")
                println("文件大小: ${savedFile.length()} bytes")

                // 测试获取文件
                val retrievedFile = getExerciseImageFile(context, savedFileName)
                println("检索到的文件: ${retrievedFile?.absolutePath}")
                println("检索文件是否存在: ${retrievedFile?.exists()}")

                true
            } catch (e: Exception) {
                println("测试失败: ${e.message}")
                e.printStackTrace()
                false
            }
        }
    }
}
