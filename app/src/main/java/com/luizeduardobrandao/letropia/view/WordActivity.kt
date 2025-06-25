package com.luizeduardobrandao.letropia.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luizeduardobrandao.letropia.R
import com.luizeduardobrandao.letropia.databinding.ActivityWordBinding
import com.luizeduardobrandao.letropia.helper.BannerAds

class WordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) Configura a Toolbar com botão de voltar
        setSupportActionBar(binding.toolbarWord)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarWord.setNavigationOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // carrega o banner no container da view binding
        BannerAds.loadBanner(this, binding.frameBanner)
    }
}