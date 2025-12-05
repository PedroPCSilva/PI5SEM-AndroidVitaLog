package com.empresa.vitalogfinal.model.usuario

data class CadastroRequest(
    val nome: String,
    val email: String,
    val senha: String
)

