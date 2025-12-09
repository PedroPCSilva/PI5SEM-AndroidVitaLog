package com.empresa.vitalogfinal.model.meta

data class Meta(
    val id: Int,
    val usuario_id: Int,
    val tipo: String, // ex: "caloria", "hidratacao"
    val meta: Double,
    val data_definicao: String?
)