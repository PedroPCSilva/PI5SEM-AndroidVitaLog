package com.empresa.vitalogfinal.view.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.empresa.vitalogfinal.LoginActivity
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.view.menu.AguaFragment // Se seu arquivo chamar AguaActivity, renomeie para Fragment
import com.empresa.vitalogfinal.view.menu.DiarioFragment // Crie se não existir
import com.empresa.vitalogfinal.view.menu.meta.MetasActivity
import com.empresa.vitalogfinal.view.menu.perfil.PerfilActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // 1. Configura a Toolbar (Menu Topo)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_principal)
        setSupportActionBar(toolbar)

        // 2. Configura o BottomNavigation (Menu Baixo)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Carrega o primeiro fragmento por padrão (Diário)
        if (savedInstanceState == null) {
            trocarFragmento(DiarioFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_diario -> {
                    trocarFragmento(DiarioFragment())
                    true
                }
                R.id.nav_agua -> {
                    trocarFragmento(AguaFragment()) // Certifique-se que é um Fragment
                    true
                }
                R.id.nav_relatorio -> {
                    trocarFragmento(RelatorioFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Lógica para inflar o menu superior (Perfil, Sair, etc)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_topo, menu)
        return true
    }

    // Lógica para cliques no menu superior
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_perfil -> {
                startActivity(Intent(this, PerfilActivity::class.java))
                true
            }
            R.id.action_metas -> {
                startActivity(Intent(this, MetasActivity::class.java))
                true
            }
            R.id.action_sair -> {
                fazerLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun trocarFragmento(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun fazerLogout() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}