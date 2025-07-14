package com.example.exercise.data.repository

import com.example.exercise.data.dao.ExerciseDao
import com.example.exercise.data.dao.ExerciseSetDao
import com.example.exercise.data.dao.TrainingSessionDao
import com.example.exercise.data.model.Exercise
import com.example.exercise.data.model.ExerciseSet
import com.example.exercise.data.model.TrainingSession
import com.example.exercise.data.model.TrainingSessionWithSets
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * 训练记录仓库，处理所有与训练记录相关的数据操作
 */
class TrainingRepository(
    private val exerciseDao: ExerciseDao,
    private val trainingSessionDao: TrainingSessionDao,
    private val exerciseSetDao: ExerciseSetDao
) {
    // 训练会话相关操作
    fun getAllSessions(): Flow<List<TrainingSession>> = trainingSessionDao.getAllSessions()

    fun getSessionsBetweenDates(startDate: Date, endDate: Date): Flow<List<TrainingSession>> =
        trainingSessionDao.getSessionsBetweenDates(startDate, endDate)

    suspend fun getSessionById(sessionId: Long): TrainingSession = trainingSessionDao.getSessionById(sessionId)

    fun getSessionWithSets(sessionId: Long): Flow<TrainingSessionWithSets> =
        trainingSessionDao.getSessionWithSets(sessionId)

    suspend fun getSessionByDate(date: Date): TrainingSession? = trainingSessionDao.getSessionByDate(date)

    suspend fun insertTrainingSession(session: TrainingSession): Long = trainingSessionDao.insertSession(session)

    suspend fun updateTrainingSession(session: TrainingSession) = trainingSessionDao.updateSession(session)

    suspend fun deleteTrainingSession(session: TrainingSession) = trainingSessionDao.deleteSession(session)

    // 训练组相关操作
    fun getExerciseSetsBySessionId(sessionId: Long): Flow<List<ExerciseSet>> =
        exerciseSetDao.getSetsBySessionId(sessionId)

    suspend fun getExerciseSetsForSession(sessionId: Long): List<ExerciseSet> =
        exerciseSetDao.getExerciseSetsForSessionSync(sessionId)

    fun getExerciseSetsByExerciseId(exerciseId: Long): Flow<List<ExerciseSet>> =
        exerciseSetDao.getSetsByExerciseId(exerciseId)

    fun getExerciseSetsBySessionAndExercise(sessionId: Long, exerciseId: Long): Flow<List<ExerciseSet>> =
        exerciseSetDao.getSetsBySessionAndExercise(sessionId, exerciseId)

    suspend fun insertExerciseSet(exerciseSet: ExerciseSet): Long = exerciseSetDao.insertSet(exerciseSet)

    suspend fun insertExerciseSets(exerciseSets: List<ExerciseSet>): List<Long> = exerciseSetDao.insertSets(exerciseSets)

    suspend fun updateExerciseSet(exerciseSet: ExerciseSet) = exerciseSetDao.updateSet(exerciseSet)

    suspend fun deleteExerciseSet(exerciseSet: ExerciseSet) = exerciseSetDao.deleteSet(exerciseSet)

    /**
     * 获取特定会话和动作的下一个组号
     */
    suspend fun getNextSetNumberForExercise(sessionId: Long, exerciseId: Long): Int {
        val currentSets = exerciseSetDao.getSetsBySessionAndExerciseSync(sessionId, exerciseId)
        return if (currentSets.isEmpty()) 1 else currentSets.maxOf { it.setNumber } + 1
    }

    /**
     * 获取今日的训练动作列表（包含详情）
     */
    suspend fun getTodayExercises(sessionId: Long): List<Exercise> {
        val exerciseIds = exerciseSetDao.getExerciseIdsForSessionSync(sessionId)
        return exerciseDao.getExercisesByIdsSync(exerciseIds)
    }
}
