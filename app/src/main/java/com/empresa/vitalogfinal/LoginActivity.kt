package com.empresa.vitalogfinal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech // Importante
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton // Importante
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.model.usuario.LoginResponse
import com.empresa.vitalogfinal.model.usuario.Usuario
import com.empresa.vitalogfinal.service.UsuarioService
import com.empresa.vitalogfinal.view.menu.MenuActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale // Importante

// Adicionei ", TextToSpeech.OnInitListener" na declaração da classe
class LoginActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var btnVoltar: Button
    private lateinit var btnLogin: Button
    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText

    // Variáveis para Acessibilidade
    private lateinit var btnAcessibilidade: ImageButton
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa componentes visuais
        btnVoltar = findViewById(R.id.btnVoltar)
        btnLogin = findViewById(R.id.btnLogin)
        edtEmail = findViewById(R.id.edtEmail)
        edtSenha = findViewById(R.id.edtSenha)
        btnAcessibilidade = findViewById(R.id.btnAcessibilidade) // Novo botão

        // Inicializa o TTS
        tts = TextToSpeech(this, this)

        // Configura cliques
        btnVoltar.setOnClickListener { finish() }

        btnLogin.setOnClickListener {
            realizarLogin()
        }

        // Clique do botão de acessibilidade
        btnAcessibilidade.setOnClickListener {
            val textoParaFalar = "Ecrã de Login. Por favor, digite o seu email no primeiro campo e a sua senha no segundo campo. Depois, clique no botão Entrar."
            falarTexto(textoParaFalar)
        }
    }

    // --- Lógica de Acessibilidade (TTS) ---

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Tenta configurar para Português
            val result = tts.setLanguage(Locale("pt", "PT")) // Tenta PT-PT primeiro

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Se não tiver PT-PT, tenta PT-BR
                val resultBR = tts.setLanguage(Locale("pt", "BR"))
                if (resultBR == TextToSpeech.LANG_MISSING_DATA || resultBR == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Idioma Português não suportado pelo aparelho.")
                }
            }
        } else {
            Log.e("TTS", "Falha na inicialização do áudio.")
        }
    }

    private fun falarTexto(texto: String) {
        if (::tts.isInitialized) {
            // O parâmetro QUEUE_FLUSH interrompe a fala anterior para falar a nova imediatamente
            tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    override fun onDestroy() {
        // Encerra o serviço de fala ao fechar a tela para economizar bateria
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    // --- Fim da Lógica de Acessibilidade ---

    val cred = Credenciais()
    private fun realizarLogin() {
        val email = edtEmail.text.toString().trim()
        val senha = edtSenha.text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha email e senha!", Toast.LENGTH_SHORT).show()
            // Dica extra: Falar o erro também ajuda na acessibilidade
            falarTexto("Erro. Preencha email e senha.")
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
                    val body = response.body()
                    val usuario: Usuario? = body?.user

                    if (usuario != null) {
                        Toast.makeText(this@LoginActivity, "Bem-vindo!", Toast.LENGTH_LONG).show()
                        falarTexto("Login realizado com sucesso. Bem-vindo.")

                        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        prefs.edit()
                            .putInt("user_id", usuario.id)
                            .putString("user_nome", usuario.nome)
                            .putString("user_email", usuario.email)
                            .apply()

                        val it = Intent(this@LoginActivity, MenuActivity::class.java)
                        startActivity(it)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, body?.message ?: "Resposta inesperada", Toast.LENGTH_SHORT).show()
                        falarTexto("Erro no login. Verifique os dados.")
                    }
                } else {
                    when (response.code()) {
                        401 -> {
                            Toast.makeText(this@LoginActivity, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
                            falarTexto("Credenciais inválidas.")
                        }
                        else -> {
                            val erro = response.errorBody()?.string()
                            Toast.makeText(this@LoginActivity, "Erro ${response.code()}: ${erro ?: "Erro no servidor"}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Falha ao conectar: ${t.message}", Toast.LENGTH_LONG).show()
                Log.d("Retrofit",t.message.toString())
                falarTexto("Falha na conexão com a internet.")
            }
        })
    }

    private fun enableEdgeToEdge() {
        // Implementação vazia mantida do seu código original
    }
}