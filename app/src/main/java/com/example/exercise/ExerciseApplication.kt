package com.example.exercise

import android.app.Application
import com.example.exercise.data.database.ExerciseDatabase
import com.example.exercise.data.repository.ExerciseRepository

/**
 * 应用程序类，用于初始化全局组件
 */
class ExerciseApplication : Application() {
    // 使用lazy进行延迟初始化
    val database by lazy { ExerciseDatabase.getInstance(this) }
    val repository by lazy {
        ExerciseRepository(database)
    }
}
