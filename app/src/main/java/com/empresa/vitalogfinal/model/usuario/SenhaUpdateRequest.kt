package com.empresa.vitalogfinal.model.usuario

data class SenhaUpdateRequest(
    val senha_atual: String,
    val nova_senha: String
)