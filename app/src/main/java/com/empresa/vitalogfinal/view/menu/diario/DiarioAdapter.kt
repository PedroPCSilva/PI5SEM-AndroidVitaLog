package com.empresa.vitalogfinal.view.menu.diario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.model.diario.GrupoModel

class DiarioAdapter(
    private var grupos: List<GrupoModel>,
    private val onClick: (GrupoModel) -> Unit
) : RecyclerView.Adapter<DiarioAdapter.GrupoViewHolder>() {

    inner class GrupoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome = itemView.findViewById<TextView>(R.id.txt_nome_grupo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrupoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grupo, parent, false) // <--- Tem que ser R.layout.item_grupo
        return GrupoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GrupoViewHolder, position: Int) {
        val grupo = grupos[position]
        holder.nome.text = grupo.nome

        holder.itemView.setOnClickListener {
            onClick(grupo)
        }
    }

    override fun getItemCount() = grupos.size

    fun updateList(newList: List<GrupoModel>) {
        grupos = newList
        notifyDataSetChanged()
    }
}
