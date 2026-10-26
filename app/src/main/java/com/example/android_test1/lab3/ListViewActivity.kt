package com.example.android_test1.lab3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.android_test1.MainActivity
import com.example.android_test1.R

/**
 * 实验三 · 01 ListView + SimpleAdapter
 * ListView 配合 SimpleAdapter 显示「文字 + 图标」的动物列表；
 * 点击条目弹出 Toast，并发出一条通知。
 */
class ListViewActivity : AppCompatActivity() {

    private lateinit var lv: ListView

    // 动物数据
    private val animals = listOf(
        "Lion" to R.drawable.lab3_lion,
        "Tiger" to R.drawable.lab3_tiger,
        "Monkey" to R.drawable.lab3_monkey,
        "Dog" to R.drawable.lab3_dog,
        "Cat" to R.drawable.lab3_cat,
        "Elephant" to R.drawable.lab3_elephant,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lab3_list_view)
        title = "ListView"

        lv = findViewById(R.id.lv_animals)

        // 构造 SimpleAdapter 数据
        val data = animals.map { (name, icon) ->
            mapOf(
                "name" to name,
                "icon" to icon,
            )
        }

        val adapter = SimpleAdapter(
            this,
            data,
            R.layout.lab3_list_item_animal,
            arrayOf("name", "icon"),
            intArrayOf(R.id.tv_name, R.id.iv_icon)
        )
        lv.adapter = adapter

        // 点击：Toast + Notification
        lv.setOnItemClickListener { _, _, position, _ ->
            val name = animals[position].first

            // Toast 显示选中项
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show()

            // 发送通知
            sendNotification(name)
        }
    }

    private fun sendNotification(name: String) {
        val mgr = getSystemService(NotificationManager::class.java)

        // Android 8+ 需要通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "animal_channel",
                "动物通知",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mgr.createNotificationChannel(channel)
        }

        // 点击通知跳回 MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pi = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "animal_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(name)
            .setContentText("你点击了 $name，这是一条通知消息")
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        mgr.notify(System.currentTimeMillis().toInt(), notification)
    }
}
