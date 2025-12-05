package com.empresa.vitalogfinal.service

import com.empresa.vitalogfinal.model.diario.FoodModel
import com.empresa.vitalogfinal.model.diario.GrupoModel
import okhttp3.ResponseBody // <--- IMPORTANTE: Adicione esta linha
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface GrupoService {


    @GET("grupo/{id}")
    suspend fun getGrupo(@Path("id") id: Int): Response<GrupoModel>

    @GET("diario/grupo/{id}/alimentos")
    suspend fun getAlimentos(@Path("id") id: Int): Response<List<FoodModel>>

    @DELETE("diario/grupo/{id}")
    suspend fun deleteGrupo(@Path("id") id: Int): Response<ResponseBody>

    @DELETE("alimento/{id}")
    suspend fun deleteAlimento(@Path("id") id: Int): Response<ResponseBody>
}