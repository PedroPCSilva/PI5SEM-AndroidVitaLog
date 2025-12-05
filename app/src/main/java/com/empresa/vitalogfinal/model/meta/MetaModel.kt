package com.empresa.vitalogfinal.model.meta

data class MetaModel(
    val id: Int = 0,
    val usuario_id: Int,
    val tipo: String,
    val meta: Double,
    val data_registro: String? = null
)