package com.example.exercise.data.util

import com.example.exercise.data.database.ExerciseDatabase
import com.example.exercise.data.model.Exercise
import com.example.exercise.data.model.ExerciseType

/**
 * 预填充初始训练动作库数据
 */
suspend fun populateInitialExercises(database: ExerciseDatabase) {
    val exerciseDao = database.exerciseDao()

    // 胸部训练动作
    val chestExercises = listOf(
        Exercise(name = "卧推", muscleGroup = "胸部", exerciseType = ExerciseType.FREE_WEIGHT, description = "平板杠铃卧推，胸肌基础复合动作", isCompound = true),
        Exercise(name = "上斜卧推", muscleGroup = "胸部", exerciseType = ExerciseType.FREE_WEIGHT, description = "��斜杠铃卧推，强调上胸肌群", isCompound = true),
        Exercise(name = "下斜卧推", muscleGroup = "胸部", exerciseType = ExerciseType.FREE_WEIGHT, description = "下斜杠铃卧推，强调下胸肌群", isCompound = true),
        Exercise(name = "哑铃飞鸟", muscleGroup = "胸部", exerciseType = ExerciseType.FREE_WEIGHT, description = "哑铃飞鸟，拉伸胸肌", isCompound = false),
        Exercise(name = "器械夹胸", muscleGroup = "胸部", exerciseType = ExerciseType.MACHINE, description = "坐姿器械夹胸，专注胸肌内侧", isCompound = false)
    )

    // 背部训练动作
    val backExercises = listOf(
        Exercise(name = "引体向上", muscleGroup = "背部", exerciseType = ExerciseType.BODYWEIGHT, description = "标准引体向上，背部基础动作", isCompound = true),
        Exercise(name = "杠铃划船", muscleGroup = "背部", exerciseType = ExerciseType.FREE_WEIGHT, description = "杠铃划船，强化整个背阔肌", isCompound = true),
        Exercise(name = "单臂哑铃划船", muscleGroup = "背部", exerciseType = ExerciseType.FREE_WEIGHT, description = "单臂哑铃划船，专注单侧背部发力", isCompound = false),
        Exercise(name = "高位下拉", muscleGroup = "背部", exerciseType = ExerciseType.MACHINE, description = "高位下拉，模拟引体向上动作", isCompound = true),
        Exercise(name = "坐姿划船", muscleGroup = "背部", exerciseType = ExerciseType.MACHINE, description = "坐姿划船机，全面刺激背部肌群", isCompound = true)
    )

    // 腿部训练动作
    val legExercises = listOf(
        Exercise(name = "深蹲", muscleGroup = "腿部", exerciseType = ExerciseType.FREE_WEIGHT, description = "标准杠铃深蹲，���肢基础复合动作", isCompound = true),
        Exercise(name = "硬拉", muscleGroup = "腿部", exerciseType = ExerciseType.FREE_WEIGHT, description = "标准硬拉，强化腿部后侧链与下背", isCompound = true),
        Exercise(name = "腿举", muscleGroup = "腿部", exerciseType = ExerciseType.MACHINE, description = "腿举机，强调股四头肌", isCompound = false),
        Exercise(name = "腿屈伸", muscleGroup = "腿部", exerciseType = ExerciseType.MACHINE, description = "腿屈伸机，隔离股四头肌", isCompound = false),
        Exercise(name = "腿弯举", muscleGroup = "腿部", exerciseType = ExerciseType.MACHINE, description = "腿弯举机，强化腘绳肌", isCompound = false)
    )

    // 肩部训练动作
    val shoulderExercises = listOf(
        Exercise(name = "肩上推举", muscleGroup = "肩部", exerciseType = ExerciseType.FREE_WEIGHT, description = "杠铃肩上推举，肩部基础复合动作", isCompound = true),
        Exercise(name = "哑铃侧平举", muscleGroup = "肩部", exerciseType = ExerciseType.FREE_WEIGHT, description = "哑铃侧平举，强调三角肌中束", isCompound = false),
        Exercise(name = "哑铃前平举", muscleGroup = "肩部", exerciseType = ExerciseType.FREE_WEIGHT, description = "哑铃前平举，强调三角肌前束", isCompound = false),
        Exercise(name = "俯身飞鸟", muscleGroup = "肩部", exerciseType = ExerciseType.FREE_WEIGHT, description = "俯身哑铃飞鸟，强调三角肌后束", isCompound = false),
        Exercise(name = "直立划船", muscleGroup = "肩部", exerciseType = ExerciseType.FREE_WEIGHT, description = "直立杠铃划船，强化三角肌后束与斜方肌", isCompound = true)
    )

    // 手臂训练动作
    val armExercises = listOf(
        Exercise(name = "杠铃弯举", muscleGroup = "手臂", exerciseType = ExerciseType.FREE_WEIGHT, description = "杠铃弯举，二头肌基础动作", isCompound = false),
        Exercise(name = "窄距卧推", muscleGroup = "手臂", exerciseType = ExerciseType.FREE_WEIGHT, description = "窄距卧推，强调三头肌", isCompound = true),
        Exercise(name = "哑铃弯举", muscleGroup = "手臂", exerciseType = ExerciseType.FREE_WEIGHT, description = "哑铃弯举，二头肌单侧训练", isCompound = false),
        Exercise(name = "三头肌下压", muscleGroup = "手臂", exerciseType = ExerciseType.MACHINE, description = "绳索三头肌下压，隔离三头肌", isCompound = false)
    )

    // 腹部训练动作
    val coreExercises = listOf(
        Exercise(name = "卷腹", muscleGroup = "腹部", exerciseType = ExerciseType.BODYWEIGHT, description = "标准卷腹，腹肌基础动作", isCompound = false),
        Exercise(name = "平板支撑", muscleGroup = "核心", exerciseType = ExerciseType.BODYWEIGHT, description = "平板支撑，核心稳定性训练", isCompound = false),
        Exercise(name = "俄罗斯转体", muscleGroup = "腹部", exerciseType = ExerciseType.BODYWEIGHT, description = "俄罗斯转体，腹斜肌训练", isCompound = false),
        Exercise(name = "仰卧举腿", muscleGroup = "腹部", exerciseType = ExerciseType.BODYWEIGHT, description = "仰卧举腿，下腹肌训练", isCompound = false)
    )

    // 将所有动作组合成一个列表
    val allExercises = chestExercises + backExercises + legExercises + shoulderExercises + armExercises + coreExercises

    // 将动作插入数据库
    exerciseDao.insertExercises(allExercises)
}
