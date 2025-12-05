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
import com.empresa.vitalogfinal.model.usuario.UsuarioUpdateRequest
import com.empresa.vitalogfinal.service.UsuarioService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EditarDadosActivity : AppCompatActivity() {

    private lateinit var edtNome: EditText
    private lateinit var edtSobrenome: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtNasc: EditText
    private lateinit var edtPeso: EditText
    private lateinit var edtAltura: EditText
    private lateinit var edtSenhaConfirm: EditText

    private var usuarioId = 0
    private lateinit var service: UsuarioService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_dados)

        usuarioId = getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getInt("user_id", 0)

        edtNome = findViewById(R.id.edt_edit_nome)
        edtSobrenome = findViewById(R.id.edt_edit_sobrenome)
        edtEmail = findViewById(R.id.edt_edit_email)
        edtNasc = findViewById(R.id.edt_edit_nasc)
        edtPeso = findViewById(R.id.edt_edit_peso)
        edtAltura = findViewById(R.id.edt_edit_altura)
        edtSenhaConfirm = findViewById(R.id.edt_confirma_senha_atual)

        val btnSalvar = findViewById<Button>(R.id.btn_salvar_edicao)

        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(UsuarioService::class.java)

        carregarDadosAtuais()

        btnSalvar.setOnClickListener {
            salvarAlteracoes()
        }
    }

    private fun carregarDadosAtuais() {
        lifecycleScope.launch {
            try {
                val res = service.getPerfil(usuarioId)
                if (res.isSuccessful) {
                    val user = res.body()
                    if (user != null) {
                        edtNome.setText(user.nome)
                        edtEmail.setText(user.email)
                        edtSobrenome.setText(user.sobrenome ?: "")
                        edtNasc.setText(user.data_nascimento ?: "")
                        edtPeso.setText(user.peso?.toString() ?: "")
                        edtAltura.setText(user.altura?.toString() ?: "")
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditarDadosActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvarAlteracoes() {
        val senhaConfirmacao = edtSenhaConfirm.text.toString()
        if (senhaConfirmacao.isEmpty()) {
            Toast.makeText(this, "Digite sua senha atual para confirmar", Toast.LENGTH_LONG).show()
            return
        }

        val request = UsuarioUpdateRequest(
            nome = edtNome.text.toString(),
            email = edtEmail.text.toString(),
            sobrenome = edtSobrenome.text.toString(),
            data_nascimento = edtNasc.text.toString(),
            peso = edtPeso.text.toString().toDoubleOrNull() ?: 0.0,
            altura = edtAltura.text.toString().toDoubleOrNull() ?: 0.0,
            senha_atual = senhaConfirmacao
        )

        lifecycleScope.launch {
            try {
                val res = service.atualizarPerfil(usuarioId, request)
                if (res.isSuccessful) {
                    Toast.makeText(this@EditarDadosActivity, "Perfil Atualizado!", Toast.LENGTH_SHORT).show()
                    finish()
                } else if (res.code() == 401) {
                    Toast.makeText(this@EditarDadosActivity, "Senha incorreta!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EditarDadosActivity, "Erro ao atualizar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditarDadosActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
            }
        }
    }
}