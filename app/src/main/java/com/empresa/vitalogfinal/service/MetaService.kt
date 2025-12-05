package com.empresa.vitalogfinal.service

import com.empresa.vitalogfinal.model.meta.MetaModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MetaService {

    @GET("meta/{usuarioId}/{data}")
    suspend fun listarMetas(
        @Path("usuarioId") usuarioId: Int,
        @Path("data") data: String
    ): Response<List<MetaModel>>

    @POST("meta")
    suspend fun salvarMeta(@Body meta: MetaModel): Response<Map<String, Any>>
}