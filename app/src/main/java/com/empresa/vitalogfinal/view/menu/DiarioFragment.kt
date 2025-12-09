package com.empresa.vitalogfinal.view.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.model.diario.GrupoModel
import com.empresa.vitalogfinal.repository.DiarioRepository
import com.empresa.vitalogfinal.repository.MetaRepository
import com.empresa.vitalogfinal.service.DiarioService
import com.empresa.vitalogfinal.service.MetaService
import com.empresa.vitalogfinal.view.menu.diario.CriarGrupoActivity
import com.empresa.vitalogfinal.view.menu.diario.DetalhesGrupoActivity
import com.empresa.vitalogfinal.view.menu.diario.DiarioAdapter
import com.empresa.vitalogfinal.view.menu.ui.DiarioViewModel
import com.empresa.vitalogfinal.view.menu.ui.DiarioViewModelFactory // <--- O IMPORT AGORA VAI FUNCIONAR
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiarioFragment : Fragment() {

    private lateinit var viewModel: DiarioViewModel
    private lateinit var adapter: DiarioAdapter
    private var dataAtual = Calendar.getInstance()
    private var usuarioId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_diario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        usuarioId = prefs.getInt("user_id", 0)

        // Configura Views
        val btnPrev = view.findViewById<ImageButton>(R.id.btn_prev_day)
        val btnNext = view.findViewById<ImageButton>(R.id.btn_next_day)
        val txtDate = view.findViewById<TextView>(R.id.txt_selected_date)
        val btnAddGrupo = view.findViewById<Button>(R.id.btn_add_grupo)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_diario)
        val txtResumo = view.findViewById<TextView>(R.id.txt_resumo_calorias)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_calorias)

        // Configura Retrofit e ViewModel
        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val diarioRepo = DiarioRepository(retrofit.create(DiarioService::class.java))
        val metaRepo = MetaRepository(retrofit.create(MetaService::class.java))

        // Factory agora é reconhecida corretamente
        viewModel = ViewModelProvider(
            this,
            DiarioViewModelFactory(diarioRepo, metaRepo)
        )[DiarioViewModel::class.java]

        // Configura Lista
        adapter = DiarioAdapter(emptyList()) { grupo ->
            abrirDetalhesGrupo(grupo)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        // Observadores
        viewModel.grupos.observe(viewLifecycleOwner) { lista ->
            adapter.updateList(lista)
        }

        viewModel.statusCalorias.observe(viewLifecycleOwner) { (consumido, meta) ->
            val porcentagem = if (meta > 0) (consumido / meta) * 100 else 0.0
            progressBar.progress = porcentagem.toInt()

            val cStr = String.format("%.0f", consumido)
            val mStr = String.format("%.0f", meta)
            txtResumo.text = "$cStr / $mStr kcal"

            if (consumido > meta) {
                progressBar.progressTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.RED)
            } else {
                progressBar.progressTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF9800"))
            }
        }

        atualizarTextoData(txtDate)
        carregarDiaAtual()

        btnPrev.setOnClickListener {
            dataAtual.add(Calendar.DAY_OF_YEAR, -1)
            atualizarTextoData(txtDate)
            carregarDiaAtual()
        }

        btnNext.setOnClickListener {
            dataAtual.add(Calendar.DAY_OF_YEAR, 1)
            atualizarTextoData(txtDate)
            carregarDiaAtual()
        }

        btnAddGrupo.setOnClickListener {
            val intent = Intent(requireContext(), CriarGrupoActivity::class.java)
            startActivityForResult(intent, 100)
        }
    }

    private fun atualizarTextoData(view: TextView) {
        val hoje = Calendar.getInstance()
        val formato = SimpleDateFormat("dd MMM", Locale("pt", "BR"))

        if (dataAtual.get(Calendar.YEAR) == hoje.get(Calendar.YEAR) &&
            dataAtual.get(Calendar.DAY_OF_YEAR) == hoje.get(Calendar.DAY_OF_YEAR)) {
            view.text = "Hoje, ${formato.format(dataAtual.time)}"
        } else {
            view.text = formato.format(dataAtual.time)
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDiaAtual()
    }

    private fun carregarDiaAtual() {
        if (usuarioId != 0) {
            val formatoApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dataString = formatoApi.format(dataAtual.time)

            lifecycleScope.launch {
                viewModel.carregarDiario(usuarioId, dataString)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val nomeGrupo = data?.getStringExtra("nome_grupo") ?: return
            lifecycleScope.launch {
                val novoGrupo = viewModel.criarGrupo(usuarioId, nomeGrupo)
                if (novoGrupo != null) {
                    Toast.makeText(requireContext(), "Grupo criado!", Toast.LENGTH_SHORT).show()
                    carregarDiaAtual()
                } else {
                    Toast.makeText(requireContext(), "Erro ao criar grupo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun abrirDetalhesGrupo(grupo: GrupoModel) {
        val intent = Intent(requireContext(), DetalhesGrupoActivity::class.java)
        intent.putExtra("grupoId", grupo.id)
        intent.putExtra("grupoNome", grupo.nome)
        startActivity(intent)
    }
}