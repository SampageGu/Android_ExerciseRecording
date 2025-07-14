package com.example.exercise.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.exercise.R
import com.example.exercise.ui.screens.analysis.AnalysisScreen
import com.example.exercise.ui.screens.exercises.ExercisesScreen
import com.example.exercise.ui.screens.history.HistoryScreen
import com.example.exercise.ui.screens.training.TrainingScreen

/**
 * 定义应用程序的主要导航目标
 */
sealed class Screen(
    val route: String,
    val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Exercises : Screen(
        "exercises",
        R.string.exercises,
        Icons.Filled.Home,
        Icons.Outlined.Home
    )

    object Training : Screen(
        "training",
        R.string.training,
        Icons.Filled.Favorite,
        Icons.Outlined.Favorite
    )

    object History : Screen(
        "history",
        R.string.history,
        Icons.Filled.DateRange,
        Icons.Outlined.DateRange
    )

    object Analysis : Screen(
        "analysis",
        R.string.analysis,
        Icons.Filled.List,
        Icons.Outlined.List
    )
}

/**
 * 应用程序的底部导航栏
 */
@Composable
fun AppBottomNavigation(navController: NavHostController) {
    val items = listOf(
        Screen.Exercises,
        Screen.Training,
        Screen.History,
        Screen.Analysis
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(screen.titleResId)) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        // 弹出到导航图的起始目标，避免构建大量目标堆栈
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // 避免同一目标的多个副本
                        launchSingleTop = true
                        // 恢复状态
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * 应用程序主导航宿主
 */
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Exercises.route,
        modifier = modifier
    ) {
        // 动作库页面
        composable(
            route = Screen.Exercises.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Training.route -> {
                        // 从训练页面返回：从左侧滑入
                        slideInHorizontally(
                            initialOffsetX = { -it / 3 },
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(350))
                    }
                    else -> {
                        // 默认动画：淡入
                        fadeIn(animationSpec = tween(300))
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Training.route -> {
                        // 前往训练页面：向左滑出
                        slideOutHorizontally(
                            targetOffsetX = { -it / 3 },
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                    else -> {
                        // 默认动画：淡出
                        fadeOut(animationSpec = tween(250))
                    }
                }
            }
        ) {
            ExercisesScreen(navController)
        }

        // 训练页面
        composable(
            route = Screen.Training.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Exercises.route -> {
                        // 从动作库进入：从右侧滑入
                        slideInHorizontally(
                            initialOffsetX = { it / 3 },
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(350))
                    }
                    Screen.History.route -> {
                        // 从历史页面返回：从左侧滑入
                        slideInHorizontally(
                            initialOffsetX = { -it / 3 },
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(350))
                    }
                    else -> {
                        fadeIn(animationSpec = tween(300))
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Exercises.route -> {
                        // 返回动作库：向右滑出
                        slideOutHorizontally(
                            targetOffsetX = { it / 3 },
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                    Screen.History.route -> {
                        // 前往历史页面：向左滑出
                        slideOutHorizontally(
                            targetOffsetX = { -it / 3 },
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                    else -> {
                        fadeOut(animationSpec = tween(250))
                    }
                }
            }
        ) {
            TrainingScreen()
        }

        // 历史页面
        composable(
            route = Screen.History.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Training.route -> {
                        // 从训练页面进入：从右侧滑入
                        slideInHorizontally(
                            initialOffsetX = { it / 3 },
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(350))
                    }
                    Screen.Analysis.route -> {
                        // 从分析页面返回：从左侧滑入
                        slideInHorizontally(
                            initialOffsetX = { -it / 3 },
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(350))
                    }
                    else -> {
                        fadeIn(animationSpec = tween(300))
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Training.route -> {
                        // 返回训练页面：向右滑出
                        slideOutHorizontally(
                            targetOffsetX = { it / 3 },
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                    Screen.Analysis.route -> {
                        // 前往分析页面：向左滑出
                        slideOutHorizontally(
                            targetOffsetX = { -it / 3 },
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                    else -> {
                        fadeOut(animationSpec = tween(250))
                    }
                }
            }
        ) {
            HistoryScreen(navController)
        }

        // 分析页面
        composable(
            route = Screen.Analysis.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.History.route -> {
                        // 从历史页面进入：从右侧滑入
                        slideInHorizontally(
                            initialOffsetX = { it / 3 },
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(350))
                    }
                    else -> {
                        fadeIn(animationSpec = tween(300))
                    }
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.History.route -> {
                        // 返回历史页面：向右滑出
                        slideOutHorizontally(
                            targetOffsetX = { it / 3 },
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                    else -> {
                        fadeOut(animationSpec = tween(250))
                    }
                }
            }
        ) {
            AnalysisScreen(navController)
        }
    }
}
