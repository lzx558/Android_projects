package com.example.android_test1.lab3

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_test1.R

/**
 * 实验三 · 05 RecyclerView
 * LinearLayoutManager + 自定义 AnimalAdapter：点击条目 Toast 名称，
 * 点击按钮追加一条记录并滚动到末位。
 */
class RecyclerViewActivity : AppCompatActivity() {

    private val animals = mutableListOf(
        Animal("Lion", R.drawable.lab3_lion),
        Animal("Tiger", R.drawable.lab3_tiger),
        Animal("Monkey", R.drawable.lab3_monkey),
        Animal("Dog", R.drawable.lab3_dog),
        Animal("Cat", R.drawable.lab3_cat),
        Animal("Elephant", R.drawable.lab3_elephant),
    )

    private lateinit var adapter: AnimalAdapter
    private var addIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lab3_recycler_view)
        title = "RecyclerView"

        val rv = findViewById<RecyclerView>(R.id.rv_animals)
        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)

        adapter = AnimalAdapter(animals) { animal ->
            Toast.makeText(this, animal.name, Toast.LENGTH_SHORT).show()
        }
        rv.adapter = adapter

        // 增加一条记录
        findViewById<Button>(R.id.btn_add).setOnClickListener {
            val names = listOf("Lion", "Tiger", "Monkey", "Dog", "Cat", "Elephant")
            val icons = listOf(
                R.drawable.lab3_lion, R.drawable.lab3_tiger, R.drawable.lab3_monkey,
                R.drawable.lab3_dog, R.drawable.lab3_cat, R.drawable.lab3_elephant
            )
            val idx = addIndex++ % names.size
            adapter.addItem(Animal(names[idx], icons[idx]))
            rv.scrollToPosition(animals.size - 1)
        }
    }
}
