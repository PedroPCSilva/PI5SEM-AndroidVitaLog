package com.empresa.vitalogfinal.view.menu.agua

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.model.hidratacao.HidratacaoModel

class AguaAdapter(
    private var lista: List<HidratacaoModel>,
    private val onClickDelete: (HidratacaoModel) -> Unit
) : RecyclerView.Adapter<AguaAdapter.ViewHolder>() {

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val txtQtd: TextView = v.findViewById(R.id.txt_qtd_agua)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_agua, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        val qtdTexto = if (item.quantidade % 1.0 == 0.0) {
            String.format("%.0f", item.quantidade)
        } else {
            item.quantidade.toString()
        }

        holder.txtQtd.text = "$qtdTexto ml"

        holder.itemView.setOnClickListener {
            onClickDelete(item)
        }
    }

    override fun getItemCount() = lista.size

    fun updateList(nova: List<HidratacaoModel>) {
        lista = nova
        notifyDataSetChanged()
    }
}