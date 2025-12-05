package com.empresa.vitalogfinal.view.menu.perfil

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

        val edtAtual = findViewById<EditText>(R.id.edt_senha_antiga)
        val edtNova = findViewById<EditText>(R.id.edt_nova_senha)
        val edtConfirm = findViewById<EditText>(R.id.edt_confirmar_nova)
        val btnSalvar = findViewById<Button>(R.id.btn_mudar_senha)

        val usuarioId = getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getInt("user_id", 0)


        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(UsuarioService::class.java)

        btnSalvar.setOnClickListener {
            val atual = edtAtual.text.toString()
            val nova = edtNova.text.toString()
            val confirm = edtConfirm.text.toString()

            if (atual.isEmpty() || nova.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nova != confirm) {
                Toast.makeText(this, "A confirmação da senha não confere", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            lifecycleScope.launch {
                try {
                    val request = SenhaUpdateRequest(atual, nova)
                    val res = service.alterarSenha(usuarioId, request)

                    if (res.isSuccessful) {
                        Toast.makeText(this@AlterarSenhaActivity, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else if (res.code() == 401) {

                        Toast.makeText(this@AlterarSenhaActivity, "A senha atual está incorreta", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AlterarSenhaActivity, "Erro no servidor", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@AlterarSenhaActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}