package com.example.exercise.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.exercise.data.dao.ExerciseDao
import com.example.exercise.data.dao.ExerciseSetDao
import com.example.exercise.data.dao.PersonalRecordDao
import com.example.exercise.data.dao.TrainingSessionDao
import com.example.exercise.data.model.Exercise
import com.example.exercise.data.model.ExerciseSet
import com.example.exercise.data.model.ExerciseType
import com.example.exercise.data.model.PersonalRecord
import com.example.exercise.data.model.TrainingSession
import com.example.exercise.data.util.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Exercise::class,
        TrainingSession::class,
        ExerciseSet::class,
        PersonalRecord::class
    ],
    version = 3,  // 增加版本号以支持新的数据结构
    exportSchema = false
)
@TypeConverters(Converters::class)  // 使用新的转换器
abstract class ExerciseDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun trainingSessionDao(): TrainingSessionDao
    abstract fun exerciseSetDao(): ExerciseSetDao
    abstract fun personalRecordDao(): PersonalRecordDao

    companion object {
        @Volatile
        private var INSTANCE: ExerciseDatabase? = null

        fun getInstance(context: Context): ExerciseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExerciseDatabase::class.java,
                    "exercise_database"
                )
                    .fallbackToDestructiveMigration()  // 简化迁移过程
                    .addCallback(DatabaseCallback())
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // 在数据库创建时添加初始数据
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.exerciseDao())
                    }
                }
            }
        }

        private suspend fun populateDatabase(exerciseDao: ExerciseDao) {
            // 添加初始的训练动作数据
            val initialExercises = listOf(
                // 器械训练
                Exercise(name = "坐姿推胸", muscleGroup = "胸部", exerciseType = ExerciseType.MACHINE, description = "器械坐姿推胸", defaultWeight = 20f, defaultReps = 10),
                Exercise(name = "高位下拉", muscleGroup = "背部", exerciseType = ExerciseType.MACHINE, description = "高位下拉训练背部", defaultWeight = 25f, defaultReps = 10),
                Exercise(name = "腿举", muscleGroup = "腿部", exerciseType = ExerciseType.MACHINE, description = "腿举训练腿部力量", defaultWeight = 30f, defaultReps = 10),
                Exercise(name = "肩部推举机", muscleGroup = "肩部", exerciseType = ExerciseType.MACHINE, description = "器械肩部推举", defaultWeight = 15f, defaultReps = 10),

                // 自由重量
                Exercise(name = "哑铃卧推", muscleGroup = "胸部", exerciseType = ExerciseType.FREE_WEIGHT, description = "哑铃平板卧推", defaultWeight = 22.5f, defaultReps = 10),
                Exercise(name = "哑铃划船", muscleGroup = "背部", exerciseType = ExerciseType.FREE_WEIGHT, description = "单臂哑铃划船", defaultWeight = 20f, defaultReps = 10),
                Exercise(name = "哑铃深蹲", muscleGroup = "腿部", exerciseType = ExerciseType.FREE_WEIGHT, description = "哑铃深蹲训练", defaultWeight = 25f, defaultReps = 10),
                Exercise(name = "哑铃推举", muscleGroup = "肩部", exerciseType = ExerciseType.FREE_WEIGHT, description = "哑铃肩部推举", defaultWeight = 17.5f, defaultReps = 10),
                Exercise(name = "杠铃深蹲", muscleGroup = "腿部", exerciseType = ExerciseType.FREE_WEIGHT, description = "杠铃深蹲", defaultWeight = 40f, defaultReps = 10),
                Exercise(name = "杠铃硬拉", muscleGroup = "背部", exerciseType = ExerciseType.FREE_WEIGHT, description = "杠铃硬拉", defaultWeight = 50f, defaultReps = 10),

                // 自重训练
                Exercise(name = "俯卧撑", muscleGroup = "胸部", exerciseType = ExerciseType.BODYWEIGHT, description = "标准俯卧撑", defaultWeight = 0f, defaultReps = 15),
                Exercise(name = "引体向上", muscleGroup = "背部", exerciseType = ExerciseType.BODYWEIGHT, description = "引体向上训练", defaultWeight = 0f, defaultReps = 8),
                Exercise(name = "卷腹", muscleGroup = "腹部", exerciseType = ExerciseType.BODYWEIGHT, description = "腹部卷腹训练", defaultWeight = 0f, defaultReps = 15),
                Exercise(name = "深蹲", muscleGroup = "腿部", exerciseType = ExerciseType.BODYWEIGHT, description = "自重深蹲", defaultWeight = 0f, defaultReps = 15),
                Exercise(name = "平板支撑", muscleGroup = "核心", exerciseType = ExerciseType.BODYWEIGHT, description = "平板支撑核心训练", defaultWeight = 0f, defaultReps = 60)
            )

            exerciseDao.insertExercises(initialExercises)
        }
    }
}
