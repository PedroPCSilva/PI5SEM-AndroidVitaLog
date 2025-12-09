package com.empresa.vitalogfinal.view.menu.meta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.model.meta.Meta // <--- IMPORT CRÍTICO

class MetasAdapter(
    private var lista: List<Meta>,
    private val onClickEditar: (Meta) -> Unit
) : RecyclerView.Adapter<MetasAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitulo: TextView = itemView.findViewById(R.id.txt_meta_titulo)
        val txtValor: TextView = itemView.findViewById(R.id.txt_meta_valor)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btn_editar_meta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meta, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        val titulo = when (item.tipo) {
            "caloria" -> "Calorias Diárias"
            "hidratacao" -> "Hidratação Diária"
            else -> item.tipo.replaceFirstChar { it.uppercase() }
        }

        val unidade = if (item.tipo == "caloria") "kcal" else "ml"

        holder.txtTitulo.text = titulo
        holder.txtValor.text = "${String.format("%.0f", item.meta)} $unidade"

        holder.btnEditar.setOnClickListener { onClickEditar(item) }
    }

    override fun getItemCount() = lista.size

    fun updateList(novaLista: List<Meta>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}