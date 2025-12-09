package com.empresa.vitalogfinal.model.meta

data class MetaCreateRequest(
    val usuario_id: Int,
    val tipo: String, // "caloria" ou "hidratacao"
    val meta: Double,
    val data_definicao: String
)