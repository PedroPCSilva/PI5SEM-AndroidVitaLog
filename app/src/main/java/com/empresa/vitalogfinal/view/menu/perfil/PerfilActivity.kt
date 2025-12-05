package com.empresa.vitalogfinal.view.menu.perfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.empresa.vitalogfinal.LoginActivity
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.service.UsuarioService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.empresa.vitalogfinal.AlterarSenhaActivity

class PerfilActivity : AppCompatActivity() {

    private lateinit var txtNome: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtPeso: TextView
    private lateinit var txtAltura: TextView
    private lateinit var txtNasc: TextView

    private var usuarioId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        usuarioId = prefs.getInt("user_id", 0)

        txtNome = findViewById(R.id.txt_perfil_nome)
        txtEmail = findViewById(R.id.txt_perfil_email)
        txtPeso = findViewById(R.id.txt_perfil_peso)
        txtAltura = findViewById(R.id.txt_perfil_altura)
        txtNasc = findViewById(R.id.txt_perfil_nasc)

        val btnEditar = findViewById<Button>(R.id.btn_editar_perfil)
        val btnAlterarSenha = findViewById<Button>(R.id.btn_alterar_senha) // Novo botão
        val btnSair = findViewById<Button>(R.id.btn_sair_app)


        btnEditar.setOnClickListener {
            startActivity(Intent(this, EditarDadosActivity::class.java))
        }

        btnAlterarSenha.setOnClickListener {
            startActivity(Intent(this, AlterarSenhaActivity::class.java))
        }

        btnSair.setOnClickListener {
            prefs.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        carregarDados()
    }

    override fun onResume() {
        super.onResume()
        carregarDados()
    }

    private fun carregarDados() {
        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(UsuarioService::class.java)

        lifecycleScope.launch {
            try {
                val response = service.getPerfil(usuarioId)
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        txtNome.text = "${user.nome} ${user.sobrenome ?: ""}"
                        txtEmail.text = user.email

                        txtPeso.text = if (user.peso != null) "${user.peso} kg" else "--"
                        txtAltura.text = if (user.altura != null) "${user.altura} m" else "--"
                        txtNasc.text = user.data_nascimento ?: "--/--/--"
                    }
                } else {
                    Toast.makeText(this@PerfilActivity, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PerfilActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
            }
        }
    }
}