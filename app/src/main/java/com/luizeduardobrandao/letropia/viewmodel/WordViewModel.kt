package com.luizeduardobrandao.letropia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.luizeduardobrandao.letropia.entitie.Word
import com.luizeduardobrandao.letropia.repository.WordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// ViewModel para gerenciar a lógica de obtenção e validação de palavras embaralhadas.
// Usa AndroidViewModel para ter acesso ao Application e ao contexto.
class WordViewModel (application: Application): AndroidViewModel(application){

    // Instância do repositório para ler e sortear palavras do JSON
    private val repo = WordRepository(application)

    // Armazena a palavra atual (sorteada) em memória
    private var currentWord: Word? = null

    // Número de letras (tamanho) solicitado para gerar a palavra (será modificado em "init")
    private var currentNumber = 0

    // LiveData que expõe a sequência embaralhada (options) da palavra atual
    private val _scrambled = MutableLiveData<String>()
    val scrambled: LiveData<String> = _scrambled

    // LiveData que expõe a lista de letras digitadas pelo usuário (pode conter null)
    private val _letters = MutableLiveData<List<Char?>>()
    val letters: LiveData<List<Char?>> = _letters

    // LiveData que indica se o botão de validação deve estar habilitado
    // fica true apenas quando todas as letras forem preenchidas
    private val _isValidateEnabled = MutableLiveData(false)
    val isValidateEnabled: LiveData<Boolean> = _isValidateEnabled

    // LiveData que indica se o palpite está correto (true),
    // incorreto (false) ou não avaliado (null)
    private val _isCorrect = MutableLiveData<Boolean?>()
    val isCorrect: LiveData<Boolean?> = _isCorrect



    // * Inicializa o ViewModel com um tamanho de palavra (number).
    // * Sorteia a primeira palavra usando IO em background.
    // * Só sorteia palavra na primeira criação
    fun init(number: Int, firstTime: Boolean) {
        currentNumber = number

        if (!firstTime) return

        viewModelScope.launch(Dispatchers.IO){
            // Sorteia uma palavra aleatória sem exclusão
            currentWord = repo.getRandomWord(number)
            // Publica o texto embaralhado na LiveData (thread segura)
            _scrambled.postValue(currentWord?.options)
            // Usa função reset() para resetar letras e estado de validação
            resetInput()
        }

    }

    // * Chamado sempre que as letras mudam na UI.
    // * Atualiza _letters e habilita validação se não houver null.
    fun onLettersChanged(list: List<Char?>) {
        _letters.value = list
        // Habilita botão apenas se todas as posições estiverem preenchidas
        _isValidateEnabled.value = list.all { it != null }
    }

    // * Valida o palpite do usuário comparando com a resposta correta.
    // * Atualiza _isCorrect com true/false.
    fun validate(){
        // Junta as letras em string para comparação
        val guess = letters.value?.joinToString(separator = "") { it.toString() } ?: ""
        // Ignora maiúsculas/minúsculas ao comparar
        _isCorrect.value = guess.equals(currentWord?.answer, ignoreCase = true)
    }

    // * Zera o estado de validação após tratar resultado na UI.
    // * Isso permite observar alterações subsequentes.
    fun onValidationHandled(){
        _isCorrect.value = null
    }

    // * Solicita uma nova palavra, diferente da atual.
    // * Atualiza currentWord e LiveData de scrambled em background.
    fun newWord(){
        viewModelScope.launch(Dispatchers.IO) {
            // Sorteia excluindo a palavra atual
            val next = repo.getRandomWord(currentNumber, currentWord)
            currentWord = next
            // Publica novo embaralhado
            _scrambled.postValue(next.options)
            // Usa função reset() para resetar letras e estado de validação
            resetInput()
        }
    }

    // * Zera o LiveData de letras e desabilita o botão de validação.
    // * Executa postValue para chamadas em background.
    fun resetInput() {
        // Cria lista de nulls do tamanho atual e publica
        _letters.postValue(List(currentNumber) { null })
        _isValidateEnabled.postValue(false)
    }
}