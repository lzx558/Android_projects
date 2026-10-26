package com.example.android_test1.lab3

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android_test1.R

/**
 * 实验三 · 03 XML 定义菜单
 * 在 res/menu 中定义选项菜单：字体大小（小/中/大）、普通项、字体颜色（红/黑）。
 */
class MenuTestActivity : AppCompatActivity() {

    private lateinit var tvTest: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lab3_menu_test)
        title = "MenuTest"
        tvTest = findViewById(R.id.tv_test)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.lab3_menu_test, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // 字体大小
            R.id.menu_size_small -> {
                tvTest.textSize = 10f
                true
            }
            R.id.menu_size_medium -> {
                tvTest.textSize = 16f
                true
            }
            R.id.menu_size_large -> {
                tvTest.textSize = 20f
                true
            }
            // 普通菜单项
            R.id.menu_normal -> {
                Toast.makeText(this, "普通菜单项被点击", Toast.LENGTH_SHORT).show()
                true
            }
            // 字体颜色
            R.id.menu_color_red -> {
                tvTest.setTextColor(0xFFFF0000.toInt())
                true
            }
            R.id.menu_color_black -> {
                tvTest.setTextColor(0xFF000000.toInt())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
