package com.example.exercise.ui.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exercise.R
import kotlinx.coroutines.delay

/**
 * 启动画面组件
 * 显示应用Logo、随机励志文案和加载动画
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val currentQuote = remember { MotivationalQuotes.getRandomQuote() }

    // 渐变背景色
    val gradientColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondary
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(3000) // 显示3秒
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(1000)
            ) + slideInVertically(
                animationSpec = tween(1000),
                initialOffsetY = { it / 2 }
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                // 应用图标区域（可以放置您的Logo）
                Card(
                    modifier = Modifier.size(120.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💪",
                            fontSize = 60.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 应用名称
                Text(
                    text = "健身助手",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // 励志文案卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentQuote.text,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "— ${currentQuote.author}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 加载动画
                LoadingAnimation()
            }
        }
    }
}

/**
 * 加载动画组件
 */
@Composable
private fun LoadingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "正在加载",
            color = Color.White.copy(alpha = alpha),
            fontSize = 16.sp
        )

        repeat(3) { index ->
            val delay = index * 200
            val dotAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = delay),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot$index"
            )

            Text(
                text = "●",
                color = Color.White.copy(alpha = dotAlpha),
                fontSize = 12.sp,
                modifier = Modifier.alpha(dotAlpha)
            )
        }
    }
}

/**
 * 励志文案数据类
 */
data class MotivationalQuote(
    val text: String,
    val author: String
)

/**
 * 励志文案管理器
 * 包含丰富的健身励志语录
 */
object MotivationalQuotes {
    private val quotes = listOf(
        MotivationalQuote("坚持就是胜利，每一次训练都是向目标迈进的一步", "健身格言"),
        MotivationalQuote("你的身体能够承受住，问题是你的意志能否承受", "阿诺德·施瓦辛格"),
        MotivationalQuote("今天的痛苦，就是明天的力量", "健身哲学"),
        MotivationalQuote("不要等待机会，而要创造机会", "励志名言"),
        MotivationalQuote("强壮的人不是征服别人的人，而是能征服自己弱点的人", "健身智慧"),
        MotivationalQuote("每一滴汗水，都是对未来更好自己的投资", "健身感悟"),
        MotivationalQuote("身体是革命的本钱，健康是最大的财富", "生活哲理"),
        MotivationalQuote("挑战自己，超越极限，遇见更强大的自己", "自我激励"),
        MotivationalQuote("肌肉不会说谎，努力终将得到回报", "健身真理"),
        MotivationalQuote("今天不走，明天就要跑", "健身警言"),
        MotivationalQuote("健身是世界上最公平的事，你付出多少，就收获多少", "公平法则"),
        MotivationalQuote("困难只是成长路上的垫脚石", "成长智慧"),
        MotivationalQuote("你的唯一限制就是你为自己设置的限制", "突破自我"),
        MotivationalQuote("强者不是没有眼泪，而是含着眼泪继续奔跑", "坚强意志"),
        MotivationalQuote("每天进步一点点，一年后你会感谢现在努力的自己", "持续进步"),
        MotivationalQuote("汗水是脂肪的眼泪", "减脂励志"),
        MotivationalQuote("如果你想要普通的结果，就做普通的事；如果你想要卓越的结果，就要做卓越的事", "卓越追求"),
        MotivationalQuote("健身不仅改变身体，更改变心态", "身心健康"),
        MotivationalQuote("当你觉得为时已晚的时候，恰恰是最早的时候", "永不太晚"),
        MotivationalQuote("你今天的努力，是幸运的伏笔", "努力与幸运"),
        MotivationalQuote("不是因为有了希望才坚持，而是因为坚持才看到了希望", "坚持的力量"),
        MotivationalQuote("改变从第一步开始，成功从坚持开始", "开始与坚持"),
        MotivationalQuote("你的身材，暴露了你的生活态度", "生活态度"),
        MotivationalQuote("健身路上没有捷径，只有坚持", "健身真谛"),
        MotivationalQuote("每一次举重，都是对重力的挑战", "挑战重力"),
        MotivationalQuote("优秀是一种习惯，健身让习惯变得优秀", "优秀习惯"),
        MotivationalQuote("你可以被打败，但永远不要被打倒", "不屈精神"),
        MotivationalQuote("健身是一场与自己的对话", "内心对话"),
        MotivationalQuote("每一个强壮的今天，都来自于昨天的坚持", "积累的力量"),
        MotivationalQuote("相信自己，你比想象中更强大", "自信力量"),
        MotivationalQuote("健身不是惩罚身体，而是奖励身体", "正确心态"),
        MotivationalQuote("痛苦是暂时的，但放弃是永久的", "坚持到底"),
        MotivationalQuote("你的身体就是你的神庙，保持它的神圣", "身体敬畏"),
        MotivationalQuote("健康的身体是灵魂的客厅，病弱的身体是灵魂的监狱", "身体与灵魂"),
        MotivationalQuote("运动是治愈一切的良药", "运动治愈"),
        MotivationalQuote("汗水不会背叛努力的人", "汗水见证"),
        MotivationalQuote("今天的你，感谢昨天坚持的自己", "感谢坚持"),
        MotivationalQuote("健身让我们学会坚持，坚持让我们变得更强", "相互成就"),
        MotivationalQuote("每一次训练，都是对平庸生活的反抗", "反抗平庸"),
        MotivationalQuote("当你想要放弃的时候，想想当初为什么开始", "初心不忘")
    )

    /**
     * 获取随机励志文案
     */
    fun getRandomQuote(): MotivationalQuote {
        return quotes.random()
    }

    /**
     * 根据日期获取今日文案（确保同一天获取的是相同文案）
     */
    fun getTodayQuote(): MotivationalQuote {
        val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000) // 获取天数
        val index = (today % quotes.size).toInt()
        return quotes[index]
    }
}
