package com.luizeduardobrandao.letropia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel para a MainActivity, responsável por:
 * 1) Guardar o item selecionado no “spinner”
 * 2) Expor um LiveData de controle para habilitar/desabilitar o botão
 */

class MainViewModel: ViewModel() {

    // 1) Número de letras selecionado (null = nada escolhido ainda)
    private val _selectedNumber = MutableLiveData<Int?>(null)
    val selectedNumber: LiveData<Int?> = _selectedNumber

    // 2) Controle de habilitação do botão (inicia em false)
    private val _isButtonEnabled = MutableLiveData(false)
    val isButtonEnabled: LiveData<Boolean> = _isButtonEnabled


     // Deve ser chamado ao escolher um item no dropdown.
     //     * @param position índice da opção (0 é hint)
     //     * @param itemText texto completo (ex: "4 letras")
     fun onItemSelected(position: Int, itemText: String){

         // extrai número (vai ser > 0 para 4,5,6,7,8)
         val numero = itemText.substringBefore(" ").toIntOrNull() ?: 0
         _selectedNumber.value = if (numero > 0) numero else null
         _isButtonEnabled.value = (numero > 0)
     }
}