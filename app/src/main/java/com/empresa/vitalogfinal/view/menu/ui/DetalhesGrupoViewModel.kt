package com.empresa.vitalogfinal.view.menu.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.empresa.vitalogfinal.model.diario.FoodModel
import com.empresa.vitalogfinal.model.diario.GrupoModel
import com.empresa.vitalogfinal.repository.GrupoRepository

class DetalhesGrupoViewModel(
    private val repo: GrupoRepository
) : ViewModel() {

    private val _grupo = MutableLiveData<GrupoModel>()
    val grupo: LiveData<GrupoModel> = _grupo

    private val _alimentos = MutableLiveData<List<FoodModel>>()
    val alimentos: LiveData<List<FoodModel>> = _alimentos

    suspend fun carregar(grupoId: Int) {
        repo.getGrupo(grupoId)?.let { _grupo.postValue(it) }
        _alimentos.postValue(repo.getAlimentos(grupoId))
    }

    suspend fun apagarGrupo(id: Int) = repo.apagarGrupo(id)

    suspend fun apagarAlimento(id: Int) = repo.apagarAlimento(id)
}
