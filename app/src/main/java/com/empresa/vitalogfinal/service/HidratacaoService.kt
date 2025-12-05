package com.empresa.vitalogfinal.service

import com.empresa.vitalogfinal.model.hidratacao.HidratacaoModel
import com.empresa.vitalogfinal.model.hidratacao.HidratacaoRequest // <--- Importe aqui
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface HidratacaoService {

    @GET("hidratacao/{usuarioId}/{data}")
    suspend fun listar(
        @Path("usuarioId") usuarioId: Int,
        @Path("data") data: String
    ): Response<List<HidratacaoModel>>

    @POST("hidratacao")
    suspend fun adicionar(@Body corpo: HidratacaoRequest): Response<ResponseBody>

    @DELETE("hidratacao/{id}")
    suspend fun remover(@Path("id") id: Int): Response<ResponseBody>
}