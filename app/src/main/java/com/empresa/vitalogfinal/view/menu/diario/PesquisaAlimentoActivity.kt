package com.empresa.vitalogfinal.view.menu.diario

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.model.alimento.Alimento
import com.empresa.vitalogfinal.repository.AlimentoRepository
import com.empresa.vitalogfinal.service.AlimentoService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PesquisaAlimentoActivity : AppCompatActivity() {

    private lateinit var adapter: PesquisaAdapter
    private lateinit var repository: AlimentoRepository

    private var usuarioId = 0
    private var grupoId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisa_alimento)

        usuarioId = intent.getIntExtra("usuarioId", 0)
        grupoId = intent.getIntExtra("grupoId", 0)

        val edtBusca = findViewById<EditText>(R.id.edt_busca_alimento)
        val btnPesquisar = findViewById<Button>(R.id.btn_pesquisar)
        val btnNovo = findViewById<Button>(R.id.btn_add_novo_manual)
        val recycler = findViewById<RecyclerView>(R.id.recycler_pesquisa)

        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        repository = AlimentoRepository(retrofit.create(AlimentoService::class.java))

        adapter = PesquisaAdapter(emptyList()) { alimentoSelecionado ->
            irParaFormulario(alimentoSelecionado)
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        btnPesquisar.setOnClickListener {
            val termo = edtBusca.text.toString().trim()
            if (termo.isNotEmpty()) {
                lifecycleScope.launch {
                    val resultados = repository.pesquisar(termo)
                    if (resultados.isEmpty()) {
                        Toast.makeText(this@PesquisaAlimentoActivity, "Nenhum alimento encontrado", Toast.LENGTH_SHORT).show()
                    }
                    adapter.updateList(resultados)
                }
            } else {
                Toast.makeText(this, "Digite algo para buscar", Toast.LENGTH_SHORT).show()
            }
        }

        btnNovo.setOnClickListener {
            irParaFormulario(null)
        }
    }

    private fun irParaFormulario(alimento: Alimento?) {
        val intent = Intent(this, FormularioAlimentoActivity::class.java)
        intent.putExtra("usuarioId", usuarioId)
        intent.putExtra("grupoId", grupoId)

        if (alimento != null) {
            intent.putExtra("nome", alimento.nome)
            intent.putExtra("caloria_base", alimento.caloria)
            intent.putExtra("porcao_base", alimento.porcao)
        }
        startActivity(intent)
        finish()
    }
}