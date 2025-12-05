package com.empresa.vitalogfinal.repository

import com.empresa.vitalogfinal.model.diario.CriarGrupoRequest
import com.empresa.vitalogfinal.model.diario.GrupoModel
import com.empresa.vitalogfinal.service.DiarioService

class DiarioRepository(
    private val diarioService: DiarioService
) {

    suspend fun getDiario(usuarioId: Int, data: String): List<GrupoModel>? {
        val response = diarioService.getDiario(usuarioId, data)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun criarGrupo(usuarioId: Int, nome: String): GrupoModel? {
        val request = CriarGrupoRequest(nome)
        val response = diarioService.criarGrupo(usuarioId, request)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getTotalCalorias(usuarioId: Int, data: String): Double {
        return try {
            val res = diarioService.getTotalCalorias(usuarioId, data)
            if (res.isSuccessful) res.body()?.total ?: 0.0 else 0.0
        } catch (e: Exception) {
            0.0
        }
    }
}