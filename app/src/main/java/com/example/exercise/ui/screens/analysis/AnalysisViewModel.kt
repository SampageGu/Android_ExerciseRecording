package com.example.exercise.ui.screens.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.exercise.ExerciseApplication
import com.example.exercise.data.model.Exercise
import com.example.exercise.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/**
 * 表示时间范围的枚举 - 优化版本
 */
enum class TimeRange(val displayName: String, val days: Int) {
    THREE_DAYS("过去3天", 3),
    ONE_WEEK("1周", 7),
    ONE_MONTH("1个月", 30),
    THREE_MONTHS("3个月", 90),
    SIX_MONTHS("6个月", 180),
    ONE_YEAR("1年", 365)
}

/**
 * 表示时间单位的枚举（用于训练容量趋势图）
 */
enum class TimeUnit {
    WEEKLY, MONTHLY
}

/**
 * 分析图表的数据点
 */
data class ChartPoint(
    val date: Date,
    val value: Float
)

/**
 * 分析屏幕的UI状态
 */
data class AnalysisUiState(
    val allExercises: List<Exercise> = emptyList(),
    val selectedExercise: Exercise? = null,
    val progressChartData: List<ChartPoint> = emptyList(),
    val volumeChartData: List<ChartPoint> = emptyList(),
    val selectedTimeRange: TimeRange = TimeRange.ONE_WEEK,
    val selectedTimeUnit: TimeUnit = TimeUnit.WEEKLY,
    val isLoading: Boolean = false
)

/**
 * 分析屏幕的ViewModel
 */
class AnalysisViewModel(private val repository: ExerciseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisUiState(isLoading = true))
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    init {
        loadAllExercises()
    }

    /**
     * 加载所有训练动作
     */
    private fun loadAllExercises() {
        viewModelScope.launch {
            repository.getAllExercises().collectLatest { exercises ->
                _uiState.update {
                    it.copy(
                        allExercises = exercises,
                        isLoading = false
                    )
                }

                // 如果还没有选择动作，自动选择第一个
                if (_uiState.value.selectedExercise == null && exercises.isNotEmpty()) {
                    selectExercise(exercises.first())
                }
            }
        }
    }

    /**
     * 选择要分析的训练动作
     */
    fun selectExercise(exercise: Exercise) {
        _uiState.update {
            it.copy(
                selectedExercise = exercise,
                isLoading = true
            )
        }

        loadProgressChartData(exercise)
    }

    /**
     * 更改时间范围
     */
    fun setTimeRange(timeRange: TimeRange) {
        _uiState.update {
            it.copy(
                selectedTimeRange = timeRange,
                isLoading = true
            )
        }

        _uiState.value.selectedExercise?.let { loadProgressChartData(it) }
        loadVolumeChartData()
    }

    /**
     * 更改时间单位（周/月）
     */
    fun setTimeUnit(timeUnit: TimeUnit) {
        _uiState.update {
            it.copy(
                selectedTimeUnit = timeUnit,
                isLoading = true
            )
        }

        loadVolumeChartData()
    }

    /**
     * 加载进度曲线图表数据
     */
    private fun loadProgressChartData(exercise: Exercise) {
        viewModelScope.launch {
            // 获取时间范围的开始日期
            val startDate = getStartDateForTimeRange()

            // 从数据库获取真实的训练记录数据，而不是生成模拟数据
            val chartPoints = try {
                // 这里应该调用 repository 获取真实数据
                // 例如：repository.getProgressDataForExercise(exercise.id, startDate)
                // 暂时返回空列表，直到有真实数据
                emptyList<ChartPoint>()
            } catch (e: Exception) {
                emptyList()
            }

            _uiState.update {
                it.copy(
                    progressChartData = chartPoints,
                    isLoading = false
                )
            }

            // ��时加载容量趋势图
            loadVolumeChartData()
        }
    }

    /**
     * 加载训练容量趋势图表数据
     */
    private fun loadVolumeChartData() {
        viewModelScope.launch {
            // 获取时间范围的开始日期
            val startDate = getStartDateForTimeRange()

            // 模拟数据 - 在实际应用中，这里会从数据库获取真实数据
            val chartPoints = generateSampleVolumeData(startDate, _uiState.value.selectedTimeUnit)

            _uiState.update {
                it.copy(
                    volumeChartData = chartPoints,
                    isLoading = false
                )
            }
        }
    }

    /**
     * 根据所选时间范围获取开始日期 - 优化版本
     */
    private fun getStartDateForTimeRange(): Date {
        val calendar = Calendar.getInstance()

        when (_uiState.value.selectedTimeRange) {
            TimeRange.THREE_DAYS -> calendar.add(Calendar.DAY_OF_MONTH, -3)
            TimeRange.ONE_WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, -1)
            TimeRange.ONE_MONTH -> calendar.add(Calendar.MONTH, -1)
            TimeRange.THREE_MONTHS -> calendar.add(Calendar.MONTH, -3)
            TimeRange.SIX_MONTHS -> calendar.add(Calendar.MONTH, -6)
            TimeRange.ONE_YEAR -> calendar.add(Calendar.YEAR, -1)
        }

        return calendar.time
    }

    /**
     * 生成示例容量数据（仅用于演示）
     */
    private fun generateSampleVolumeData(startDate: Date, timeUnit: TimeUnit): List<ChartPoint> {
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        val result = mutableListOf<ChartPoint>()
        val random = java.util.Random()
        var baseVolume = 5000f  // 基础训练容量

        // 生成从开始日期到今天的数据点
        when (timeUnit) {
            TimeUnit.WEEKLY -> {
                // 按周汇总数据
                while (calendar.time.before(Date())) {
                    // 每周的训练容量有波动
                    val volume = baseVolume + random.nextFloat() * 2000 - 500  // -500到1500之间的随机波动

                    result.add(ChartPoint(
                        date = Date(calendar.time.time),
                        value = volume
                    ))

                    baseVolume += random.nextFloat() * 200  // 总体呈上升趋势
                    calendar.add(Calendar.WEEK_OF_YEAR, 1)
                }
            }
            TimeUnit.MONTHLY -> {
                // 按月汇总数据
                while (calendar.time.before(Date())) {
                    // 每月的训练容量有波动
                    val volume = baseVolume * 4 + random.nextFloat() * 5000 - 2000  // 更大的范围波动

                    result.add(ChartPoint(
                        date = Date(calendar.time.time),
                        value = volume
                    ))

                    baseVolume += random.nextFloat() * 500  // 总体呈上升趋势
                    calendar.add(Calendar.MONTH, 1)
                }
            }
        }

        return result
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ExerciseApplication)
                AnalysisViewModel(application.repository)
            }
        }
    }
}
