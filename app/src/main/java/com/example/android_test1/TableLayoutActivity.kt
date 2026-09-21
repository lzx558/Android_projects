package com.example.android_test1

import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 实验2：表格布局
 * TableLayout + TableRow：第一列菜单名，第二列快捷键（stretchColumns=1 拉伸），
 * Import/Export 通过 drawableStart 在同一单元格内显示图标。
 */
class TableLayoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_layout)

        val table = findViewById<TableLayout>(R.id.table_menu)
        for (i in 0 until table.childCount) {
            val row = table.getChildAt(i) as? TableRow ?: continue
            val label = row.getChildAt(0) as? TextView ?: continue
            label.setOnClickListener {
                Toast.makeText(this, label.text, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
