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
import com.empresa.vitalogfinal.model.meta.MetaModel
import com.empresa.vitalogfinal.repository.MetaRepository
import com.empresa.vitalogfinal.service.MetaService
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MetasActivity : AppCompatActivity() {

    private lateinit var adapter: MetasAdapter
    private lateinit var repository: MetaRepository
    private var usuarioId = 0
    private val dataHoje = LocalDate.now().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metas)
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        usuarioId = prefs.getInt("user_id", 0)

        val recycler = findViewById<RecyclerView>(R.id.recycler_metas)
        recycler.layoutManager = LinearLayoutManager(this)


        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        repository = MetaRepository(retrofit.create(MetaService::class.java))

        adapter = MetasAdapter(emptyList()) { meta ->
            mostrarDialogEditar(meta)
        }
        recycler.adapter = adapter

        carregarMetas()
    }

    private fun carregarMetas() {
        lifecycleScope.launch {

            val metasSalvas = repository.listar(usuarioId, dataHoje)


            val listaFinal = mutableListOf<MetaModel>()

            val metaCaloria = metasSalvas.find { it.tipo == "caloria" }
            if (metaCaloria != null) {
                listaFinal.add(metaCaloria)
            } else {

                listaFinal.add(MetaModel(0, usuarioId, "caloria", 2000.0))
            }


            val metaAgua = metasSalvas.find { it.tipo == "hidratacao" }
            if (metaAgua != null) {
                listaFinal.add(metaAgua)
            } else {

                listaFinal.add(MetaModel(0, usuarioId, "hidratacao", 2000.0))
            }

            adapter.updateList(listaFinal)
        }
    }

    private fun mostrarDialogEditar(meta: MetaModel) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Definir Meta")
        builder.setMessage("Digite o novo valor para ${if (meta.tipo == "caloria") "Calorias" else "Água"}:")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.setText(meta.meta.toString())
        builder.setView(input)

        builder.setPositiveButton("Salvar") { _, _ ->
            val valor = input.text.toString().toDoubleOrNull()
            if (valor != null && valor > 0) {
                salvarMeta(meta.copy(meta = valor))
            } else {
                Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun salvarMeta(meta: MetaModel) {
        lifecycleScope.launch {
            val sucesso = repository.salvar(meta)
            if (sucesso) {
                Toast.makeText(this@MetasActivity, "Meta atualizada!", Toast.LENGTH_SHORT).show()
                carregarMetas()
            } else {
                Toast.makeText(this@MetasActivity, "Erro ao salvar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}