package com.example.android_test1.lab2

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.android_test1.R

/**
 * 实验二 · 04 约束布局（二）——太空旅行订票
 * 各元素均通过 ConstraintLayout 约束定位（Tab 等分链、航线 packed 链等）。
 * 图片资源来自群文件：space_station_icon、rocket_icon、rover_icon（Tab 图标）、
 * double_arrows（往返交换箭头）、single_arrow（单程箭头）、galaxy（轨道插图）。
 */
class SpaceTravelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lab2_space_travel)

        val departure = findViewById<TextView>(R.id.box_departure)
        val arrival = findViewById<TextView>(R.id.box_arrival)
        val imgSwap = findViewById<ImageView>(R.id.img_swap)
        val switchOneWay = findViewById<SwitchCompat>(R.id.switch_one_way)

        // One Way 开关：勾选时显示单程箭头，取消时显示双向箭头
        switchOneWay.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                imgSwap.setImageResource(R.drawable.lab2_single_arrow)
            } else {
                imgSwap.setImageResource(R.drawable.lab2_double_arrows)
            }
        }

        // 初始状态为 One Way（单程），显示单箭头
        imgSwap.setImageResource(R.drawable.lab2_single_arrow)

        // 点击箭头交换出发地与目的地
        imgSwap.setOnClickListener {
            val tmp = departure.text
            departure.text = arrival.text
            arrival.text = tmp
        }

        // 点击人数按钮在 1~4 之间循环
        val traveller = findViewById<android.widget.Button>(R.id.btn_traveller)
        traveller.setOnClickListener {
            val n = traveller.text.toString().substringBefore(" ").toIntOrNull() ?: 1
            val next = if (n >= 4) 1 else n + 1
            traveller.text = "$next Traveller"
        }

        findViewById<android.widget.Button>(R.id.btn_depart).setOnClickListener {
            Toast.makeText(
                this,
                "${departure.text} → ${arrival.text}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
