package com.empresa.vitalogfinal.model.usuario

data class UsuarioUpdateRequest(
    val nome: String,
    val email: String,
    val sobrenome: String,
    val data_nascimento: String,
    val peso: Double,
    val altura: Double,
    val senha_atual: String
)