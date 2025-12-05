package com.empresa.vitalogfinal.view.menu.diario

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.model.diario.FoodModel
import com.empresa.vitalogfinal.repository.GrupoRepository
import com.empresa.vitalogfinal.service.GrupoService
import com.empresa.vitalogfinal.view.menu.ui.DetalhesGrupoViewModel
import com.empresa.vitalogfinal.view.menu.ui.DetalhesGrupoViewModelFactory
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetalhesGrupoActivity : AppCompatActivity() {

    private lateinit var viewModel: DetalhesGrupoViewModel
    private lateinit var adapter: AlimentosAdapter

    private var grupoId = 0
    private var grupoNome = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_grupo)

        // 1. Pegar dados da intent
        grupoId = intent.getIntExtra("grupoId", 0)
        grupoNome = intent.getStringExtra("grupoNome") ?: ""

        val txtNomeGrupo = findViewById<TextView>(R.id.txt_nome_grupo)
        val recycler = findViewById<RecyclerView>(R.id.recycler_alimentos)
        val btnAdd = findViewById<Button>(R.id.btn_add_alimento)
        val btnApagarGrupo = findViewById<Button>(R.id.btn_apagar_grupo)
        val btnVoltar = findViewById<Button>(R.id.btn_voltar)

        txtNomeGrupo.text = grupoNome


        btnVoltar.setOnClickListener {
            finish()
        }

        adapter = AlimentosAdapter(emptyList()) { alimento ->
            confirmarExclusaoAlimento(alimento)
        }

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        viewModel = ViewModelProvider(
            this,
            DetalhesGrupoViewModelFactory(GrupoRepository(retrofit.create(GrupoService::class.java)))
        )[DetalhesGrupoViewModel::class.java]

        viewModel.alimentos.observe(this) { lista ->
            adapter.update(lista)
        }

        btnAdd.setOnClickListener {
            adicionarAlimento()
        }

        btnApagarGrupo.setOnClickListener {
            confirmarExclusaoGrupo()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDados()
    }

    private fun carregarDados() {
        if (grupoId != 0) {
            lifecycleScope.launch {
                viewModel.carregar(grupoId)
            }
        }
    }

    private fun adicionarAlimento() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val usuarioId = prefs.getInt("user_id", 0)

        if (usuarioId == 0) {
            Toast.makeText(this, "Erro: Usuário não identificado.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, PesquisaAlimentoActivity::class.java)
        intent.putExtra("usuarioId", usuarioId)
        intent.putExtra("grupoId", grupoId)
        startActivity(intent)
    }

    private fun confirmarExclusaoAlimento(alimento: FoodModel) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Alimento")
            .setMessage("Deseja remover '${alimento.nome}' deste grupo?")
            .setPositiveButton("Apagar") { _, _ ->
                executarExclusaoAlimento(alimento.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun executarExclusaoAlimento(alimentoId: Int) {
        lifecycleScope.launch {
            try {
                val sucesso = viewModel.apagarAlimento(alimentoId)
                if (sucesso) {
                    Toast.makeText(this@DetalhesGrupoActivity, "Alimento removido!", Toast.LENGTH_SHORT).show()
                    // Recarrega a lista para o alimento sumir da tela
                    carregarDados()
                } else {
                    Toast.makeText(this@DetalhesGrupoActivity, "Erro ao remover alimento.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@DetalhesGrupoActivity, "Erro de conexão.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmarExclusaoGrupo() {
        AlertDialog.Builder(this)
            .setTitle("Excluir Grupo")
            .setMessage("Tem certeza que deseja apagar o grupo '$grupoNome'? Todos os alimentos serão perdidos.")
            .setPositiveButton("Sim, Apagar") { _, _ ->
                executarExclusaoGrupo()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun executarExclusaoGrupo() {
        lifecycleScope.launch {
            try {
                val sucesso = viewModel.apagarGrupo(grupoId)
                if (sucesso) {
                    Toast.makeText(this@DetalhesGrupoActivity, "Grupo removido!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@DetalhesGrupoActivity, "Erro ao apagar grupo.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@DetalhesGrupoActivity, "Erro de conexão.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}