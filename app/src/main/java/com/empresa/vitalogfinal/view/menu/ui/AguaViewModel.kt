package com.empresa.vitalogfinal.view.menu.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.empresa.vitalogfinal.model.hidratacao.HidratacaoModel
import com.empresa.vitalogfinal.repository.HidratacaoRepository
import com.empresa.vitalogfinal.repository.MetaRepository // <--- Importante

class AguaViewModel(
    private val repoAgua: HidratacaoRepository,
    private val repoMeta: MetaRepository // <--- Injeção do repo de metas
) : ViewModel() {

    private val _lista = MutableLiveData<List<HidratacaoModel>>()
    val lista: LiveData<List<HidratacaoModel>> = _lista

    private val _total = MutableLiveData<Double>()
    val total: LiveData<Double> = _total

    // Novo LiveData para a Meta
    private val _metaDiaria = MutableLiveData<Double>()
    val metaDiaria: LiveData<Double> = _metaDiaria

    suspend fun carregarDados(usuarioId: Int, data: String) {
        // 1. Carrega Água
        val resultado = repoAgua.listar(usuarioId, data)
        _lista.postValue(resultado)

        val soma = resultado.sumOf { it.quantidade }
        _total.postValue(soma)

        // 2. Carrega Meta do dia
        val metas = repoMeta.listar(usuarioId, data)
        // Busca a meta de tipo 'hidratacao', se não achar usa 2000.0 padrão
        val metaAgua = metas.find { it.tipo == "hidratacao" }?.meta ?: 2000.0
        _metaDiaria.postValue(metaAgua)
    }

    suspend fun adicionarAgua(usuarioId: Int, qtd: Double): Boolean {
        return repoAgua.adicionar(usuarioId, qtd)
    }

    suspend fun removerAgua(id: Int): Boolean {
        return repoAgua.remover(id)
    }
}

// Factory Atualizada
class AguaViewModelFactory(
    private val repoAgua: HidratacaoRepository,
    private val repoMeta: MetaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AguaViewModel(repoAgua, repoMeta) as T
    }
}