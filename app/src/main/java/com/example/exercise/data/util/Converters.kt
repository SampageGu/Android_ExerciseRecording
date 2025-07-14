package com.example.exercise.data.util

import androidx.room.TypeConverter
import com.example.exercise.data.model.ExerciseType
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromExerciseType(value: ExerciseType): String {
        return value.name
    }

    @TypeConverter
    fun toExerciseType(value: String): ExerciseType {
        return ExerciseType.valueOf(value)
    }
}
