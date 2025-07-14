package com.example.exercise.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

/**
 * 动作类型枚举
 */
enum class ExerciseType(
    val displayName: String,
    val weightIncrement: Float,
    val weightRange: Pair<Float, Float>
) {
    MACHINE("器械训练", 1.0f, 15.0f to 100.0f),           // 器械型：15-100kg，每次增加1kg
    FREE_WEIGHT("自由重量", 2.5f, 2.5f to 100.0f),        // 哑铃/杠铃型：2.5kg起，每次增加2.5kg
    BODYWEIGHT("自重训练", 0.0f, 0.0f to 0.0f)            // 无器械型：不涉及重量
}

/**
 * 训练动作实体
 */
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,                    // 动作名称
    val muscleGroup: String,             // 主要目标肌群
    val exerciseType: ExerciseType,      // 动作类型
    val description: String = "",        // 动作描述
    val imageUrl: String = "",          // 动作图片URL
    val isCompound: Boolean = false,     // 是否为复合动作
    val defaultWeight: Float = 0f,       // 默认重量
    val defaultReps: Int = 10            // 默认次数
)

/**
 * 训练记录实体 - 一次训练中的所有动作记录
 */
@Entity(tableName = "training_sessions")
data class TrainingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Date,             // 训练日期
    val name: String = "",      // 训练名称（如："腿部日"、"推拉日"）
    val notes: String = ""      // 训练笔记
)

/**
 * 训练动作组实体 - 一次训练中特定动作的记录
 */
@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = TrainingSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("sessionId"),
        Index("exerciseId")
    ]
)
data class ExerciseSet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,        // 关联的训练记录ID
    val exerciseId: Long,       // 关联的训练动作ID
    val setNumber: Int,         // 组号
    val weight: Float,          // 重量（千克）
    val reps: Int,              // 次数
    val isPersonalRecord: Boolean = false, // 是否为个人记录
    val timestamp: Date = Date() // 记录时间戳，用于获取"上一次"数据
)

/**
 * 个人记录实体 - 跟踪用户在特定动作上的最佳表现
 */
@Entity(
    tableName = "personal_records",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseId", "reps", unique = true)]
)
data class PersonalRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseId: Long,       // 关联的训练动作ID
    val reps: Int,              // 次数
    val weight: Float,          // 重量（千克）
    val date: Date,             // 记录日期
    val setId: Long             // 关联的训练组ID
)

/**
 * 动作的最后一次训练记录 - 用于提供默认值
 */
data class LastExerciseRecord(
    val exerciseId: Long,
    val lastWeight: Float,
    val lastReps: Int,
    val lastDate: Date
)

/**
 * 带有动作详情的训练组
 */
data class ExerciseSetWithDetails(
    @androidx.room.Embedded
    val set: ExerciseSet,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "id"
    )
    val exercise: Exercise
)

/**
 * 带有所有训练组的训练记录
 */
data class TrainingSessionWithSets(
    @androidx.room.Embedded
    val session: TrainingSession,
    @Relation(
        entity = ExerciseSet::class,
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val sets: List<ExerciseSetWithDetails>
)
