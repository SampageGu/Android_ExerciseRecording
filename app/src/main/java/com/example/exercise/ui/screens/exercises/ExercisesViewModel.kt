package com.example.exercise.ui.screens.exercises

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.exercise.ExerciseApplication
import com.example.exercise.data.model.Exercise
import com.example.exercise.data.model.ExerciseSet
import com.example.exercise.data.model.LastExerciseRecord
import com.example.exercise.data.model.TrainingSession
import com.example.exercise.data.repository.ExerciseRepository
import com.example.exercise.utils.ImageUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class ExercisesViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {

    /**
     * 获取指定肌群的所有训练动作
     */
    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<Exercise>> {
        return repository.getAllExercises().map { exercises ->
            exercises.filter { exercise ->
                exercise.muscleGroup.equals(muscleGroup, ignoreCase = true)
            }
        }
    }

    /**
     * 获取所有训练动作
     */
    fun getAllExercises(): Flow<List<Exercise>> {
        return repository.getAllExercises()
    }

    /**
     * 添加新的训练动作（支持图片保存）
     */
    fun addExercise(exercise: Exercise, context: Context? = null) {
        viewModelScope.launch {
            val finalExercise = if (context != null && exercise.imageUrl.isNotEmpty() && exercise.imageUrl.startsWith("content://")) {
                // 如果是content URI，需要保存图片到本地
                val imageUri = Uri.parse(exercise.imageUrl)
                val savedFileName = ImageUtils.saveExerciseImage(context, imageUri)
                if (savedFileName != null) {
                    exercise.copy(imageUrl = savedFileName)
                } else {
                    // 保存失败，清空图片URL
                    exercise.copy(imageUrl = "")
                }
            } else {
                exercise
            }
            repository.insertExercise(finalExercise)
        }
    }

    /**
     * 记录训练组
     */
    fun recordExerciseSet(exerciseId: Long, weight: Float, reps: Int) {
        viewModelScope.launch {
            val sessionId = getTodaySession()
            val existingSets = repository.getSetsBySessionAndExercise(sessionId, exerciseId)
            val nextSetNumber = existingSets.size + 1

            val exerciseSet = ExerciseSet(
                sessionId = sessionId,
                exerciseId = exerciseId,
                setNumber = nextSetNumber,
                weight = weight,
                reps = reps,
                isPersonalRecord = false,
                timestamp = Date()
            )

            // 检查是否为个人记录
            val isPersonalRecord = repository.isPersonalRecord(exerciseId, reps, weight)

            val finalSet = if (isPersonalRecord) {
                exerciseSet.copy(isPersonalRecord = true)
            } else {
                exerciseSet
            }

            repository.insertExerciseSet(finalSet)
        }
    }

    /**
     * 获取或创建今日训练会话
     */
    private suspend fun getTodaySession(): Long {
        // 获取今天的开始时间（00:00:00）
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.time

        // 获取今天的结束时间（23:59:59）
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val todayEnd = calendar.time

        // 查找今天范围内的训练会话
        val todaySessions = repository.getSessionsBetweenDates(todayStart, todayEnd)

        return if (todaySessions.isNotEmpty()) {
            // 如果今天已有会话，使用第一个
            todaySessions.first().id
        } else {
            // 创建新的训练会话，使用今天的开始时间
            val newSession = TrainingSession(
                date = todayStart,
                name = "今日训练",
                notes = ""
            )
            repository.insertSession(newSession)
        }
    }

    /**
     * 获取动作的上次训练记录
     */
    suspend fun getLastExerciseRecord(exerciseId: Long): LastExerciseRecord? {
        return try {
            repository.getLastExerciseRecord(exerciseId)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 更新训练动作（支持图片保存）
     */
    fun updateExercise(exercise: Exercise, context: Context? = null) {
        viewModelScope.launch {
            val finalExercise = if (context != null && exercise.imageUrl.isNotEmpty() && exercise.imageUrl.startsWith("content://")) {
                // 如果是新选择的content URI，需要保存图片到本地
                val imageUri = Uri.parse(exercise.imageUrl)
                val savedFileName = ImageUtils.saveExerciseImage(context, imageUri)
                if (savedFileName != null) {
                    exercise.copy(imageUrl = savedFileName)
                } else {
                    // 保存失败，保持原有图片
                    val originalExercise = repository.getExerciseById(exercise.id)
                    exercise.copy(imageUrl = originalExercise.imageUrl)
                }
            } else {
                exercise
            }
            repository.updateExercise(finalExercise)
        }
    }

    /**
     * 删除训练动作
     */
    fun deleteExercise(exerciseId: Long) {
        viewModelScope.launch {
            repository.deleteExerciseById(exerciseId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return ExercisesViewModel(
                    (application as ExerciseApplication).repository
                ) as T
            }
        }
    }
}
