package com.empresa.vitalogfinal.service

import com.empresa.vitalogfinal.model.meta.Meta
import com.empresa.vitalogfinal.model.meta.MetaCreateRequest // <--- Import Novo
import com.empresa.vitalogfinal.model.meta.MetaUpdateRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST // <--- Import Novo
import retrofit2.http.PUT
import retrofit2.http.Path

interface MetaService {

    @GET("meta/{usuarioId}/{data}")
    suspend fun listarMetas(
        @Path("usuarioId") usuarioId: Int,
        @Path("data") data: String
    ): Response<List<Meta>>

    @PUT("meta/{id}")
    suspend fun atualizarMeta(
        @Path("id") id: Int,
        @Body body: MetaUpdateRequest
    ): Response<ResponseBody>

    // FUNÇÃO NOVA PARA CRIAR META SE ELA NÃO EXISTIR
    @POST("meta")
    suspend fun criarMeta(
        @Body body: MetaCreateRequest
    ): Response<ResponseBody>
}