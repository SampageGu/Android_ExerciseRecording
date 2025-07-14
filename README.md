# 💪 Exercise Tracker - Android健身训练记录应用

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Room](https://img.shields.io/badge/Room-4285F4?style=for-the-badge&logo=android&logoColor=white)

*一款现代化的Android健身训练记录应用，帮助您追踪和管理您的健身旅程*

</div>

## 📱 应用简介

Exercise Tracker 是一款使用最新Android技术栈开发的健身记录应用，专为健身爱好者设计。应用采用Material Design 3设计语言，提供直观美观的用户界面和流畅的用户体验。

## ✨ 主要功能

### 🎯 核心功能
- **动作库管理**：内置15种常见训练动作，支持自定义添加
- **智能分类**：按肌群分类（胸部、背部、腿部、肩部、手臂、核心）
- **训练记录**：快速记录训练重量和次数
- **个人记录追踪**：自动检测并标记个人最佳成绩
- **历史数据**：完整的训练历史记录和数据分析

### 🏋️ 动作类型支持
- **器械训练**：健身房器械类动作（15-100kg，1kg递增）
- **自由重量**：杠铃哑铃类动作（2.5kg起，2.5kg递增）
- **自重训练**：无器械训练动作

### 🖼️ 个性化功能
- **自定义封面**：为每个动作添加个人图片封面
- **智能默认值**：记住上次训练数据作为默认值
- **直观图表**：训练数据可视化展示

## 🛠️ 技术架构

### 核心技术栈
- **开发语言**：Kotlin 100%
- **UI框架**：Jetpack Compose - 现代声明式UI
- **架构模式**：MVVM + Repository Pattern
- **数据库**：Room - 本地数据持久化
- **导航**：Navigation Compose
- **图片加载**：Coil - 异步图片加载
- **依赖注入**：Hilt/Manual DI

### 项目架构
```
app/
├── src/main/java/com/example/exercise/
│   ├── data/                    # 数据层
│   │   ├── dao/                # 数据访问对象
│   │   ├── database/           # 数据库配置
│   │   ├── model/              # 数据模型
│   │   ├── repository/         # 数据仓库
│   │   └── util/               # 数据工具类
│   ├── ui/                     # UI层
│   │   ├── components/         # 可复用组件
│   │   ├── navigation/         # 导航配置
│   │   ├── screens/            # 页面组件
│   │   ├── theme/              # 主题配置
│   │   └── viewmodel/          # 视图模型
│   └── utils/                  # 工具类
└── src/main/res/               # 资源文件
```

## 📊 数据模型设计

### 核心实体
- **Exercise**：训练动作信息
- **TrainingSession**：训练会话记录
- **ExerciseSet**：具体训练组数据
- **PersonalRecord**：个人最佳记录

### 数据关系
```
TrainingSession (1) ─── (N) ExerciseSet
Exercise (1) ─── (N) ExerciseSet
Exercise (1) ─── (N) PersonalRecord
```

## 🎨 UI/UX特性

### Material Design 3
- **动态主题**：支持Material You动态颜色
- **流畅动画**：页面切换动画和状态转换
- **响应式设计**：适配不同屏幕尺寸

### 交互设计
- **滑动动画**：页面间流畅的滑动切换效果
- **长按操作**：长按动作卡片显示编辑/删除菜单
- **智能表单**：根据动作类型自动调整输入项

## 🚀 快速开始

### 环境要求
- Android Studio Arctic Fox 或更高版本
- Android SDK 24+ (Android 7.0)
- Kotlin 1.8+
- Gradle 8.0+

### 安装步骤

1. **克隆仓库**
```bash
git clone https://github.com/yourusername/exercise-tracker.git
cd exercise-tracker
```

2. **打开项目**
```bash
# 使用Android Studio打开项目
# 或使用命令行
./gradlew build
```

3. **编译运行**
```bash
# 编译Kotlin代码
./gradlew compileDebugKotlin

# 构建APK
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug
```

## 📋 功能使用指南

### 添加新动作
1. 点击动作库页面右上角的"+"按钮
2. 填写动作信息（名称、肌群、类型等）
3. 可选择添加动作封面图片
4. 点击"添加"保存

### 记录训练
1. 在动作库中点击要训练的动作
2. 输入重量和次数（会显示上次训练数据作为参考）
3. 点击确认记录

### 查看历史
1. 切换到"历史"页面
2. 查看按日期排列的训练记录
3. 点击具体记录查看详细数据

### 数据分析
1. 进入"分析"页面
2. 查看训练进度图表
3. 查看个人记录统计







## 🤝 贡献指南

我们欢迎各种形式的贡献！

### 如何贡献
1. Fork 这个仓库
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的修改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建一个 Pull Request

### 代码规范
- 遵循Kotlin编码规范
- 使用有意义的变量和函数命名
- 添加适当的注释
- 确保代码通过所有测试

## 🐛 问题反馈

如果您遇到任何问题或有改进建议，请：

1. 查看[现有issues](https://github.com/yourusername/exercise-tracker/issues)
2. 如果问题不存在，请[创建新issue](https://github.com/yourusername/exercise-tracker/issues/new)
3. 提供详细的问题描述和重现步骤

## 📄 开源协议

本项目采用 MIT 协议 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🙏 致谢

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - 现代Android UI工具包
- [Room](https://developer.android.com/training/data-storage/room) - 数据库抽象层
- [Coil](https://coil-kt.github.io/coil/) - 图片加载库
- [Material Design](https://material.io/) - 设计系统

## 📱 下载体验

### 最新版本
- **版本**: v1.0.0
- **更新时间**: 2025年7月14日
- **支持系统**: Android 7.0+

### 下载方式
- [GitHub Releases](https://github.com/yourusername/exercise-tracker/releases)
- [Google Play Store](https://play.google.com/store/apps/details?id=com.example.exercise) (即将上线)



---

<div align="center">

**如果这个项目对您有帮助，请给个⭐️支持一下！**

Made with ❤️ for fitness enthusiasts

</div>
