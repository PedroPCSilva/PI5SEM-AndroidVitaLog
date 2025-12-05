package com.empresa.vitalogfinal.view.menu.diario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.model.alimento.Alimento

class PesquisaAdapter(
    private var lista: List<Alimento>,
    private val onClick: (Alimento) -> Unit
) : RecyclerView.Adapter<PesquisaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.txt_nome_alimento)
        val info: TextView = view.findViewById(R.id.txt_info_alimento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alimento, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.nome.text = item.nome
        holder.info.text = "${item.porcao}g • ${item.caloria} kcal"

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = lista.size

    fun updateList(novaLista: List<Alimento>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}