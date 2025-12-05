package com.empresa.vitalogfinal.repository

import com.empresa.vitalogfinal.model.alimento.Alimento
import com.empresa.vitalogfinal.model.diario.FoodModel
import com.empresa.vitalogfinal.service.AlimentoService

class AlimentoRepository(private val service: AlimentoService) {

    suspend fun pesquisar(termo: String): List<Alimento> {
        return try {
            val res = service.pesquisarAlimentos(termo)
            if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun salvar(alimento: FoodModel): Boolean {
        return try {
            val res = service.adicionarAlimento(alimento)
            res.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}