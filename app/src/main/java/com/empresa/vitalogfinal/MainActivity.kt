package com.empresa.vitalogfinal // Mantenha seu package original

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate // <--- IMPORTANTE: Adicione este import
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnCadastro : Button
    private lateinit var btnLogin : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        // --- ADICIONE ESTA LINHA AQUI ---
        // Isso força o app a ignorar o tema escuro do celular e usar sempre o claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // -------------------------------

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnCadastro = findViewById(R.id.btnCadastro)

        btnCadastro.setOnClickListener {
            var intent = Intent(this@MainActivity, CadastroActivity::class.java)
            startActivity(intent)
        }

        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            var intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}