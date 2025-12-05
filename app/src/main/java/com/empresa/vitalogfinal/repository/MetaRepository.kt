package com.empresa.vitalogfinal.repository

import com.empresa.vitalogfinal.model.meta.MetaModel
import com.empresa.vitalogfinal.service.MetaService

class MetaRepository(private val api: MetaService) {

    suspend fun listar(usuarioId: Int, data: String): List<MetaModel> {
        val res = api.listarMetas(usuarioId, data)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun salvar(meta: MetaModel): Boolean {
        return api.salvarMeta(meta).isSuccessful
    }
}