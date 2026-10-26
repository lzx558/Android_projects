package com.example.android_test1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_test1.lab2.CalculatorActivity
import com.example.android_test1.lab2.ComposeTaskActivity
import com.example.android_test1.lab2.LinearLayoutActivity
import com.example.android_test1.lab2.SpaceTravelActivity
import com.example.android_test1.lab2.TableLayoutActivity
import com.example.android_test1.lab3.ActionModeActivity
import com.example.android_test1.lab3.AlertDialogActivity
import com.example.android_test1.lab3.ListViewActivity
import com.example.android_test1.lab3.MenuTestActivity
import com.example.android_test1.lab3.RecyclerViewActivity
import com.example.android_test1.midterm.NoteEditor
import com.example.android_test1.midterm.NoteSettings
import com.example.android_test1.midterm.NotesList
import com.example.android_test1.ui.theme.Android_test1Theme

/* ============================================================
 * 实验总菜单（入口）
 * ------------------------------------------------------------
 * 每个实验的所有界面都放在自己的包里（lab2 / lab3 / midterm …），
 * 新增实验时：新建包 → 写 Activity → 下面加一个 ExperimentGroup。
 * ============================================================ */

/** 单个实验界面 */
private data class Experiment(
    val title: String,
    val subtitle: String,
    val target: Class<*>,
)

/** 一次实验（按作业划分），内部包含若干界面 */
private data class ExperimentGroup(
    val name: String,
    val summary: String,
    val items: List<Experiment>,
)

private val groups = listOf(

    ExperimentGroup(
        name = "实验二 · Android 界面布局",
        summary = "LinearLayout / TableLayout / ConstraintLayout / Compose",
        items = listOf(
            Experiment(
                "01 线性布局 LinearLayout",
                "嵌套 LinearLayout + weight 实现 4×4 等宽等高按钮网格",
                LinearLayoutActivity::class.java
            ),
            Experiment(
                "02 表格布局 TableLayout",
                "TableLayout + TableRow 实现深色菜单（Open/Save/Quit…）",
                TableLayoutActivity::class.java
            ),
            Experiment(
                "03 约束布局 ConstraintLayout（一）",
                "Chain 约束实现计算器界面与按键网格",
                CalculatorActivity::class.java
            ),
            Experiment(
                "04 约束布局 ConstraintLayout（二）",
                "太空旅行订票界面：Tab、航线、开关、DEPART 按钮",
                SpaceTravelActivity::class.java
            ),
            Experiment(
                "05 Android Compose",
                "课程学习任务列表：添加 / 勾选 / 删除与状态同步",
                ComposeTaskActivity::class.java
            ),
        )
    ),

    ExperimentGroup(
        name = "实验三 · 事件处理与列表",
        summary = "ListView / AlertDialog / 菜单 / ActionMode / RecyclerView",
        items = listOf(
            Experiment(
                "01 ListView + SimpleAdapter",
                "动物列表：点击 Toast + 发送通知",
                ListViewActivity::class.java
            ),
            Experiment(
                "02 自定义 AlertDialog",
                "自定义登录对话框：橙色标题栏 + 用户名/密码",
                AlertDialogActivity::class.java
            ),
            Experiment(
                "03 XML 定义菜单",
                "字体大小（小/中/大）、普通项、字体颜色（红/黑）",
                MenuTestActivity::class.java
            ),
            Experiment(
                "04 ActionMode 上下文菜单",
                "ListView 多选删除：长按进入上下文操作模式",
                ActionModeActivity::class.java
            ),
            Experiment(
                "05 RecyclerView",
                "动物列表：Adapter + ViewHolder，点击显示名称，增加记录即时更新",
                RecyclerViewActivity::class.java
            ),
        )
    ),

    ExperimentGroup(
        name = "期中考试 · NotePad 记事本应用",
        summary = "ContentProvider + SQLite · 时间戳 · 搜索 · 分类 · 置顶 · 导出 · 偏好设置",
        items = listOf(
            Experiment(
                "01 笔记列表",
                "条目显示时间戳，按标题或内容搜索，分类筛选，长按置顶/复制/删除",
                NotesList::class.java
            ),
            Experiment(
                "02 笔记编辑",
                "标题 / 正文 / 分类编辑，导出为 txt 分享，置顶切换",
                NoteEditor::class.java
            ),
            Experiment(
                "03 偏好设置",
                "排序方式、正文字号、深色模式、新建笔记默认分类",
                NoteSettings::class.java
            ),
        )
    ),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Android_test1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ExperimentMenu(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
private fun ExperimentMenu(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "移动软件开发实验",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            groups.forEach { group ->
                // 分组标题
                item(key = "header_${group.name}") {
                    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)) {
                        Text(
                            text = group.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2EA76D)
                        )
                        Text(
                            text = group.summary,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                // 该组下的界面
                items(group.items) { exp ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                            .clickable {
                                context.startActivity(Intent(context, exp.target))
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = exp.title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text(
                                text = exp.subtitle,
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
