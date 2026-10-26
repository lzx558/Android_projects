package com.example.android_test1.lab3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_test1.R

/**
 * 实验三 · 05 RecyclerView 的数据与适配器
 * Animal：列表项数据；AnimalAdapter：ViewHolder + 点击回调 + 增加记录。
 */
data class Animal(val name: String, val iconRes: Int)

class AnimalAdapter(
    private val animals: MutableList<Animal>,
    private val onClick: (Animal) -> Unit
) : RecyclerView.Adapter<AnimalAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val iv: ImageView = view.findViewById(R.id.iv_animal)
        val tv: TextView = view.findViewById(R.id.tv_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lab3_item_recycler_animal, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val animal = animals[position]
        holder.iv.setImageResource(animal.iconRes)
        holder.tv.text = animal.name
        holder.itemView.setOnClickListener { onClick(animal) }
    }

    override fun getItemCount(): Int = animals.size

    fun addItem(animal: Animal) {
        animals.add(animal)
        notifyItemInserted(animals.size - 1)
    }
}
