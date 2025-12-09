package com.empresa.vitalogfinal.model.diario

data class GrupoModel(
    val id: Int,
    val usuario_id: Int,
    val nome: String,
    // ADICIONADO PARA CORRIGIR O ERRO NO VIEWMODEL
    val total_calorias: Double = 0.0,
    val data_registro: String? = null
)