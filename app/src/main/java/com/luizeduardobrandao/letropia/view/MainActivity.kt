package com.luizeduardobrandao.letropia.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luizeduardobrandao.letropia.R
import com.luizeduardobrandao.letropia.databinding.ActivityMainBinding
import com.luizeduardobrandao.letropia.helper.BannerAds
import com.luizeduardobrandao.letropia.view.adapter.SpinnerAdapter
import com.luizeduardobrandao.letropia.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a Toolbar (binding.toolbarMain) como ActionBar da Activity
        setSupportActionBar(binding.toolbarMain)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Prepara a lista de opções vindas do arrays.xml
        val opcoes = resources.getStringArray(R.array.array_letras).toList()

        setAdapter(opcoes)
        setObservers()
        setListeners()


        // carrega o banner no container da view binding
        BannerAds.loadBanner(this, binding.frameBanner)
    }

    // Configura adapter e comportamento do dropdown
    private fun setAdapter(opcoes: List<String>) {
        val adapter = SpinnerAdapter(this, opcoes)
        binding.spinnerLetras.apply {
            // threshold 0 -> mostrar dropdown sem digitar
            threshold = 0
            setAdapter(adapter)

            // bloqueia edição direta pra evitar disparo de filtro
            inputType = InputType.TYPE_NULL

            // mostra sempre a lista completa no clique
            setOnClickListener { showDropDown() }

            // // Escuta cliques nas opções e repassa ao ViewModel
            setOnItemClickListener { _, _, position, _ ->
                viewModel.onItemSelected(position, opcoes[position])
            }
        }
    }

    // Trata o clique no botão “Iniciar”
    private fun setListeners() {

        // Clicou em Iniciar?
        binding.btnIniciar.setOnClickListener {
            val numero = viewModel.selectedNumber.value
            if (numero == null) {
                Toast.makeText(
                    this, "Escolha o número de letras", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(
                Intent(this, WordActivity::class.java)
                    .putExtra("EXTRA_NUM_LETRAS", numero)
            )
        }
    }

    // Observa o LiveData para habilitar/desabilitar o botão
    private fun setObservers() {
        viewModel.isButtonEnabled.observe(this) { enabled ->
            binding.btnIniciar.alpha = if (enabled) 1f else 0.5f
        }
    }
}