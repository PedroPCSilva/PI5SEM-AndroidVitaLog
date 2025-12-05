package com.empresa.vitalogfinal.service

import com.empresa.vitalogfinal.model.usuario.CadastroRequest
import com.empresa.vitalogfinal.model.usuario.CadastroResponse
import com.empresa.vitalogfinal.model.usuario.LoginResponse
import com.empresa.vitalogfinal.model.usuario.SenhaUpdateRequest // <--- Importe Importante
import com.empresa.vitalogfinal.model.usuario.Usuario
import com.empresa.vitalogfinal.model.usuario.UsuarioUpdateRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH // <--- Importe do PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioService {

    @GET("usuario/login")
    fun login(
        @Query("email") email: String,
        @Query("senha") senha: String
    ): Call<LoginResponse>

    @POST("usuario/cadastro")
    fun cadastro(
        @Body request: CadastroRequest
    ): Call<CadastroResponse>

    @GET("usuario/{id}")
    suspend fun getPerfil(@Path("id") id: Int): Response<Usuario>

    @PUT("usuario/{id}")
    suspend fun atualizarPerfil(
        @Path("id") id: Int,
        @Body dados: UsuarioUpdateRequest
    ): Response<Map<String, String>>

    @PATCH("usuario/senha/{id}")
    suspend fun alterarSenha(
        @Path("id") id: Int,
        @Body dados: SenhaUpdateRequest
    ): Response<Map<String, String>>
}