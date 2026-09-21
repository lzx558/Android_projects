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
import com.example.android_test1.ui.theme.Android_test1Theme

private data class Experiment(
    val title: String,
    val subtitle: String,
    val target: Class<*>,
)

private val experiments = listOf(
    Experiment(
        "1. 线性布局 LinearLayout",
        "嵌套 LinearLayout + weight 实现 4×4 等宽等高按钮网格",
        LinearLayoutActivity::class.java
    ),
    Experiment(
        "2. 表格布局 TableLayout",
        "TableLayout + TableRow 实现深色菜单（Open/Save/Quit…）",
        TableLayoutActivity::class.java
    ),
    Experiment(
        "3. 约束布局 ConstraintLayout（一）",
        "Chain 约束实现计算器界面与按键网格",
        CalculatorActivity::class.java
    ),
    Experiment(
        "4. 约束布局 ConstraintLayout（二）",
        "太空旅行订票界面：Tab、航线、开关、DEPART 按钮",
        SpaceTravelActivity::class.java
    ),
    Experiment(
        "5. Android Compose",
        "课程学习任务列表：添加 / 勾选 / 删除与状态同步",
        ComposeTaskActivity::class.java
    )
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
            text = "实验2  Android 界面布局",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(experiments) { exp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable {
                            context.startActivity(Intent(context, exp.target))
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = exp.title, fontSize = 17.sp, fontWeight = FontWeight.Medium)
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
