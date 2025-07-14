package com.example.exercise.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.exercise.ExerciseApplication
import com.example.exercise.data.model.TrainingSession
import com.example.exercise.data.model.TrainingSessionWithSets
import com.example.exercise.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class HistoryViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(Calendar.getInstance())
    val selectedMonth: StateFlow<Calendar> = _selectedMonth.asStateFlow()

    private val _selectedSession = MutableStateFlow<TrainingSessionWithSets?>(null)
    val selectedSession: StateFlow<TrainingSessionWithSets?> = _selectedSession.asStateFlow()

    // 获取当月的所有训练会话
    val sessionsThisMonth: StateFlow<List<TrainingSession>> = selectedMonth
        .flatMapLatest { calendar ->
            val startOfMonth = getStartOfMonth(calendar)
            val endOfMonth = getEndOfMonth(calendar)
            repository.getSessionsBetweenDatesFlow(startOfMonth, endOfMonth)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectMonth(month: Calendar) {
        _selectedMonth.value = month
    }

    fun selectSession(sessionId: Long) {
        viewModelScope.launch {
            try {
                val sessionWithSets = repository.getSessionWithSets(sessionId)
                _selectedSession.value = sessionWithSets
            } catch (e: Exception) {
                _selectedSession.value = null
            }
        }
    }

    fun clearSelectedSession() {
        _selectedSession.value = null
    }

    private fun getStartOfMonth(calendar: Calendar): Date {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    private fun getEndOfMonth(calendar: Calendar): Date {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return HistoryViewModel(
                    (application as ExerciseApplication).repository
                ) as T
            }
        }
    }
}
