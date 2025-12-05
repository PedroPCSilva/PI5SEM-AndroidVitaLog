package com.empresa.vitalogfinal.view.menu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.empresa.vitalogfinal.repository.GrupoRepository

class DetalhesGrupoViewModelFactory(
    private val repository: GrupoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetalhesGrupoViewModel::class.java)) {
            return DetalhesGrupoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
