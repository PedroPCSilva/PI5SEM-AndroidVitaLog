package com.empresa.vitalogfinal.model.usuario

data class Usuario(
    val id: Int,
    val nome: String,
    val email: String,
    val sobrenome: String?,
    val peso: Double?,
    val altura: Double?,
    val data_nascimento: String?
)