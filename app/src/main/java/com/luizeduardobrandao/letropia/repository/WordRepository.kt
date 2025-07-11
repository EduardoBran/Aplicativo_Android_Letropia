package com.luizeduardobrandao.letropia.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luizeduardobrandao.letropia.R
import com.luizeduardobrandao.letropia.entitie.Word

class WordRepository(private val context: Context) {

    // Lê e desserializa o JSON apenas uma vez (by lazy)
    private val allWords: List<Word> by lazy {
        context.resources.openRawResource(R.raw.wordsdata)
            .bufferedReader()
            .use { it.readText() }
            .let { json ->
                val type = object: TypeToken<List<Word>>() {}.type
                Gson().fromJson<List<Word>>(json, type)
            }
    }

    // Retorna todas as palavras cujo campo numberLetters == escolha em MainActivity
    fun getWordsByNumber(numberLetters: Int): List<Word> {
        return allWords.filter { it.number == numberLetters }
    }

    // Sorteia uma palavra aleatória do mesmo tamanho,
    // opcionalmente diferente de uma anterior para “nova palavra”.
    fun getRandomWord(numberLetters: Int, exclude: Word? = null): Word{
        val candidates = allWords.filter {
            it.number == numberLetters && it != exclude
        }
        return candidates.random()
    }
}