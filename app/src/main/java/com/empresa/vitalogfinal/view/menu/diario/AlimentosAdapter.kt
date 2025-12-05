package com.empresa.vitalogfinal.view.menu.diario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.model.diario.FoodModel

class AlimentosAdapter(
    private var alimentos: List<FoodModel>,
    private val onClick: (FoodModel) -> Unit
) : RecyclerView.Adapter<AlimentosAdapter.AlimentoViewHolder>() {

    inner class AlimentoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome = view.findViewById<TextView>(R.id.txt_nome_alimento)
        val info = view.findViewById<TextView>(R.id.txt_info_alimento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlimentoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alimento, parent, false)
        return AlimentoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlimentoViewHolder, position: Int) {
        val alimento = alimentos[position]
        holder.nome.text = alimento.nome

        val infoTxt = "${alimento.porcao_consumida}g • ${alimento.caloria_base} kcal"
        holder.info.text = infoTxt

        holder.itemView.setOnClickListener { onClick(alimento) }
    }

    override fun getItemCount() = alimentos.size

    fun update(newList: List<FoodModel>) {
        alimentos = newList
        notifyDataSetChanged()
    }
}
