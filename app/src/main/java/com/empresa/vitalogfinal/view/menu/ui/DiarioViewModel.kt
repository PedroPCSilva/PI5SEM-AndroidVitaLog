package com.empresa.vitalogfinal.view.menu.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.empresa.vitalogfinal.model.diario.GrupoModel
import com.empresa.vitalogfinal.repository.DiarioRepository
import com.empresa.vitalogfinal.repository.MetaRepository

class DiarioViewModel(
    private val repoDiario: DiarioRepository,
    private val repoMeta: MetaRepository
) : ViewModel() {

    private val _grupos = MutableLiveData<List<GrupoModel>>()
    val grupos: LiveData<List<GrupoModel>> = _grupos

    // Status: (Total Consumido, Meta)
    private val _statusCalorias = MutableLiveData<Pair<Double, Double>>()
    val statusCalorias: LiveData<Pair<Double, Double>> = _statusCalorias

    suspend fun carregarDiario(usuarioId: Int, data: String) {
        try {
            // 1. Busca Grupos (Lista)
            val result = repoDiario.getDiario(usuarioId, data)
            _grupos.postValue(result ?: emptyList())

            // 2. Busca Total Consumido (Isso corrige o 0 kcal)
            val totalConsumido = repoDiario.getTotalCalorias(usuarioId, data)

            // 3. Busca Meta Definida
            val metas = repoMeta.listar(usuarioId, data)
            val metaAlvo = metas.find { it.tipo == "caloria" }?.meta ?: 2000.0

            // 4. Envia para a tela
            _statusCalorias.postValue(Pair(totalConsumido, metaAlvo))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun criarGrupo(usuarioId: Int, nome: String): GrupoModel? {
        return repoDiario.criarGrupo(usuarioId, nome)
    }
}

// --- A FACTORY FICA AQUI DENTRO, COMO NO ANTIGO ---
class DiarioViewModelFactory(
    private val repoDiario: DiarioRepository, // 'private val' corrige o erro de referência
    private val repoMeta: MetaRepository      // 'private val' corrige o erro de referência
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiarioViewModel::class.java)) {
            return DiarioViewModel(repoDiario, repoMeta) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}