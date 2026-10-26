# 移动软件开发 —— Android 实验工程

一个 Android Studio 工程，按**实验**分包归档所有实验界面，从首页菜单可直接跳转。

| 实验 | 主题 | 包路径 |
|------|------|--------|
| 实验二 | Android 界面布局（LinearLayout / TableLayout / ConstraintLayout / Compose） | `com.example.android_test1.lab2` |
| 实验三 | 事件处理与列表（ListView / AlertDialog / 菜单 / ActionMode / RecyclerView） | `com.example.android_test1.lab3` |
| 期中考试 | NotePad 记事本应用（ContentProvider + SQLite，Java） | `com.example.android_test1.midterm` |

## 技术栈

| 项目 | 版本 |
|------|------|
| Android Gradle Plugin | 9.3.0 |
| Kotlin | 2.2.10 |
| Compose BOM | 2026.02.01 |
| AndroidX ConstraintLayout | 2.2.1 |
| AndroidX AppCompat | 1.7.0 |
| AndroidX RecyclerView | — |
| Material Components | 1.12.0 |
| compileSdk / targetSdk | 37 |
| minSdk | 24 |

## 项目结构

```
Android_test1/
├── app/src/main/
│   ├── AndroidManifest.xml                      # 按实验分组登记所有 Activity
│   ├── java/com/example/android_test1/
│   │   ├── MainActivity.kt                      # 入口菜单（按实验分组）
│   │   ├── ui/theme/                            # Compose 主题（共用）
│   │   ├── lab2/                                # ===== 实验二：界面布局 =====
│   │   │   ├── LinearLayoutActivity.kt          # 01 线性布局
│   │   │   ├── TableLayoutActivity.kt           # 02 表格布局
│   │   │   ├── CalculatorActivity.kt            # 03 约束布局（一）计算器
│   │   │   ├── SpaceTravelActivity.kt           # 04 约束布局（二）太空旅行
│   │   │   └── ComposeTaskActivity.kt           # 05 Compose 任务列表
│   │   └── lab3/                                # ===== 实验三：事件与列表 =====
│   │       ├── ListViewActivity.kt              # 01 ListView + SimpleAdapter
│   │       ├── AlertDialogActivity.kt           # 02 自定义 AlertDialog
│   │       ├── MenuTestActivity.kt              # 03 XML 菜单
│   │       ├── ActionModeActivity.kt            # 04 ActionMode 上下文菜单
│   │       ├── RecyclerViewActivity.kt          # 05 RecyclerView
│   │       └── AnimalAdapter.kt                 #    实验三 · 05 的数据与适配器
│   │   └── midterm/                             # ===== 期中考试：NotePad 记事本（Java）=====
│   │       ├── NotePad.java                     # 数据契约（表名 / URI / 列名）
│   │       ├── NotePadProvider.java             # ContentProvider + SQLite
│   │       ├── NotesList.java                   # 01 列表：时间戳 / 搜索 / 分类 / 置顶
│   │       ├── NotesCursorAdapter.java          #    列表条目绑定
│   │       ├── NoteEditor.java                  # 02 编辑：分类 / 导出 / 置顶
│   │       ├── TitleEditor.java                 # 03 修改标题
│   │       ├── NoteSettings.java                # 04 偏好设置
│   │       ├── Prefs.java                       #    SharedPreferences 封装
│   │       └── MidtermActivity.java             #    页面基类（控制深色模式生效时机）
│   └── res/
│       ├── layout/    lab2_* / lab3_* / midterm_*   # 布局按模块加前缀
│       ├── drawable/  lab2_* / lab3_* / midterm_*   # 图片、背景、图标同前缀
│       ├── menu/      lab3_* / midterm_*            # 菜单资源
│       ├── values/                                  # colors / strings / themes（共用）
│       ├── values-night/                            # 期中考试深色模式颜色
│       └── mipmap-*/                                # 应用图标
├── gradle/libs.versions.toml
└── gradle.properties
```

### 命名约定

新增文件一律带模块前缀，避免不同模块之间重名：

| 类型 | 规则 | 例子 |
|------|------|------|
| 实验 N 的类 | 放在 `labN` 包内 | `lab3/RecyclerViewActivity.kt` |
| 期中考试的类 | 放在 `midterm` 包内 | `midterm/NotesList.java` |
| 布局 | 前缀 + `_xxx.xml` | `lab3_list_view.xml`、`midterm_notes_list.xml` |
| 图片 / 背景 | 前缀 + `_xxx` | `lab2_galaxy.png`、`midterm_ic_pin.xml` |
| 菜单 | 前缀 + `_xxx.xml` | `lab3_menu_test.xml`、`midterm_list_options.xml` |
| 颜色 | 前缀 + `_xxx` | `lab2_calc_primary`、`midterm_primary` |
| 资源 id | 保持短名（如 `tv_name`），R.id 全局共享 | `<TextView android:id="@+id/tv_name">` |

> 前缀说明：实验按序号用 `labN`（`lab2`、`lab3`…）；期中考试不是实验序号，单独用 `midterm`，
> 这样以后再加实验四仍然可以用 `lab4` 这个包名和前缀，不会撞车。

## 实验二：Android 界面布局

打开应用后进入**实验总菜单 → 实验二**分组，点击卡片跳转。

### 01 线性布局 LinearLayout

- 文件：`lab2/LinearLayoutActivity.kt`、`res/layout/lab2_linear_layout.xml`
- 4 行 × 4 列按钮网格
- 外层垂直 LinearLayout + 内层水平 LinearLayout
- `layout_width="0dp"` + `layout_weight="1"` 实现等宽等高
- 点击按钮 Toast 显示文字

### 02 表格布局 TableLayout

- 文件：`lab2/TableLayoutActivity.kt`、`res/layout/lab2_table_layout.xml`
- 深色菜单界面（Open / Save / New / Project / Quit）
- TableLayout + TableRow 实现行分割线与对齐

### 03 约束布局（一）计算器

- 文件：`lab2/CalculatorActivity.kt`、`res/layout/lab2_calculator.xml`
- ConstraintLayout Chain 约束实现 4×5 按键网格
- 绿色等号主按键、深色显示区

### 04 约束布局（二）太空旅行订票

- 文件：`lab2/SpaceTravelActivity.kt`、`res/layout/lab2_space_travel.xml`
- 顶部三 Tab（Space Stations / Flights / Rovers）水平 spread chain 等分
- DCA ⇄ MARS 航线框用 packed chain 居中
- One Way 开关：勾选显示 `lab2_single_arrow`，取消显示 `lab2_double_arrows`
- 素材来源 `实验2图片/`：`lab2_space_station_icon`、`lab2_rocket_icon`、`lab2_rover_icon`、`lab2_galaxy`

### 05 Android Compose 任务列表

- 文件：`lab2/ComposeTaskActivity.kt`（纯 Compose，无 XML 布局）
- LazyColumn 列表 + 勾选删除线 + 完成数统计同步
- 添加「复习 LazyColumn」后状态实时更新

## 实验三：事件处理与列表

### 01 ListView + SimpleAdapter

- 文件：`lab3/ListViewActivity.kt`、`res/layout/lab3_list_view.xml`、`res/layout/lab3_list_item_animal.xml`
- SimpleAdapter 绑定「动物名 + 图标」
- 点击条目 Toast 名称，并发送一条通知（Android 8+ 渠道）

### 02 自定义 AlertDialog

- 文件：`lab3/AlertDialogActivity.kt`、`res/layout/lab3_alert_dialog.xml`、`res/layout/lab3_dialog_custom.xml`
- 橙色标题栏 + 用户名/密码输入
- Cancel 关闭；Sign in 提示用户名；密码框回车等同登录

### 03 XML 定义菜单

- 文件：`lab3/MenuTestActivity.kt`、`res/layout/lab3_menu_test.xml`、`res/menu/lab3_menu_test.xml`
- 字体大小子菜单（小/中/大）、普通菜单项、字体颜色子菜单（红/黑）

### 04 ActionMode 上下文菜单

- 文件：`lab3/ActionModeActivity.kt`、`res/layout/lab3_action_mode.xml`、`res/menu/lab3_menu_action_mode.xml`
- 长按进入多选模式，标题显示已选数量
- 删除时按索引倒序移除，避免错位

### 05 RecyclerView

- 文件：`lab3/RecyclerViewActivity.kt`、`lab3/AnimalAdapter.kt`、`res/layout/lab3_recycler_view.xml`、`res/layout/lab3_item_recycler_animal.xml`
- Adapter + ViewHolder 标准写法
- 点击条目 Toast 名称；点「增加一条记录」追加并滚动到末位

## 期中考试：NotePad 记事本应用

基于 Android 官方 [NotePad](https://github.com/fjnu-cse/NotePad) 示例扩展，沿用它的
`ContentProvider + SQLite` 架构，用 Java 实现（与原示例一致）。

> **移植说明**：原示例使用的 `ListActivity` 和 `managedQuery()` 在 Android 11（API 30）
> 已被移除，照抄无法编译。这里改用 `AppCompatActivity` + `ContentResolver.query()`，
> 其余设计（数据契约、UriMatcher、CursorAdapter、三个 Activity 的分工）保持原样。

### 基本要求 1：笔记条目显示时间戳

- 数据来源：`NotePadProvider` 在 insert / update 时自动写入 `created` 与 `modified`
- 显示位置：`NotesCursorAdapter.bindView()`，格式如 `修改于 2026-10-26 14:30`
- 从未修改过的笔记回退显示创建时间

### 基本要求 2：按标题或内容查询

- `NotesList.refresh()`，顶部搜索框输入即实时过滤，无需点按钮
- 条件：`(title LIKE ? OR note LIKE ?)`，与分类条件用 `AND` 组合
- 无结果时列表区显示「没有找到匹配的笔记」

### 附加功能 1：笔记分类

- 表结构新增 `category` 列
- 编辑页下拉选择：工作 / 学习 / 生活 / 灵感 / 其他
- 列表页顶部下拉按分类筛选，条目右侧显示分类标签
- 新建笔记的默认分类可在设置里指定

### 附加功能 2：偏好设置

- `Prefs.java` 封装 SharedPreferences，`NoteSettings` 提供设置界面
- **排序方式**：修改时间 / 创建时间 / 标题（置顶笔记永远排在最前）
- **正文字号**：12–24sp，作用于编辑页正文
- **深色模式**：`AppCompatDelegate.setLocalNightMode()`，只影响记事本界面，
  不会把实验二、实验三的界面也变黑（配套 `values-night/midterm_colors.xml`）
- **默认分类** + 一键恢复默认设置

### 附加功能 3：导出笔记

- 编辑页菜单 →「导出」
- 生成 txt（标题 / 分类 / 导出时间 / 正文）保存到
  `Android/data/com.example.android_test1/files/exports/`
- 通过 FileProvider + `ACTION_SEND` 调起系统分享面板

### 附加功能 4：置顶收藏

- 表结构新增 `pinned` 列
- 列表长按 →「置顶 / 取消置顶」；编辑页菜单同样可切换
- 排序 SQL 首列为 `pinned DESC`，置顶笔记恒在列表顶部
- 条目左侧显示图钉标记

### 附加功能与代码对照

| 功能 | 主要文件 |
|------|----------|
| 数据契约 / 表结构 | `midterm/NotePad.java` |
| SQLite + ContentProvider | `midterm/NotePadProvider.java` |
| 时间戳 + 搜索 + 分类筛选 | `midterm/NotesList.java`、`midterm/NotesCursorAdapter.java` |
| 分类选择 / 导出 / 置顶 / 改标题 | `midterm/NoteEditor.java`、`midterm/TitleEditor.java` |
| 偏好设置 | `midterm/NoteSettings.java`、`midterm/Prefs.java` |
| 深色模式生效时机 | `midterm/MidtermActivity.java`（4 个页面的基类） |

> 深色模式的实现要点：在 `MidtermActivity.attachBaseContext()` 里设置 `setLocalNightMode`，
> 这是唯一能让新建页面一打开就是正确配色的时机（放在 onCreate 里对已 attach 的页面太晚，
> 会出现「从列表进编辑页，编辑页还是浅色」）；`onResume` 里再校验一次，用于同步返回栈里
> 已存在的页面。用 localNightMode 而不是全局 `setDefaultNightMode`，保证首页菜单和
> 实验二、实验三不会被一起改成深色。

### 建议的验证步骤

1. 首页 → 期中考试 → 笔记列表，点右上角「+」新建一条笔记，填标题和正文，选分类，保存
2. 回到列表，确认条目下方显示时间戳、右侧显示分类
3. 在搜索框输入正文里的某个词，确认能搜到（按内容查询）
4. 输入标题里的词，确认也能搜到（按标题查询）
5. 顶部下拉选某个分类，确认只显示该分类的笔记
6. 长按条目 → 置顶，确认该条目跳到最前并出现图钉
7. 进入设置改排序方式、拖字号、开深色模式，返回列表确认生效
8. 打开一条笔记 → 菜单「导出」，确认弹出分享面板并生成了 txt

## 新增一个实验的步骤

以实验四为例：

1. 建包 `app/src/main/java/com/example/android_test1/lab4/`，写 `XxxActivity.kt`（包声明为 `com.example.android_test1.lab4`，记得 `import com.example.android_test1.R`）
2. 资源统一加前缀：`res/layout/lab4_xxx.xml`、`res/drawable/lab4_xxx`、颜色 `lab4_xxx`
3. 在 `AndroidManifest.xml` 的「实验四」注释区块下登记 Activity（`android:name=".lab4.XxxActivity"`）
4. 在 `MainActivity.kt` 的 `groups` 列表末尾追加一个 `ExperimentGroup`

## 运行方式

### 环境要求

- Android Studio 最新版
- JDK 21（AGP 9.3.0 要求）
- Android SDK 37

### 步骤

1. Android Studio 打开项目根目录
2. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
   → Gradle JDK 设为本地 JDK 21（如 `E:\jdk21`）
3. 等待 Gradle 同步完成
4. 连接真机或启动模拟器（Pixel 7 + Android 14/15）
5. 点击 ▶ Run 运行

### JDK 配置说明

若 Gradle 提示 `jlink executable` 错误，说明 IDE 用了错误的 JDK。本项目已在 `gradle.properties` 中固定：

```properties
org.gradle.java.home=E:\\jdk21
org.gradle.java.installations.paths=E:\\jdk21
```

同时在 `.idea/gradle.xml` 中设置 `<option name="gradleJvm" value="E:\\jdk21" />`。
