package com.empresa.vitalogfinal

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.model.usuario.CadastroRequest
import com.empresa.vitalogfinal.model.usuario.CadastroResponse
import com.empresa.vitalogfinal.service.UsuarioService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CadastroActivity : AppCompatActivity() {
    private lateinit var btnVoltar : Button
    private lateinit var edtNome : EditText
    private lateinit var edtEmail : EditText
    private lateinit var edtSenha : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnVoltar = findViewById(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            finish()
        }

        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        edtNome = findViewById(R.id.edtNome)
        edtEmail = findViewById(R.id.edtEmail)
        edtSenha = findViewById(R.id.edtSenha)

        btnCadastrar.setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            // Validação simples
            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = CadastroRequest(nome, email, senha)
            val cred = Credenciais()
            val retrofit = Retrofit.Builder()
                .baseUrl(cred.ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build()


            val usuarioService = retrofit.create(UsuarioService::class.java)

            usuarioService.cadastro(request).enqueue(object : Callback<CadastroResponse> {
                override fun onResponse(
                    call: Call<CadastroResponse>,
                    response: Response<CadastroResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        Toast.makeText(
                            this@CadastroActivity,
                            body?.message ?: "Cadastro realizado!",
                            Toast.LENGTH_LONG
                        ).show()
                        finish() // volta para a tela anterior
                    } else if (response.code() == 409) {
                        Toast.makeText(this@CadastroActivity, "Email já cadastrado!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@CadastroActivity, "Erro no cadastro!", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<CadastroResponse>, t: Throwable) {
                    Toast.makeText(this@CadastroActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}