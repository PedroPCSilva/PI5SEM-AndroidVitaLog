package com.empresa.vitalogfinal.model.alimento

import com.google.gson.annotations.SerializedName

data class Alimento(
    val id: Int,
    val nome: String,


    @SerializedName("caloria", alternate = ["caloria_base", "calorias"])
    val caloria: Double,

    @SerializedName("porcao", alternate = ["porcao_base", "tamanho_porcao"])
    val porcao: Double
)