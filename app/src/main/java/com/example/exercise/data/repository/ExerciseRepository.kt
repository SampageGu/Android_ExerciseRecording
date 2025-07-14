package com.example.exercise.data.repository

import com.example.exercise.data.dao.ExerciseDao
import com.example.exercise.data.dao.ExerciseSetDao
import com.example.exercise.data.dao.PersonalRecordDao
import com.example.exercise.data.dao.TrainingSessionDao
import com.example.exercise.data.database.ExerciseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import com.example.exercise.data.model.*
import java.util.Date

/**
 * 训练数据仓库
 * 提供统一的数据访问接口
 */
class ExerciseRepository(
    private val database: ExerciseDatabase
) {
    private val exerciseDao = database.exerciseDao()
    private val trainingSessionDao = database.trainingSessionDao()
    private val exerciseSetDao = database.exerciseSetDao()
    private val personalRecordDao = database.personalRecordDao()

    // 动作相关
    fun getAllExercises(): Flow<List<Exercise>> = exerciseDao.getAllExercises()

    fun getExercisesByType(type: ExerciseType): Flow<List<Exercise>> =
        exerciseDao.getExercisesByType(type)

    suspend fun getExerciseById(exerciseId: Long): Exercise =
        exerciseDao.getExerciseById(exerciseId)

    suspend fun insertExercise(exercise: Exercise): Long =
        exerciseDao.insertExercise(exercise)

    suspend fun updateExercise(exercise: Exercise) =
        exerciseDao.updateExercise(exercise)

    suspend fun deleteExercise(exercise: Exercise) =
        exerciseDao.deleteExercise(exercise)

    suspend fun deleteExerciseById(exerciseId: Long) =
        exerciseDao.deleteExerciseById(exerciseId)

    // 训练会话相关
    suspend fun insertSession(session: TrainingSession): Long =
        trainingSessionDao.insertSession(session)

    suspend fun updateSession(session: TrainingSession) =
        trainingSessionDao.updateSession(session)

    suspend fun getSessionByDate(date: Date): TrainingSession? =
        trainingSessionDao.getSessionByDate(date)

    // 新增：获取日期范围内的训练会话
    suspend fun getSessionsBetweenDates(startDate: Date, endDate: Date): List<TrainingSession> {
        return try {
            // 使用first()获取一次性结果而不是collect
            trainingSessionDao.getSessionsBetweenDates(startDate, endDate).first()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 新增：获取训���会话详情（包含训练组数据）
    suspend fun getSessionWithSets(sessionId: Long): TrainingSessionWithSets? {
        return try {
            trainingSessionDao.getSessionWithSets(sessionId).first()
        } catch (e: Exception) {
            null
        }
    }

    // 新增：获取日期范围内的训练会话（Flow版本）
    fun getSessionsBetweenDatesFlow(startDate: Date, endDate: Date): Flow<List<TrainingSession>> =
        trainingSessionDao.getSessionsBetweenDates(startDate, endDate)

    // 训练组相关
    suspend fun insertExerciseSet(set: ExerciseSet): Long =
        exerciseSetDao.insertSet(set)

    suspend fun deleteExerciseSet(set: ExerciseSet) =
        exerciseSetDao.deleteSet(set)

    fun getSetDetailsForSession(sessionId: Long): Flow<List<ExerciseSetWithDetails>> =
        exerciseSetDao.getSetDetailsForSession(sessionId)

    suspend fun getSetsBySessionAndExercise(sessionId: Long, exerciseId: Long): List<ExerciseSet> =
        exerciseSetDao.getSetsBySessionAndExerciseSync(sessionId, exerciseId)

    suspend fun getLastExerciseRecord(exerciseId: Long): LastExerciseRecord? =
        exerciseSetDao.getLastExerciseRecord(exerciseId)

    // 个人记录相关
    suspend fun insertPersonalRecord(record: PersonalRecord): Long =
        personalRecordDao.insertRecord(record)

    suspend fun isPersonalRecord(exerciseId: Long, reps: Int, weight: Float): Boolean =
        personalRecordDao.isPersonalRecord(exerciseId, reps, weight)
}
