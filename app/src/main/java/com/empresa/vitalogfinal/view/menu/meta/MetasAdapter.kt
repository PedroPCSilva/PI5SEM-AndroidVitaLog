package com.empresa.vitalogfinal.view.menu.meta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.model.meta.MetaModel

class MetasAdapter(
    private var lista: List<MetaModel>,
    private val onEditClick: (MetaModel) -> Unit
) : RecyclerView.Adapter<MetasAdapter.ViewHolder>() {

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val titulo: TextView = v.findViewById(R.id.txt_meta_titulo)
        val valor: TextView = v.findViewById(R.id.txt_meta_valor)
        val btn: Button = v.findViewById(R.id.btn_editar_meta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_meta, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        holder.titulo.text = if (item.tipo == "caloria") "Calorias (kcal)" else "Hidratação (ml)"
        holder.valor.text = item.meta.toString()

        holder.btn.setOnClickListener { onEditClick(item) }
    }

    override fun getItemCount() = lista.size

    fun updateList(nova: List<MetaModel>) {
        lista = nova
        notifyDataSetChanged()
    }
}