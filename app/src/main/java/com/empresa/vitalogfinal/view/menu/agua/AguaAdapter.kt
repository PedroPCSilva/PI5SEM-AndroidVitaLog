package com.empresa.vitalogfinal.view.menu.agua

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.model.hidratacao.HidratacaoModel

class AguaAdapter(
    private var lista: List<HidratacaoModel>,
    private val onDeleteClick: (HidratacaoModel) -> Unit // Callback para deletar
) : RecyclerView.Adapter<AguaAdapter.AguaViewHolder>() {

    inner class AguaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtQtd: TextView = itemView.findViewById(R.id.txt_qtd_agua)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_agua) // Agora acha o ID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AguaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_agua, parent, false)
        return AguaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AguaViewHolder, position: Int) {
        val item = lista[position]

        // Formatação bonita (ex: 250.0 vira 250)
        val qtdTexto = if (item.quantidade % 1.0 == 0.0) {
            String.format("%.0f", item.quantidade)
        } else {
            item.quantidade.toString()
        }
        holder.txtQtd.text = "$qtdTexto ml"

        // Configura o clique do botão de apagar
        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount() = lista.size

    fun updateList(novaLista: List<HidratacaoModel>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}