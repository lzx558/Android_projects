package com.example.android_test1.lab2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_test1.R
import com.example.android_test1.ui.theme.Android_test1Theme

private data class Task(val id: Int, val name: String, val done: Boolean)

/**
 * 实验二 · 05 Android Compose——课程学习任务列表
 * - 初始 3 项任务，完成 1 项
 * - 输入任务名点击“添加”新增（如“复习 LazyColumn”）
 * - 勾选任务后，完成数与删除线同步更新
 */
class ComposeTaskActivity : ComponentActivity() {

    private val accentRed = Color(0xFFE53935)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Android_test1Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TaskScreen(accentRed)
                }
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun TaskScreen(accent: Color) {
        val tasks = remember {
            mutableStateListOf(
                Task(1, "学习 Column 和 Row", done = true),
                Task(2, "学习状态管理", done = false),
                Task(3, "完成 Compose 实验", done = false)
            )
        }
        var input by remember { mutableStateOf("") }
        var nextId by remember { mutableStateOf(4) }
        val doneCount = tasks.count { it.done }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "课程学习任务",
                color = accent,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("请输入学习任务") }
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        val name = input.trim()
                        if (name.isNotEmpty()) {
                            tasks.add(Task(nextId++, name, done = false))
                            input = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accent)
                ) {
                    Text("添加")
                }
            }

            Text(
                text = "已完成：$doneCount / ${tasks.size}",
                modifier = Modifier.padding(vertical = 12.dp),
                fontSize = 15.sp
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.done,
                            onCheckedChange = { checked ->
                                val index = tasks.indexOf(task)
                                if (index >= 0) {
                                    tasks[index] = task.copy(done = checked)
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = accent,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = task.name,
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp,
                            color = if (task.done) Color.Gray else Color.Black,
                            textDecoration = if (task.done) TextDecoration.LineThrough else null
                        )
                        TextButton(onClick = { tasks.remove(task) }) {
                            Text("删除", color = accent)
                        }
                    }
                }
            }
        }
    }
}
