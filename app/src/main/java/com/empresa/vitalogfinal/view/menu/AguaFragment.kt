package com.empresa.vitalogfinal.view.menu

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton // Importante
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.model.hidratacao.HidratacaoModel
import com.empresa.vitalogfinal.repository.HidratacaoRepository
import com.empresa.vitalogfinal.repository.MetaRepository
import com.empresa.vitalogfinal.service.HidratacaoService
import com.empresa.vitalogfinal.service.MetaService
import com.empresa.vitalogfinal.view.menu.agua.AguaAdapter
import com.empresa.vitalogfinal.view.menu.ui.AguaViewModel
import com.empresa.vitalogfinal.view.menu.ui.AguaViewModelFactory
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class AguaFragment : Fragment() {

    private lateinit var viewModel: AguaViewModel
    private lateinit var adapter: AguaAdapter
    private var selectedDate: LocalDate = LocalDate.now()
    private var usuarioId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_agua, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        usuarioId = prefs.getInt("user_id", 0)

        val txtData = view.findViewById<TextView>(R.id.txt_agua_data)
        val txtTotal = view.findViewById<TextView>(R.id.txt_agua_total)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_agua)
        val btnAdd = view.findViewById<Button>(R.id.btn_add_agua)

        // Atualizado para ImageButton
        val btnPrev = view.findViewById<ImageButton>(R.id.btn_agua_prev)
        val btnNext = view.findViewById<ImageButton>(R.id.btn_agua_next)

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_agua)
        val txtStatusMeta = view.findViewById<TextView>(R.id.txt_status_meta)

        // Configurar botões rápidos (se existirem no XML)
        // DICA: Adicione android:id="@+id/btn_200ml" no XML se não tiver
        try {
            val btn200 = view.findViewById<Button>(R.id.btn_200ml) // Supondo ID
            val btn500 = view.findViewById<Button>(R.id.btn_500ml) // Supondo ID

            // Se o ID não existir, vai cair no catch e não quebra o app
            if (btn200 != null) btn200.setOnClickListener { adicionarRapido(200.0) }
            if (btn500 != null) btn500.setOnClickListener { adicionarRapido(500.0) }
        } catch (e: Exception) {
            // Ignora se não achar os botões
        }

        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val serviceAgua = retrofit.create(HidratacaoService::class.java)
        val serviceMeta = retrofit.create(MetaService::class.java)

        val repoAgua = HidratacaoRepository(serviceAgua)
        val repoMeta = MetaRepository(serviceMeta)

        viewModel = ViewModelProvider(
            this,
            AguaViewModelFactory(repoAgua, repoMeta)
        )[AguaViewModel::class.java]

        adapter = AguaAdapter(emptyList()) { item ->
            confirmarExclusao(item)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel.lista.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }

        viewModel.total.observe(viewLifecycleOwner) { total ->
            val metaAtual = viewModel.metaDiaria.value ?: 2000.0
            atualizarBarra(progressBar, txtStatusMeta, total, metaAtual)

            val totalStr = if (total % 1.0 == 0.0) String.format("%.0f", total) else total.toString()
            txtTotal.text = "$totalStr ml"
        }

        viewModel.metaDiaria.observe(viewLifecycleOwner) { meta ->
            val totalAtual = viewModel.total.value ?: 0.0
            atualizarBarra(progressBar, txtStatusMeta, totalAtual, meta)
        }

        atualizarTextoData(txtData)
        carregarDados()

        btnPrev.setOnClickListener {
            selectedDate = selectedDate.minusDays(1)
            atualizarTextoData(txtData)
            carregarDados()
        }

        btnNext.setOnClickListener {
            selectedDate = selectedDate.plusDays(1)
            atualizarTextoData(txtData)
            carregarDados()
        }

        btnAdd.setOnClickListener {
            mostrarDialogAdicionar()
        }
    }

    private fun atualizarTextoData(txt: TextView) {
        val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale("pt", "BR"))
        if (selectedDate == LocalDate.now()) {
            txt.text = "Hoje"
        } else {
            txt.text = selectedDate.format(formatter)
        }
    }

    private fun atualizarBarra(bar: ProgressBar, txt: TextView, total: Double, meta: Double) {
        val porcentagem = if (meta > 0) (total / meta) * 100 else 0.0
        bar.progress = porcentagem.toInt()

        val tStr = String.format("%.0f", total)
        val mStr = String.format("%.0f", meta)
        txt.text = "Meta: $mStr ml"

        if (total >= meta) {
            bar.progressTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50"))
        } else {
            bar.progressTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#29B6F6"))
        }
    }

    private fun carregarDados() {
        if (usuarioId != 0) {
            lifecycleScope.launch {
                viewModel.carregarDados(usuarioId, selectedDate.toString())
            }
        }
    }

    private fun adicionarRapido(qtd: Double) {
        lifecycleScope.launch {
            val ok = viewModel.adicionarAgua(usuarioId, qtd)
            if (ok) {
                Toast.makeText(requireContext(), "+${qtd.toInt()}ml", Toast.LENGTH_SHORT).show()
                carregarDados()
            }
        }
    }

    private fun mostrarDialogAdicionar() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Adicionar Água")
        builder.setMessage("Digite a quantidade em ml:")
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "Ex: 200"
        builder.setView(input)
        builder.setPositiveButton("Salvar") { _, _ ->
            val texto = input.text.toString()
            val qtd = texto.toDoubleOrNull()
            if (qtd != null && qtd > 0) {
                lifecycleScope.launch {
                    val ok = viewModel.adicionarAgua(usuarioId, qtd)
                    if (ok) {
                        Toast.makeText(requireContext(), "Registrado!", Toast.LENGTH_SHORT).show()
                        carregarDados()
                    } else {
                        Toast.makeText(requireContext(), "Erro ao salvar", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Valor inválido", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun confirmarExclusao(item: HidratacaoModel) {
        val qtdStr = if (item.quantidade % 1.0 == 0.0) String.format("%.0f", item.quantidade) else item.quantidade.toString()
        AlertDialog.Builder(requireContext())
            .setTitle("Remover")
            .setMessage("Deseja apagar este registro de $qtdStr ml?")
            .setPositiveButton("Sim, apagar") { _, _ ->
                lifecycleScope.launch {
                    val ok = viewModel.removerAgua(item.id)
                    if (ok) {
                        Toast.makeText(requireContext(), "Removido!", Toast.LENGTH_SHORT).show()
                        carregarDados()
                    } else {
                        Toast.makeText(requireContext(), "Erro ao remover", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        carregarDados()
    }
}