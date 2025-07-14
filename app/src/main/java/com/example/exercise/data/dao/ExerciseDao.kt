package com.example.exercise.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.exercise.data.model.Exercise
import com.example.exercise.data.model.ExerciseSet
import com.example.exercise.data.model.ExerciseSetWithDetails
import com.example.exercise.data.model.ExerciseType
import com.example.exercise.data.model.LastExerciseRecord
import com.example.exercise.data.model.PersonalRecord
import com.example.exercise.data.model.TrainingSession
import com.example.exercise.data.model.TrainingSessionWithSets
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExerciseDao {
    // 动作相关操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>): List<Long>

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("DELETE FROM exercises WHERE id = :exerciseId")
    suspend fun deleteExerciseById(exerciseId: Long)

    @Query("SELECT * FROM exercises ORDER BY name")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE muscleGroup = :muscleGroup ORDER BY name")
    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE exerciseType = :exerciseType ORDER BY name")
    fun getExercisesByType(exerciseType: ExerciseType): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :exerciseId")
    suspend fun getExerciseById(exerciseId: Long): Exercise

    // 根据ID列表获取动作列表（同步方法）
    @Query("SELECT * FROM exercises WHERE id IN (:exerciseIds)")
    suspend fun getExercisesByIdsSync(exerciseIds: List<Long>): List<Exercise>
}

@Dao
interface TrainingSessionDao {
    // 训练记录相关操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TrainingSession): Long

    @Update
    suspend fun updateSession(session: TrainingSession)

    @Delete
    suspend fun deleteSession(session: TrainingSession)

    @Query("SELECT * FROM training_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<TrainingSession>>

    @Query("SELECT * FROM training_sessions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getSessionsBetweenDates(startDate: Date, endDate: Date): Flow<List<TrainingSession>>

    @Query("SELECT * FROM training_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): TrainingSession

    // 获取带有所有训练组详情的训练记录
    @Transaction
    @Query("SELECT * FROM training_sessions WHERE id = :sessionId")
    fun getSessionWithSets(sessionId: Long): Flow<TrainingSessionWithSets>

    // 获取某一天的训练记录
    @Query("SELECT * FROM training_sessions WHERE date = :date LIMIT 1")
    suspend fun getSessionByDate(date: Date): TrainingSession?
}

@Dao
interface ExerciseSetDao {
    // 训练组相关操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(exerciseSet: ExerciseSet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(exerciseSets: List<ExerciseSet>): List<Long>

    @Update
    suspend fun updateSet(exerciseSet: ExerciseSet)

    @Delete
    suspend fun deleteSet(exerciseSet: ExerciseSet)

    // 获取特定训练记录中的所有训练组
    @Query("SELECT * FROM exercise_sets WHERE sessionId = :sessionId ORDER BY exerciseId, setNumber")
    fun getSetsBySessionId(sessionId: Long): Flow<List<ExerciseSet>>

    // 获取特定训练记录中的所有训练组（同步方法）
    @Query("SELECT * FROM exercise_sets WHERE sessionId = :sessionId ORDER BY exerciseId, setNumber")
    suspend fun getExerciseSetsForSessionSync(sessionId: Long): List<ExerciseSet>

    // 获取特定动作的所有训练组记录（历史记录）
    @Query("SELECT * FROM exercise_sets WHERE exerciseId = :exerciseId ORDER BY timestamp DESC")
    fun getSetsByExerciseId(exerciseId: Long): Flow<List<ExerciseSet>>

    // 获取特定动作在特定训练记录中的所有组
    @Query("SELECT * FROM exercise_sets WHERE sessionId = :sessionId AND exerciseId = :exerciseId ORDER BY setNumber")
    fun getSetsBySessionAndExercise(sessionId: Long, exerciseId: Long): Flow<List<ExerciseSet>>

    // 获取特定动作在特定训练记录中的所有组（同步方法）
    @Query("SELECT * FROM exercise_sets WHERE sessionId = :sessionId AND exerciseId = :exerciseId ORDER BY setNumber")
    suspend fun getSetsBySessionAndExerciseSync(sessionId: Long, exerciseId: Long): List<ExerciseSet>

    // 获取特定训练记录中使用的所有动作ID
    @Query("SELECT DISTINCT exerciseId FROM exercise_sets WHERE sessionId = :sessionId")
    suspend fun getExerciseIdsForSessionSync(sessionId: Long): List<Long>

    // 获取带有动作详情的训练组
    @Transaction
    @Query("SELECT es.* FROM exercise_sets es JOIN exercises e ON es.exerciseId = e.id WHERE es.sessionId = :sessionId ORDER BY e.name, es.setNumber")
    fun getSetDetailsForSession(sessionId: Long): Flow<List<ExerciseSetWithDetails>>

    // 新增：获取特定动作的最后一次训练记录（用于提供默认值）
    @Query("""
        SELECT 
            exerciseId,
            weight as lastWeight,
            reps as lastReps,
            timestamp as lastDate
        FROM exercise_sets 
        WHERE exerciseId = :exerciseId 
        ORDER BY timestamp DESC 
        LIMIT 1
    """)
    suspend fun getLastExerciseRecord(exerciseId: Long): LastExerciseRecord?

    // 新增：获取特定动作的平均次数（如果没有历史记录则返回10）
    @Query("""
        SELECT COALESCE(AVG(reps), 10) 
        FROM exercise_sets 
        WHERE exerciseId = :exerciseId
    """)
    suspend fun getAverageRepsForExercise(exerciseId: Long): Int
}

@Dao
interface PersonalRecordDao {
    // 个人记录相关操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: PersonalRecord): Long

    @Update
    suspend fun updateRecord(record: PersonalRecord)

    @Delete
    suspend fun deleteRecord(record: PersonalRecord)

    // 获取特定动作的所有个人记录
    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId ORDER BY reps")
    fun getRecordsByExerciseId(exerciseId: Long): Flow<List<PersonalRecord>>

    // 获取特定动作在特定次数下的个人记录
    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId AND reps = :reps LIMIT 1")
    suspend fun getRecordByExerciseAndReps(exerciseId: Long, reps: Int): PersonalRecord?

    // 检查是否为个人记录
    @Query("SELECT EXISTS(SELECT 1 FROM personal_records WHERE exerciseId = :exerciseId AND reps = :reps AND weight < :weight)")
    suspend fun isPersonalRecord(exerciseId: Long, reps: Int, weight: Float): Boolean

    // 获取所有个人记录
    @Query("SELECT * FROM personal_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<PersonalRecord>>
}
