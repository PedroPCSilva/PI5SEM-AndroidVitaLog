package com.empresa.vitalogfinal.repository

import com.empresa.vitalogfinal.model.meta.Meta
import com.empresa.vitalogfinal.model.meta.MetaCreateRequest
import com.empresa.vitalogfinal.model.meta.MetaUpdateRequest
import com.empresa.vitalogfinal.service.MetaService

class MetaRepository(private val service: MetaService) {

    suspend fun listar(usuarioId: Int, data: String): List<Meta> {
        val response = service.listarMetas(usuarioId, data)
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    suspend fun atualizar(id: Int, valor: Double): Boolean {
        val request = MetaUpdateRequest(meta = valor)
        return service.atualizarMeta(id, request).isSuccessful
    }

    // FUNÇÃO NOVA
    suspend fun criar(usuarioId: Int, tipo: String, valor: Double, data: String): Boolean {
        val request = MetaCreateRequest(
            usuario_id = usuarioId,
            tipo = tipo,
            meta = valor,
            data_definicao = data
        )
        return service.criarMeta(request).isSuccessful
    }
}