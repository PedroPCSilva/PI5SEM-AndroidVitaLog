package com.empresa.vitalogfinal

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.service.AlterarSenhaRequest
import com.empresa.vitalogfinal.service.GenericResponse
import com.empresa.vitalogfinal.service.UsuarioService
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlterarSenhaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alterar_senha)

        val edtSenhaAtual = findViewById<TextInputEditText>(R.id.edtSenhaAtual)
        val edtNovaSenha = findViewById<TextInputEditText>(R.id.edtNovaSenha)
        val edtConfirmarSenha = findViewById<TextInputEditText>(R.id.edtConfirmarSenha)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarSenha)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        btnCancelar.setOnClickListener { finish() }

        btnSalvar.setOnClickListener {
            val atual = edtSenhaAtual.text.toString()
            val nova = edtNovaSenha.text.toString()
            val confirmar = edtConfirmarSenha.text.toString()

            if (atual.isEmpty() || nova.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nova != confirmar) {
                Toast.makeText(this, "As senhas novas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            salvarNovaSenha(atual, nova)
        }
    }

    private fun salvarNovaSenha(senhaAtual: String, novaSenha: String) {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("user_id", 0)

        if (userId == 0) {
            Toast.makeText(this, "Erro: Usuário não identificado.", Toast.LENGTH_SHORT).show()
            return
        }

        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UsuarioService::class.java)
        val request = AlterarSenhaRequest(senhaAtual, novaSenha)

        service.alterarSenha(userId, request).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(applicationContext, "A senha atual está incorreta.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, "Erro ao alterar senha.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro de conexão.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}