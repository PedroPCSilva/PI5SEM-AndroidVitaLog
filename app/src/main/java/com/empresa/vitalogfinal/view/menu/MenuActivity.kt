package com.empresa.vitalogfinal.view.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast // <--- ADICIONADO
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.view.menu.meta.MetasActivity
import com.empresa.vitalogfinal.view.menu.perfil.PerfilActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MenuActivity : AppCompatActivity() {

    private val FRAG_TAG_DIARIO = "frag_diario"
    private val FRAG_TAG_AGUA = "frag_agua"
    private val FRAG_TAG_REL = "frag_rel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val usuarioId = prefs.getInt("user_id", 0) // <--- Corrigido: Adicionado 'val'

        if (usuarioId == 0) {
            Toast.makeText(this, "ERRO CRÍTICO: Usuário ID é 0. Faça Login novamente.", Toast.LENGTH_LONG).show()
        } else {

        }


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            showFragment(FRAG_TAG_DIARIO)
            bottomNav.selectedItemId = R.id.nav_diario
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_diario -> showFragment(FRAG_TAG_DIARIO)
                R.id.nav_agua -> showFragment(FRAG_TAG_AGUA)
                R.id.nav_relatorio -> showFragment(FRAG_TAG_REL)
            }
            true
        }
    }

    private fun showFragment(tag: String) {
        val fm = supportFragmentManager
        val containerId = R.id.menu_fragment_container
        val transaction = fm.beginTransaction()

        val tags = listOf(FRAG_TAG_DIARIO, FRAG_TAG_AGUA, FRAG_TAG_REL)
        for (t in tags) {
            val f = fm.findFragmentByTag(t)
            if (f != null && f.isAdded) transaction.hide(f)
        }

        var fragmentToShow = fm.findFragmentByTag(tag)
        if (fragmentToShow == null) {
            fragmentToShow = when (tag) {
                FRAG_TAG_DIARIO -> DiarioFragment()
                FRAG_TAG_AGUA -> AguaFragment()
                FRAG_TAG_REL -> RelatorioFragment()
                else -> DiarioFragment()
            }
            transaction.add(containerId, fragmentToShow, tag)
        } else {
            transaction.show(fragmentToShow)
        }
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_topo, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_metas -> {
                startActivity(Intent(this, MetasActivity::class.java))
                true
            }
            R.id.action_perfil -> {

                startActivity(Intent(this, PerfilActivity::class.java))
                true
            }


            R.id.action_sair -> {
                getSharedPreferences("app_prefs", MODE_PRIVATE).edit().clear().apply()
                finish()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}