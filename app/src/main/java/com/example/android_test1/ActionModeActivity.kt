package com.example.android_test1

import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ActionModeActivity : AppCompatActivity() {

    private lateinit var lv: ListView
    private val items = mutableListOf("One", "Two", "Three", "Four", "Five")
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_mode)
        title = "ActionMode"

        lv = findViewById(R.id.lv_items)
        adapter = ArrayAdapter(this, R.layout.list_item_simple, R.id.tv_item, items)
        lv.adapter = adapter

        // 长按触发 ActionMode 上下文菜单
        lv.setMultiChoiceModeListener(object : AbsListView.MultiChoiceModeListener {
            override fun onItemCheckedStateChanged(
                mode: ActionMode?, position: Int, id: Long, checked: Boolean
            ) {
                val count = lv.checkedItemCount
                mode?.title = "$count selected"
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menuInflater.inflate(R.menu.menu_action_mode, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                if (item?.itemId == R.id.action_delete) {
                    val positions = lv.checkedItemPositions
                    val toRemove = mutableListOf<Int>()
                    for (i in 0 until positions.size()) {
                        if (positions.valueAt(i)) {
                            toRemove.add(positions.keyAt(i))
                        }
                    }
                    // 从大到小删除，避免索引错位
                    toRemove.sortDescending()
                    for (pos in toRemove) {
                        items.removeAt(pos)
                    }
                    adapter.notifyDataSetChanged()
                    Toast.makeText(
                        this@ActionModeActivity,
                        "删除了 ${toRemove.size} 项",
                        Toast.LENGTH_SHORT
                    ).show()
                    mode?.finish()
                    return true
                }
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {}
        })
    }
}
