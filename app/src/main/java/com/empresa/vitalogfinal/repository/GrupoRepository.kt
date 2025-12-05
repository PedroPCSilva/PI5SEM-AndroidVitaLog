package com.empresa.vitalogfinal.repository

import com.empresa.vitalogfinal.model.diario.FoodModel
import com.empresa.vitalogfinal.model.diario.GrupoModel
import com.empresa.vitalogfinal.service.GrupoService

class GrupoRepository(private val api: GrupoService) {

    suspend fun getGrupo(id: Int): GrupoModel? {
        val res = api.getGrupo(id)
        return if (res.isSuccessful) res.body() else null
    }

    suspend fun getAlimentos(grupoId: Int): List<FoodModel> {
        val res = api.getAlimentos(grupoId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun apagarGrupo(id: Int): Boolean {
        return api.deleteGrupo(id).isSuccessful
    }

    suspend fun apagarAlimento(id: Int): Boolean {
        return api.deleteAlimento(id).isSuccessful
    }
}