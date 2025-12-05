package com.empresa.vitalogfinal.service
import com.empresa.vitalogfinal.model.relatorio.RelatorioDia
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RelatorioService {
    @GET("relatorio/semanal/{id}")
    suspend fun getSemanal(@Path("id") id: Int): Response<List<RelatorioDia>>
}