package com.empresa.vitalogfinal.view.menu

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.model.relatorio.RelatorioDia
import com.empresa.vitalogfinal.service.MetaService
import com.empresa.vitalogfinal.service.RelatorioService
import com.empresa.vitalogfinal.service.UsuarioService
import com.empresa.vitalogfinal.view.menu.perfil.EditarDadosActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RelatorioFragment : Fragment() {

    private lateinit var layoutFaltando: LinearLayout
    private lateinit var layoutConteudo: ScrollView
    private lateinit var txtImc: TextView
    private lateinit var txtClassImc: TextView
    private lateinit var txtAguaIdeal: TextView

    // Gráficos
    private lateinit var chartCalorias: BarChart
    private lateinit var chartAgua: BarChart

    private var usuarioId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_relatorio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        usuarioId = prefs.getInt("user_id", 0)

        // Bind Views
        layoutFaltando = view.findViewById(R.id.layout_dados_faltando)
        layoutConteudo = view.findViewById(R.id.layout_relatorio_conteudo)
        txtImc = view.findViewById(R.id.txt_valor_imc)
        txtClassImc = view.findViewById(R.id.txt_class_imc)
        txtAguaIdeal = view.findViewById(R.id.txt_agua_ideal)
        chartCalorias = view.findViewById(R.id.chart_calorias)
        chartAgua = view.findViewById(R.id.chart_agua)

        val btnCompletar = view.findViewById<Button>(R.id.btn_completar_cadastro)
        btnCompletar.setOnClickListener {
            startActivity(Intent(requireContext(), EditarDadosActivity::class.java))
        }

        verificarPerfil()
    }

    override fun onResume() {
        super.onResume()
        verificarPerfil()
    }

    private fun verificarPerfil() {
        val cred = Credenciais()
        val retrofit = Retrofit.Builder().baseUrl(cred.ip).addConverterFactory(GsonConverterFactory.create()).build()
        val serviceUser = retrofit.create(UsuarioService::class.java)

        lifecycleScope.launch {
            try {
                val response = serviceUser.getPerfil(usuarioId)
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        if (user.peso != null && user.altura != null && user.peso > 0 && user.altura > 0) {
                            layoutFaltando.visibility = View.GONE
                            layoutConteudo.visibility = View.VISIBLE

                            calcularEstatisticas(user.peso, user.altura)

                            // Carrega gráficos
                            carregarGraficos(retrofit)
                        } else {
                            layoutFaltando.visibility = View.VISIBLE
                            layoutConteudo.visibility = View.GONE
                        }
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun calcularEstatisticas(peso: Double, altura: Double) {
        val aguaIdeal = peso * 35
        txtAguaIdeal.text = "${String.format("%.0f", aguaIdeal)} ml"

        val imc = peso / (altura * altura)
        txtImc.text = String.format("%.1f", imc)

        val classificacao = when {
            imc < 18.5 -> "Abaixo do peso"
            imc < 24.9 -> "Peso normal"
            imc < 29.9 -> "Sobrepeso"
            else -> "Obesidade"
        }
        txtClassImc.text = classificacao

        // Cores do IMC
        if (classificacao == "Peso normal") {
            txtImc.setTextColor(Color.parseColor("#4CAF50")) // Verde
        } else {
            txtImc.setTextColor(Color.parseColor("#FF9800")) // Laranja
        }
    }

    private fun carregarGraficos(retrofit: Retrofit) {
        val serviceRelatorio = retrofit.create(RelatorioService::class.java)
        val serviceMeta = retrofit.create(MetaService::class.java)

        lifecycleScope.launch {
            try {
                // 1. Busca Histórico (7 dias)
                val resHist = serviceRelatorio.getSemanal(usuarioId)

                // 2. Busca Metas Atuais (para colorir o gráfico)
                val dataHoje = LocalDate.now().toString()
                val resMeta = serviceMeta.listarMetas(usuarioId, dataHoje)

                if (resHist.isSuccessful && resMeta.isSuccessful) {
                    val listaDias = resHist.body() ?: emptyList()
                    val metas = resMeta.body() ?: emptyList()

                    val metaCaloria = metas.find { it.tipo == "caloria" }?.meta ?: 2000.0
                    val metaAgua = metas.find { it.tipo == "hidratacao" }?.meta ?: 2000.0

                    configurarGrafico(chartCalorias, listaDias, metaCaloria, isCaloria = true)
                    configurarGrafico(chartAgua, listaDias, metaAgua, isCaloria = false)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun configurarGrafico(chart: BarChart, dados: List<RelatorioDia>, meta: Double, isCaloria: Boolean) {
        // Ordenar dados por data
        val dadosOrdenados = dados.sortedBy { it.data_reg }

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val cores = ArrayList<Int>()

        dadosOrdenados.forEachIndexed { index, dia ->
            val valor = if (isCaloria) dia.total_calorias.toFloat() else dia.total_agua.toFloat()
            entries.add(BarEntry(index.toFloat(), valor))


            try {
                val date = LocalDate.parse(dia.data_reg.substring(0, 10))
                val formatter = DateTimeFormatter.ofPattern("dd/MM")
                labels.add(date.format(formatter))
            } catch (e: Exception) {
                labels.add(dia.data_reg)
            }



            if (isCaloria) {

                if (valor > meta) cores.add(Color.parseColor("#F44336")) // Passou
                else cores.add(Color.parseColor("#4CAF50")) // Ok
            } else {

                if (valor >= meta) cores.add(Color.parseColor("#2196F3")) // Meta Batida (Azul)
                else cores.add(Color.parseColor("#FF9800")) // Falta beber
            }
        }

        val dataSet = BarDataSet(entries, if (isCaloria) "Calorias" else "Água")
        dataSet.colors = cores
        dataSet.valueTextSize = 10f
        dataSet.valueTextColor = Color.BLACK

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f // Largura da barra

        chart.data = barData


        chart.description.isEnabled = false // Remove descrição
        chart.legend.isEnabled = false // Remove legenda
        chart.animateY(1000) // Animação
        chart.setFitBars(true)
        chart.setTouchEnabled(false) // Desativa zoom/toque

        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f


        chart.axisRight.isEnabled = false // Remove eixo da direita
        chart.axisLeft.setDrawGridLines(true)
        chart.axisLeft.axisMinimum = 0f // Começa do 0

        chart.invalidate() // Atualiza
    }
}