package com.empresa.vitalogfinal.view.menu.perfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.empresa.vitalogfinal.LoginActivity // Ajuste se seu Login estiver em outra pasta
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.service.UsuarioService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PerfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // IDs baseados no seu XML "activity_perfil.xml"
        val txtNome = findViewById<TextView>(R.id.txt_perfil_nome)
        val txtEmail = findViewById<TextView>(R.id.txt_perfil_email)
        val txtPeso = findViewById<TextView>(R.id.txt_perfil_peso)
        val txtAltura = findViewById<TextView>(R.id.txt_perfil_altura)
        val txtIdade = findViewById<TextView>(R.id.txt_perfil_nasc) // Usado para idade

        val btnEditar = findViewById<Button>(R.id.btn_editar_perfil)
        val btnAlterarSenha = findViewById<Button>(R.id.btn_alterar_senha)
        val btnSair = findViewById<Button>(R.id.btn_sair_app) // ID corrigido

        carregarDadosUsuario(txtNome, txtEmail, txtPeso, txtAltura, txtIdade)

        btnAlterarSenha.setOnClickListener {
            // Garante que importou a activity correta no topo ou está no mesmo pacote
            val intent = Intent(this, AlterarSenhaActivity::class.java)
            startActivity(intent)
        }

        btnEditar.setOnClickListener {
            val intent = Intent(this, EditarDadosActivity::class.java)
            startActivity(intent)
        }

        btnSair.setOnClickListener {
            fazerLogout()
        }
    }

    private fun carregarDadosUsuario(
        txtNome: TextView,
        txtEmail: TextView,
        txtPeso: TextView,
        txtAltura: TextView,
        txtIdade: TextView
    ) {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val id = prefs.getInt("user_id", 0)

        if (id == 0) return

        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(UsuarioService::class.java)

        lifecycleScope.launch {
            try {
                val response = service.getPerfil(id)
                if (response.isSuccessful) {
                    val usuario = response.body()
                    if (usuario != null) {
                        txtNome.text = usuario.nome
                        txtEmail.text = usuario.email

                        // Exibindo dados extras (se existirem no seu objeto Usuario)
                        // Converta para String e trate nulos
                        txtPeso.text = "${usuario.peso ?: "--"} kg"
                        txtAltura.text = "${usuario.altura ?: "--"} m"

                        // Calculo simples de idade se houver data de nascimento
                        txtIdade.text = calcularIdade(usuario.data_nascimento)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                txtNome.text = "Erro de conexão"
            }
        }
    }

    private fun calcularIdade(dataNascString: String?): String {
        if (dataNascString.isNullOrEmpty()) return "--"
        return try {
            // Supondo que a data venha como "yyyy-MM-dd" ou "dd/MM/yyyy"
            // Ajuste o padrão conforme seu banco de dados
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dataNasc = sdf.parse(dataNascString)

            if (dataNasc != null) {
                val hoje = Calendar.getInstance()
                val nasc = Calendar.getInstance()
                nasc.time = dataNasc

                var idade = hoje.get(Calendar.YEAR) - nasc.get(Calendar.YEAR)
                if (hoje.get(Calendar.DAY_OF_YEAR) < nasc.get(Calendar.DAY_OF_YEAR)) {
                    idade--
                }
                "$idade anos"
            } else {
                "--"
            }
        } catch (e: Exception) {
            "--"
        }
    }

    private fun fazerLogout() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        // Verifique se sua LoginActivity está na raiz ou em view.Login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}