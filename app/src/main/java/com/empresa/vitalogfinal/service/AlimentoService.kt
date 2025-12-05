package com.empresa.vitalogfinal.service

import com.empresa.vitalogfinal.model.alimento.Alimento
import com.empresa.vitalogfinal.model.diario.FoodModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AlimentoService {

    @GET("alimentos/pesquisa")
    suspend fun pesquisarAlimentos(
        @Query("q") termo: String
    ): Response<List<Alimento>>


    @POST("alimento")
    suspend fun adicionarAlimento(
        @Body alimento: FoodModel
    ): Response<Map<String, Any>>
}