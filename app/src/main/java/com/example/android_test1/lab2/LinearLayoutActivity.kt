package com.example.android_test1.lab2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android_test1.R

/**
 * 实验二 · 01 线性布局 LinearLayout
 * 4 行（垂直 LinearLayout）× 4 列（水平 LinearLayout），
 * 每格使用 layout_width="0dp" + layout_weight="1" 实现等宽等高。
 */
class LinearLayoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lab2_linear_layout)

        // 点击任一按钮时提示其文字，验证每个按钮可独立响应
        // 布局层级：content → 垂直 LinearLayout → 水平 LinearLayout(×4) → Button(×16)
        val root = findViewById<android.view.ViewGroup>(android.R.id.content)
        val verticalLayout = root.getChildAt(0) as android.view.ViewGroup
        for (i in 0 until verticalLayout.childCount) {
            val row = verticalLayout.getChildAt(i) as android.view.ViewGroup
            for (j in 0 until row.childCount) {
                val btn = row.getChildAt(j) as android.widget.Button
                btn.setOnClickListener {
                    Toast.makeText(this, btn.text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
