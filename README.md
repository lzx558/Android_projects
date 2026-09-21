# 移动软件开发 —— 实验2：Android 界面布局

Android 学习项目，涵盖传统 XML 布局（LinearLayout / TableLayout / ConstraintLayout）与 Jetpack Compose。

## 技术栈

| 项目 | 版本 |
|------|------|
| Android Gradle Plugin | 9.3.0 |
| Kotlin | 2.2.10 |
| Compose BOM | 2026.02.01 |
| Compose Material3 | — |
| AndroidX ConstraintLayout | 2.2.1 |
| AndroidX AppCompat | 1.7.0 |
| Material Components | 1.12.0 |
| compileSdk / targetSdk | 37 |
| minSdk | 24 |

## 项目结构

```
Android_test1/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/example/android_test1/
│   │   ├── MainActivity.kt              # 入口菜单（Compose LazyColumn）
│   │   ├── LinearLayoutActivity.kt      # 实验1：线性布局
│   │   ├── TableLayoutActivity.kt       # 实验2：表格布局
│   │   ├── CalculatorActivity.kt        # 实验3：约束布局（一）计算器
│   │   ├── SpaceTravelActivity.kt        # 实验4：约束布局（二）太空旅行
│   │   ├── ComposeTaskActivity.kt        # 实验5：Compose 任务列表
│   │   └── ui/theme/                    # Compose 主题
│   └── res/
│       ├── layout/                      # 4 个 XML 布局文件
│       ├── drawable/                    # 背景、图标、矢量图
│       ├── values/                      # colors / strings / themes
│       └── mipmap-*/                    # 应用图标
├── gradle/libs.versions.toml            # 版本目录
└── gradle.properties
```

## 5 个实验

打开应用后进入**实验菜单**，点击任意卡片跳转到对应界面：

### 实验 1：线性布局 LinearLayout

- 文件：[LinearLayoutActivity.kt](app/src/main/java/com/example/android_test1/LinearLayoutActivity.kt)、[activity_linear_layout.xml](app/src/main/res/layout/activity_linear_layout.xml)
- 4 行 × 4 列按钮网格
- 外层垂直 LinearLayout + 内层水平 LinearLayout
- 通过 `layout_width="0dp"` + `layout_weight="1"` 实现等宽等高
- 点击按钮弹出 Toast 显示文字

### 实验 2：表格布局 TableLayout

- 文件：[TableLayoutActivity.kt](app/src/main/java/com/example/android_test1/TableLayoutActivity.kt)、[activity_table_layout.xml](app/src/main/res/layout/activity_table_layout.xml)
- 深色菜单界面（Open / Save / New / Project / Quit）
- TableLayout + TableRow 实现行分割线与对齐

### 实验 3：约束布局（一）计算器

- 文件：[CalculatorActivity.kt](app/src/main/java/com/example/android_test1/CalculatorActivity.kt)、[activity_calculator.xml](app/src/main/res/layout/activity_calculator.xml)
- ConstraintLayout Chain 约束实现 4×5 按键网格
- 绿色等号主按键、深色显示区

### 实验 4：约束布局（二）太空旅行订票

- 文件：[SpaceTravelActivity.kt](app/src/main/java/com/example/android_test1/SpaceTravelActivity.kt)、[activity_space_travel.xml](app/src/main/res/layout/activity_space_travel.xml)
- 顶部三 Tab（Space Stations / Flights / Rovers）使用水平 spread chain 等分
- DCA ⇄ MARS 航线框使用 packed chain 居中
- One Way 开关：勾选显示 `single_arrow`，取消显示 `double_arrows`
- 中心轨道插图、底部 DEPART 绿色按钮
- 图片资源来自 `实验2图片/` 文件夹，已放入 `res/drawable/`：
  - `space_station_icon.png`、`rocket_icon.png`、`rover_icon.png`（Tab 图标）
  - `double_arrows.png`、`single_arrow.png`（交换箭头）
  - `galaxy.png`（轨道示意图）

### 实验 5：Android Compose 任务列表

- 文件：[ComposeTaskActivity.kt](app/src/main/java/com/example/android_test1/ComposeTaskActivity.kt)
- 纯 Jetpack Compose 实现
- LazyColumn 列表 + 勾选删除线 + 完成数统计同步
- 添加 "复习 LazyColumn" 任务后状态实时更新

## 运行方式

### 环境要求

- Android Studio 最新版（推荐 Ladybug 或以上）
- JDK 21（AGP 9.3.0 要求）
- Android SDK 37 + Android 14 系统镜像（用于模拟器）
- Windows 需启用 Hyper-V / Hypervisor 以支持模拟器

### 步骤

1. Android Studio 打开项目根目录
2. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
   → 将 Gradle JDK 设置为本地 JDK 21 路径（如 `E:\jdk21`）
3. 等待 Gradle 同步完成
4. 连接真机或启动模拟器（Pixel 7 + Android 14/15）
5. 点击 ▶ Run 运行

### JDK 配置说明

如果 Gradle 提示 `jlink executable` 错误，说明 IDE 使用了错误的 JDK 路径。可在项目根目录 `gradle.properties` 中强制指定：

```properties
org.gradle.java.home=E:\\jdk21
org.gradle.java.installations.paths=E:\\jdk21
```

同时在 `.idea/gradle.xml` 中设置 `<option name="gradleJvm" value="E:\\jdk21" />`。
