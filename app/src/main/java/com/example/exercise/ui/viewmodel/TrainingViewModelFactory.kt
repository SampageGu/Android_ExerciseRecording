package com.example.exercise.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.exercise.data.dao.ExerciseDao
import com.example.exercise.data.dao.ExerciseSetDao
import com.example.exercise.data.dao.PersonalRecordDao
import com.example.exercise.data.dao.TrainingSessionDao

/**
 * ViewModelFactory用于创建TrainingViewModel
 */
class TrainingViewModelFactory(
    private val exerciseDao: ExerciseDao,
    private val trainingSessionDao: TrainingSessionDao,
    private val exerciseSetDao: ExerciseSetDao,
    private val personalRecordDao: PersonalRecordDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrainingViewModel::class.java)) {
            return TrainingViewModel(
                exerciseDao = exerciseDao,
                trainingSessionDao = trainingSessionDao,
                exerciseSetDao = exerciseSetDao,
                personalRecordDao = personalRecordDao
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
