package com.example.exercise.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exercise.data.dao.ExerciseDao
import com.example.exercise.data.dao.ExerciseSetDao
import com.example.exercise.data.dao.PersonalRecordDao
import com.example.exercise.data.dao.TrainingSessionDao
import com.example.exercise.data.model.*
import com.example.exercise.utils.ImageUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * 训练记录ViewModel
 * 管理当前训练会话的状态和操作
 */
class TrainingViewModel(
    private val exerciseDao: ExerciseDao,
    private val trainingSessionDao: TrainingSessionDao,
    private val exerciseSetDao: ExerciseSetDao,
    private val personalRecordDao: PersonalRecordDao
) : ViewModel() {

    private val _currentSession = MutableStateFlow<TrainingSession?>(null)
    val currentSession: StateFlow<TrainingSession?> = _currentSession.asStateFlow()

    private val _currentExerciseSets = MutableStateFlow<List<ExerciseSetWithDetails>>(emptyList())
    val currentExerciseSets: StateFlow<List<ExerciseSetWithDetails>> = _currentExerciseSets.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showPersonalRecordDialog = MutableStateFlow<PersonalRecord?>(null)
    val showPersonalRecordDialog: StateFlow<PersonalRecord?> = _showPersonalRecordDialog.asStateFlow()

    // 获取所有动作
    val allExercises = exerciseDao.getAllExercises()

    /**
     * 创建新的训练会话
     */
    fun createNewSession(name: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val session = TrainingSession(
                    date = Date(),
                    name = name.ifEmpty { "训练 ${Date().let { "${it.month + 1}/${it.date}" }}" }
                )
                val sessionId = trainingSessionDao.insertSession(session)
                _currentSession.value = session.copy(id = sessionId)
                _currentExerciseSets.value = emptyList()
            } catch (e: Exception) {
                // 处理错误
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 加载指定日期的训练会话
     */
    fun loadSessionByDate(date: Date) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val session = trainingSessionDao.getSessionByDate(date)
                _currentSession.value = session
                session?.let { loadSessionSets(it.id) }
            } catch (e: Exception) {
                // 处理错误
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 获取动作的上次训练记录
     */
    suspend fun getLastExerciseRecord(exerciseId: Long): LastExerciseRecord? {
        return try {
            exerciseSetDao.getLastExerciseRecord(exerciseId)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 添加训练组
     */
    fun addExerciseSet(exerciseId: Long, weight: Float, reps: Int) {
        val session = _currentSession.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 获取当前动作已有的组数
                val existingSets = exerciseSetDao.getSetsBySessionAndExerciseSync(session.id, exerciseId)
                val setNumber = existingSets.size + 1

                // 检查是否为个人记录
                val isPersonalRecord = personalRecordDao.isPersonalRecord(exerciseId, reps, weight)

                // 创建新的训练组
                val exerciseSet = ExerciseSet(
                    sessionId = session.id,
                    exerciseId = exerciseId,
                    setNumber = setNumber,
                    weight = weight,
                    reps = reps,
                    isPersonalRecord = isPersonalRecord,
                    timestamp = Date()
                )

                val setId = exerciseSetDao.insertSet(exerciseSet)

                // 如���是个人记录，更新个人记录表
                if (isPersonalRecord) {
                    val personalRecord = PersonalRecord(
                        exerciseId = exerciseId,
                        reps = reps,
                        weight = weight,
                        date = Date(),
                        setId = setId
                    )
                    personalRecordDao.insertRecord(personalRecord)
                    _showPersonalRecordDialog.value = personalRecord
                }

                // 刷新当前训练组列表
                loadSessionSets(session.id)

            } catch (e: Exception) {
                // 处理错误
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 创建新动作 - 支持图片保存（修复版本）
     */
    fun createNewExercise(
        context: Context,
        name: String,
        muscleGroup: String,
        exerciseType: ExerciseType,
        defaultWeight: Float,
        defaultReps: Int,
        imageUri: Uri? = null
    ) {
        viewModelScope.launch {
            try {
                println("=== 开始创建新动作 ===")
                println("动作名称: $name")
                println("图片URI: $imageUri")

                // 先保存图片，等待完成后再创建动作记录
                val savedImageFileName = if (imageUri != null) {
                    println("开始保存图片...")
                    try {
                        val fileName = ImageUtils.saveExerciseImage(context, imageUri)
                        println("图片保存结果: $fileName")

                        // 验证图片确实被保存
                        if (fileName != null) {
                            val imageFile = ImageUtils.getExerciseImageFile(context, fileName)
                            println("���证图片文件: ${imageFile?.absolutePath}, 存在: ${imageFile?.exists()}")
                            if (imageFile?.exists() == true) {
                                fileName
                            } else {
                                println("图片保存验证失败")
                                ""
                            }
                        } else {
                            println("图片保存返回null")
                            ""
                        }
                    } catch (e: Exception) {
                        println("图片保存异常: ${e.message}")
                        e.printStackTrace()
                        ""
                    }
                } else {
                    println("没有选择图片")
                    ""
                }

                println("最终图片文件名: '$savedImageFileName'")

                // 创建动作记录，确保imageUrl字段保存的是文件名而不是URI
                val exercise = Exercise(
                    name = name,
                    muscleGroup = muscleGroup,
                    exerciseType = exerciseType,
                    defaultWeight = defaultWeight,
                    defaultReps = defaultReps,
                    imageUrl = savedImageFileName // 这里保存文件名，不是URI
                )

                val exerciseId = exerciseDao.insertExercise(exercise)
                println("动作已保存到数据库，ID: $exerciseId")
                println("保存的图片文件名: '$savedImageFileName'")

                // 最终验证
                if (savedImageFileName.isNotEmpty()) {
                    val finalCheck = ImageUtils.getExerciseImageFile(context, savedImageFileName)
                    println("最终验证 - 文件路径: ${finalCheck?.absolutePath}")
                    println("最终验证 - 文件存在: ${finalCheck?.exists()}")
                    println("最终验证 - 文件大小: ${finalCheck?.length()} bytes")
                }

            } catch (e: Exception) {
                println("创建动作失败: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * 删除训练组
     */
    fun deleteExerciseSet(exerciseSet: ExerciseSet) {
        viewModelScope.launch {
            try {
                exerciseSetDao.deleteSet(exerciseSet)
                _currentSession.value?.let { session ->
                    loadSessionSets(session.id)
                }
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    /**
     * 更新训练会话名称
     */
    fun updateSessionName(name: String) {
        val session = _currentSession.value ?: return
        viewModelScope.launch {
            try {
                val updatedSession = session.copy(name = name)
                trainingSessionDao.updateSession(updatedSession)
                _currentSession.value = updatedSession
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    /**
     * 完成训练会话
     */
    fun finishSession() {
        _currentSession.value = null
        _currentExerciseSets.value = emptyList()
    }

    /**
     * 关闭个人记录对话框
     */
    fun dismissPersonalRecordDialog() {
        _showPersonalRecordDialog.value = null
    }

    /**
     * 加载训练会话的所有训练组
     */
    private fun loadSessionSets(sessionId: Long) {
        viewModelScope.launch {
            try {
                exerciseSetDao.getSetDetailsForSession(sessionId).collect { sets ->
                    _currentExerciseSets.value = sets
                }
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    /**
     * 自动创建或加载今天的训练会话
     */
    fun createTodaySessionIfNeeded() {
        viewModelScope.launch {
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

            try {
                // 查找今天范围内的训练会话
                trainingSessionDao.getSessionsBetweenDates(todayStart, todayEnd).collect { sessions ->
                    if (sessions.isNotEmpty()) {
                        // 如果今天已有会话，使用第一个
                        val existingSession = sessions.first()
                        _currentSession.value = existingSession
                        loadSessionSets(existingSession.id)
                    } else {
                        // 创建新的训练会话
                        val newSession = TrainingSession(
                            date = todayStart,
                            name = "今日训练",
                            notes = ""
                        )
                        val sessionId = trainingSessionDao.insertSession(newSession)
                        _currentSession.value = newSession.copy(id = sessionId)
                        _currentExerciseSets.value = emptyList()
                    }
                }
            } catch (e: Exception) {
                // 处理错误，创建新会话作为备选
                createNewSession("今日训练")
            }
        }
    }
}
