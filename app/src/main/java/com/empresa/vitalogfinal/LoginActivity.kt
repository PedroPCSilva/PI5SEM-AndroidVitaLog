package com.empresa.vitalogfinal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.model.usuario.LoginResponse
import com.empresa.vitalogfinal.model.usuario.Usuario
import com.empresa.vitalogfinal.service.EmailRequest
import com.empresa.vitalogfinal.service.GenericResponse
import com.empresa.vitalogfinal.service.UsuarioService
import com.empresa.vitalogfinal.view.menu.MenuActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class LoginActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var btnVoltar: Button
    private lateinit var btnLogin: Button
    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var txtEsqueciSenha: TextView

    private lateinit var btnAcessibilidade: ImageButton
    private lateinit var tts: TextToSpeech

    val cred = Credenciais()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnVoltar = findViewById(R.id.btnVoltar)
        btnLogin = findViewById(R.id.btnLogin)
        edtEmail = findViewById(R.id.edtEmail)
        edtSenha = findViewById(R.id.edtSenha)
        btnAcessibilidade = findViewById(R.id.btnAcessibilidade)
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha)

        tts = TextToSpeech(this, this)

        btnVoltar.setOnClickListener { finish() }

        btnLogin.setOnClickListener { realizarLogin() }

        txtEsqueciSenha.setOnClickListener { abrirDialogoRecuperacao() }

        btnAcessibilidade.setOnClickListener {
            val texto = "Ecrã de Login. Se esqueceu sua senha, clique na opção 'Esqueci minha senha' abaixo do campo de senha."
            falarTexto(texto)
        }
    }

    private fun abrirDialogoRecuperacao() {
        val inputEmail = EditText(this)
        inputEmail.hint = "Digite seu e-mail cadastrado"
        inputEmail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        AlertDialog.Builder(this)
            .setTitle("Recuperar Acesso")
            .setMessage("Enviaremos uma nova senha para o seu e-mail.")
            .setView(inputEmail)
            .setPositiveButton("Enviar") { _, _ ->
                val email = inputEmail.text.toString().trim()
                if (email.isNotEmpty()) {
                    enviarTokenEmail(email)
                } else {
                    Toast.makeText(this, "Digite um e-mail válido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enviarTokenEmail(email: String) {
        falarTexto("Enviando solicitação, aguarde.")

        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UsuarioService::class.java)
        val call = service.esqueciSenha(EmailRequest(email))

        call.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Senha enviada! Verifique seu e-mail.", Toast.LENGTH_LONG).show()
                    falarTexto("Sucesso. Verifique seu e-mail.")
                } else {
                    Toast.makeText(this@LoginActivity, "E-mail não encontrado.", Toast.LENGTH_SHORT).show()
                    falarTexto("E-mail não encontrado no sistema.")
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro de conexão.", Toast.LENGTH_SHORT).show()
                falarTexto("Erro de conexão com o servidor.")
            }
        })
    }

    private fun realizarLogin() {
        val email = edtEmail.text.toString().trim()
        val senha = edtSenha.text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha email e senha!", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UsuarioService::class.java)
        val call = service.login(email, senha)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val usuario = response.body()?.user
                    if (usuario != null) {
                        Toast.makeText(this@LoginActivity, "Bem-vindo!", Toast.LENGTH_LONG).show()
                        falarTexto("Login realizado com sucesso.")

                        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        prefs.edit()
                            .putInt("user_id", usuario.id)
                            .putString("user_nome", usuario.nome)
                            .putString("user_email", usuario.email)
                            .apply()

                        startActivity(Intent(this@LoginActivity, MenuActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Erro no login", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
                    falarTexto("Senha ou email incorretos.")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Falha ao conectar", Toast.LENGTH_LONG).show()
                falarTexto("Falha na conexão.")
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("pt", "PT"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(Locale("pt", "BR"))
            }
        }
    }

    private fun falarTexto(texto: String) {
        if (::tts.isInitialized) {
            tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) { tts.stop(); tts.shutdown() }
        super.onDestroy()
    }
}