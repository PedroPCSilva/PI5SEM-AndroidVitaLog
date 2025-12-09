package com.empresa.vitalogfinal.view.menu.perfil

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.model.usuario.SenhaUpdateRequest
import com.empresa.vitalogfinal.service.UsuarioService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlterarSenhaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alterar_senha)

        // Agora estes IDs existem no XML corrigido acima
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltar_senha)
        val edtSenhaAtual = findViewById<EditText>(R.id.edt_senha_atual)
        val edtNovaSenha = findViewById<EditText>(R.id.edt_nova_senha)
        val edtConfirmarSenha = findViewById<EditText>(R.id.edt_confirmar_senha)
        val btnSalvar = findViewById<Button>(R.id.btn_salvar_senha)

        btnVoltar.setOnClickListener { finish() }

        btnSalvar.setOnClickListener {
            val senhaAtual = edtSenhaAtual.text.toString()
            val novaSenha = edtNovaSenha.text.toString()
            val confirmar = edtConfirmarSenha.text.toString()

            if (senhaAtual.isEmpty() || novaSenha.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (novaSenha != confirmar) {
                Toast.makeText(this, "As senhas não conferem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Recupera ID do usuário salvo
            val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val usuarioId = sharedPrefs.getInt("user_id", 0)

            if (usuarioId == 0) {
                Toast.makeText(this, "Erro de sessão. Faça login novamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            alterarSenhaNoServidor(usuarioId, senhaAtual, novaSenha)
        }
    }

    private fun alterarSenhaNoServidor(id: Int, atual: String, nova: String) {
        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UsuarioService::class.java)

        val dados = SenhaUpdateRequest(
            senha_atual = atual,
            nova_senha = nova
        )

        lifecycleScope.launch {
            try {
                val response = service.alterarSenha(id, dados)

                if (response.isSuccessful) {
                    Toast.makeText(this@AlterarSenhaActivity, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Erro ao alterar senha"
                    Toast.makeText(this@AlterarSenhaActivity, "Falha: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AlterarSenhaActivity, "Erro de conexão.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}