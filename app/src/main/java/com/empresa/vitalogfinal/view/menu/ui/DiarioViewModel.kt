package com.empresa.vitalogfinal.view.menu.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.empresa.vitalogfinal.model.diario.GrupoModel
import com.empresa.vitalogfinal.repository.DiarioRepository
import com.empresa.vitalogfinal.repository.MetaRepository // <--- Importante

class DiarioViewModel(
    private val repoDiario: DiarioRepository,
    private val repoMeta: MetaRepository // <--- Nova dependência
) : ViewModel() {

    private val _grupos = MutableLiveData<List<GrupoModel>>()
    val grupos: LiveData<List<GrupoModel>> = _grupos

    // Novo LiveData que guarda: Par(Consumido, Meta)
    private val _statusCalorias = MutableLiveData<Pair<Double, Double>>()
    val statusCalorias: LiveData<Pair<Double, Double>> = _statusCalorias

    suspend fun carregarDiario(usuarioId: Int, data: String) {
        // 1. Busca Grupos (Lista)
        val result = repoDiario.getDiario(usuarioId, data)
        _grupos.postValue(result ?: emptyList())

        // 2. Busca Total Consumido (Número)
        val totalConsumido = repoDiario.getTotalCalorias(usuarioId, data)

        // 3. Busca Meta Definida
        val metas = repoMeta.listar(usuarioId, data)
        // Acha meta "caloria". Se não tiver, usa 2000.0
        val metaAlvo = metas.find { it.tipo == "caloria" }?.meta ?: 2000.0

        // 4. Envia para a tela
        _statusCalorias.postValue(Pair(totalConsumido, metaAlvo))
    }

    suspend fun criarGrupo(usuarioId: Int, nome: String): GrupoModel? {
        return repoDiario.criarGrupo(usuarioId, nome)
    }

    fun adicionarGrupoLocal(grupo: GrupoModel) {
        _grupos.value = _grupos.value.orEmpty() + grupo
    }
}
