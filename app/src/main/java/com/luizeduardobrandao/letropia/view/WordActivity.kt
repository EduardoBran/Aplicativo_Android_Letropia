package com.luizeduardobrandao.letropia.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.luizeduardobrandao.letropia.R
import com.luizeduardobrandao.letropia.databinding.ActivityWordBinding
import com.luizeduardobrandao.letropia.helper.BannerAds
import com.luizeduardobrandao.letropia.view.adapter.LetterAdapter
import com.luizeduardobrandao.letropia.viewmodel.WordViewModel

// Activity responsável pela interação com o usuário na tela de adivinhação de palavra.
class WordActivity : AppCompatActivity() {

    // Binding gerado para acesso às views do layout xml
    private lateinit var binding: ActivityWordBinding

    // Adapter que exibe um campo de letra para cada caractere da palavra
    private lateinit var adapter: LetterAdapter

    // ViewModel que contém toda a lógica de negócio e estado da palavra
    private val viewModel: WordViewModel by viewModels()

    // guardar a cor original do botão validar
    private lateinit var defaultBtnValidadeTint: ColorStateList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Infla o layout e inicializa o binding
        binding = ActivityWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a Toolbar com botão de voltar
        setSupportActionBar(binding.toolbarWord)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarWord.setNavigationOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recupera do Intent o tamanho da palavra (número de letras)
        val number = intent.getIntExtra("EXTRA_NUM_LETRAS", 4)

        // Armazena cor original do background do botão validar
        defaultBtnValidadeTint = binding.btnValidate.backgroundTintList!!

                // Inicializa o RecyclerView e seu adapter
        setAdapter(number)
        // Configura observadores para LiveData do ViewModel
        setObservers()
        // Configura listeners de clique em botões e navegação
        setListeners()

        // Inicializa o ViewModel apenas na primeira criação (evita recarregar em rotações)
        viewModel.init(number, savedInstanceState == null)

        // carrega o banner no container da view binding
        BannerAds.loadBanner(this, binding.frameBanner)


    }

    // * Configura o RecyclerView com um layout horizontal e associa o adapter.
    // * @param number quantidade de letras para gerar campos.
    private fun setAdapter(number: Int){

        // Cria um LetterAdapter passando callback de mudança de letras
        adapter = LetterAdapter(number) { letters ->
            viewModel.onLettersChanged(letters)
        }

        // Aplica configuração de layout horizontal e associa o adapter
        binding.rvLetters.apply {
            layoutManager = LinearLayoutManager(
                this@WordActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = this@WordActivity.adapter
        }
    }


    // * Observa mudanças nas LiveData para manter a UI sincronizada com o estado.
    private fun setObservers(){

        // Atualiza o TextView com a string embaralhada quando muda
        viewModel.scrambled.observe(this) { scrambled ->
            binding.tvScrambled.text = scrambled
        }

        // Quando a lista de letras muda, atualiza o adapter para mostrar no RecyclerView
        viewModel.letters.observe(this) { list->
            adapter.setLetters(list)
        }

        // Habilita ou desabilita o botão de validação conforme estado
        viewModel.isValidateEnabled.observe(this) { enabled ->
            binding.btnValidate.isEnabled = enabled
        }

        // Exibe feedback visual ao validar: muda cor e agenda próxima ação
        viewModel.isCorrect.observe(this) { correct ->
            correct?.let {
                // Define a cor do botão de validação (verde/vermelho)
                val colorRes = if (it) R.color.green_correct else R.color.red_wrong
                binding.btnValidate.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, colorRes))

                // Após 2 segundos, limpa estado ou carrega nova palavra
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.onValidationHandled()
                    if (it) viewModel.newWord()          // acerto: nova palavra
                    else viewModel.resetInput()          // erro: limpa apenas inputs
                    // restaura cor original do botão após 2s
                    binding.btnValidate.backgroundTintList = defaultBtnValidadeTint
                }, 2000)
            }
        }
    }

    // * Configura os listeners de clique para botões e ação de navegação.
    private fun setListeners(){

        // Botão para validar o palpite
        binding.btnValidate.setOnClickListener { viewModel.validate() }

        // Botão para forçar nova palavra manualmente
        binding.btnNewWord.setOnClickListener { viewModel.newWord() }

    }
}