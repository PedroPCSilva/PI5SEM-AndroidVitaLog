package com.empresa.vitalogfinal.view.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SimpleAdapter(private val items: List<String>) :
    RecyclerView.Adapter<SimpleAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.tv.text = items[position]
    }

    override fun getItemCount() = items.size
}
