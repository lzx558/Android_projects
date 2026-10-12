package com.example.android_test1

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewActivity : AppCompatActivity() {

    private val animals = mutableListOf(
        Animal("Lion", R.drawable.lion),
        Animal("Tiger", R.drawable.tiger),
        Animal("Monkey", R.drawable.monkey),
        Animal("Dog", R.drawable.dog),
        Animal("Cat", R.drawable.cat),
        Animal("Elephant", R.drawable.elephant),
    )

    private lateinit var adapter: AnimalAdapter
    private var addIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
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
                R.drawable.lion, R.drawable.tiger, R.drawable.monkey,
                R.drawable.dog, R.drawable.cat, R.drawable.elephant
            )
            val idx = addIndex++ % names.size
            adapter.addItem(Animal(names[idx], icons[idx]))
            rv.scrollToPosition(animals.size - 1)
        }
    }
}
