package com.empresa.vitalogfinal.view.menu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.empresa.vitalogfinal.repository.DiarioRepository
import com.empresa.vitalogfinal.repository.MetaRepository // <--- Importante adicionar este import

class DiarioViewModelFactory(
    private val repoDiario: DiarioRepository,
    private val repoMeta: MetaRepository // <--- Adicionamos o segundo repositório aqui
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiarioViewModel::class.java)) {
            // Agora passamos os dois para o construtor do ViewModel
            return DiarioViewModel(repoDiario, repoMeta) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}