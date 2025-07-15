# 健身训练记录应用 (Exercise Tracker)

一个功能强大的Android健身训练记录应用，帮助你跟踪训练进度，管理健身计划，分析训练数据。

## ✨ 主要功能

### 🏋️ 训练动作管理
- **多类型动作支持**：器械训练、自由重量、自重训练
- **肌群分类**：胸部、背部、腿部、肩部、手臂、核心
- **动作自定义**：添加自定义动作，支持图片上传
- **默认参数设置**：为每个动作设置默认重量和次数

### 📊 训练记录
- **便捷记录**：快速记录每组训练的重量和次数
- **历史数据**：查看上次训练记录作为参考
- **个人记录追踪**：自动识别并标记个人最佳成绩
- **训练会话管理**：按日期组织训练记录

### 📈 数据分析
- **进度曲线图**：可视化展示训练进步趋势
- **多时间范围**：支持3天、1周、1个月等多种时间范围分析
- **训练容量统计**：追踪总训练量变化
- **详细统计数据**：全面的训练数据分析

### 📱 用户体验
- **现代化UI设计**：采用Material Design 3设计语言
- **流畅交互**：长按编辑、滑动操作等直观交互
- **响应式布局**：适配不同屏幕尺寸
- **暗色模式支持**：护眼的夜间模式

## 🚀 技术特性

### 架构设计
- **MVVM架构**：Model-View-ViewModel架构模式
- **Repository模式**：统一的数据访问层
- **依赖注入**：使用Dagger Hilt进行依赖管理

### 技术栈
- **开发语言**：Kotlin 100%
- **UI框架**：Jetpack Compose
- **数据库**：Room数据库
- **异步处理**：Kotlin Coroutines + Flow
- **图片处理**：Coil图片加载库
- **导航**：Navigation Compose

### 数据管理
- **本地存储**：Room数据库持久化存储
- **响应式数据**：Flow + StateFlow响应式编程
- **数据缓存**：智能缓存机制提升性能
- **数据备份**：支持训练数据导出

## 📱 界面预览

### 主要界面
- **训练界面**：选择动作，记录训练数据
- **动作管理**：浏览、添加、编辑训练动作
- **历史记录**：查看历史训练记录
- **数据分析**：可视化训练进度分析

### 交互设计
- **长按操作**：长按动作卡片进行编辑或删除
- **滑动选择**：流畅的滑动选择器
- **智能输入**：基于历史记录的智能默认值
- **即时反馈**：实时的视觉反馈和动画效果

## 🛠️ 安装说明

### 系统要求
- Android 7.0 (API level 24) 或更高版本
- 至少 50MB 存储空间

### 从源码构建
1. **克隆仓库**
   ```bash
   git clone https://github.com/yourusername/exercise-tracker.git
   cd exercise-tracker
   ```

2. **打开项目**
   - 使用 Android Studio 打开项目
   - 等待 Gradle 同步完成

3. **运行应用**
   ```bash
   ./gradlew assembleDebug
   # 或者直接在 Android Studio 中点击运行按钮
   ```

### 发布版本安装
1. 下载最新的 APK 文件
2. 在Android设备上启用"未知来源"安装
3. 安装APK文件

## 📖 使用指南

### 快速开始
1. **添加第一个动作**
   - 点击"+"按钮添加新动作
   - 选择动作类型（器械/自由重量/自重）
   - 设置目标肌群和默认参数

2. **记录训练**
   - 在训练界面选择要练习的动作
   - 输入重量和次数
   - 系统会自动保存记录

3. **查看进度**
   - 在分析界面选择想要分析的动作
   - 选择时间范围查看进步趋势
   - 查看个人记录和统计数据

### 高级功能
- **自定义动作**：添加自己的训练动作并上传图片
- **数据分析**：使用多种图表分析训练效果
- **个人记录**：自动追踪并标记个人最佳成绩
- **训练计划**：制定并跟踪个性化训练计划

## 🗂️ 项目结构

```
app/
├── src/main/java/com/example/exercise/
│   ├── data/                    # 数据层
│   │   ├── dao/                # 数据访问对象
│   │   ├── database/           # 数据库配置
│   │   ├── model/              # 数据模型
│   │   └── repository/         # 数据仓库
│   ├── ui/                     # UI层
│   │   ├── components/         # 可复用组件
│   │   ├── navigation/         # 导航配置
│   │   ├── screens/            # 各个界面
│   │   └── theme/              # 主题配置
│   ├── utils/                  # 工具类
│   └── ExerciseApplication.kt  # 应用入口
└── src/main/res/               # 资源文件
```

## 🔧 开发配置

### 环境要求
- Android Studio Giraffe | 2022.3.1 或更高版本
- Kotlin 1.9.0 或更高版本
- Gradle 8.0 或更高版本
- Java 17 或更高版本


## 🤝 贡献指南

我们欢迎所有形式的贡献！

### 如何贡献
1. Fork 这个项目
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

### 报告问题
如果你发现了bug或有功能建议，请：
1. 检查是否已有相关的issue
2. 创建新的issue并详细描述问题
3. 包含复现步骤和设备信息

## 📝 更新日志

### v1.0.0 (2025-01-15)
- ✨ 初始版本发布
- 🏋️ 基础训练记录功能
- 📊 数据分析和可视化
- 📱 现代化UI设计
- 💾 本地数据存储

### 计划中的功能
- [ ] 云端数据同步
- [ ] 训练计划模板
- [ ] 社交功能
- [ ] 健身教程集成
- [ ] 可穿戴设备支持

## 📄 许可证



## 📞 联系方式

- **项目维护者**：[你的名字]
- **邮箱**：your.email@example.com
- **项目主页**：https://github.com/yourusername/exercise-tracker

## 🙏 致谢

感谢以下开源项目的支持：
- [Android Jetpack](https://developer.android.com/jetpack)
- [Material Design](https://material.io/)
- [Kotlin](https://kotlinlang.org/)
- [Room Database](https://developer.android.com/jetpack/androidx/releases/room)
- [Coil](https://coil-kt.github.io/coil/)

---

如果这个项目对你有帮助，请考虑给它一个 ⭐ Star！
