package com.empresa.vitalogfinal.service

import com.empresa.vitalogfinal.model.diario.CriarGrupoRequest
import com.empresa.vitalogfinal.model.diario.GrupoModel
import com.empresa.vitalogfinal.model.diario.TotalCaloriasResponse // <--- Importe
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DiarioService {

    @GET("diario/{usuarioId}/{data}")
    suspend fun getDiario(
        @Path("usuarioId") usuarioId: Int,
        @Path("data") data: String
    ): Response<List<GrupoModel>>

    @POST("diario/{usuarioId}/grupo")
    suspend fun criarGrupo(
        @Path("usuarioId") usuarioId: Int,
        @Body request: CriarGrupoRequest
    ): Response<GrupoModel>


    @GET("diario/total/{usuarioId}/{data}")
    suspend fun getTotalCalorias(
        @Path("usuarioId") usuarioId: Int,
        @Path("data") data: String
    ): Response<TotalCaloriasResponse>
}