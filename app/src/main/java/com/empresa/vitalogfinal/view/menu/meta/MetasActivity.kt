package com.empresa.vitalogfinal.view.menu.meta

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.model.meta.Meta
import com.empresa.vitalogfinal.repository.MetaRepository
import com.empresa.vitalogfinal.service.MetaService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MetasActivity : AppCompatActivity() {

    private lateinit var adapter: MetasAdapter
    private lateinit var repository: MetaRepository
    private var usuarioId = 0
    private var dataHoje = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metas)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        usuarioId = prefs.getInt("user_id", 0)
        dataHoje = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val recycler = findViewById<RecyclerView>(R.id.recycler_metas)
        recycler.layoutManager = LinearLayoutManager(this)

        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        repository = MetaRepository(retrofit.create(MetaService::class.java))

        // Agora permitimos clicar mesmo se for ID 0
        adapter = MetasAdapter(emptyList()) { meta ->
            abrirDialogEditar(meta)
        }
        recycler.adapter = adapter

        carregarMetas()
    }

    private fun carregarMetas() {
        lifecycleScope.launch {
            try {
                val lista = repository.listar(usuarioId, dataHoje)
                if (lista.isNotEmpty()) {
                    adapter.updateList(lista)
                } else {
                    mostrarMetasPadrao()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mostrarMetasPadrao()
            }
        }
    }

    private fun mostrarMetasPadrao() {
        val listaPadrao = listOf(
            Meta(0, usuarioId, "caloria", 2000.0, null),
            Meta(0, usuarioId, "hidratacao", 2500.0, null)
        )
        adapter.updateList(listaPadrao)
    }

    private fun abrirDialogEditar(meta: Meta) {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Novo valor"
        input.setText(meta.meta.toString())

        AlertDialog.Builder(this)
            .setTitle("Definir Meta")
            .setMessage("Valor para ${meta.tipo} (hoje)")
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val novoValor = input.text.toString().toDoubleOrNull()
                if (novoValor != null && novoValor > 0) {
                    processarSalvamento(meta, novoValor)
                } else {
                    Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun processarSalvamento(meta: Meta, valor: Double) {
        lifecycleScope.launch {
            val sucesso: Boolean

            if (meta.id == 0) {
                // SE ID É 0, SIGNIFICA QUE NÃO EXISTE NO BANCO -> CRIAR (POST)
                sucesso = repository.criar(usuarioId, meta.tipo, valor, dataHoje)
            } else {
                // SE TEM ID, JÁ EXISTE -> ATUALIZAR (PUT)
                sucesso = repository.atualizar(meta.id, valor)
            }

            if (sucesso) {
                Toast.makeText(this@MetasActivity, "Meta salva com sucesso!", Toast.LENGTH_SHORT).show()
                carregarMetas() // Recarrega para pegar o ID real do banco
            } else {
                Toast.makeText(this@MetasActivity, "Erro ao salvar meta.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}