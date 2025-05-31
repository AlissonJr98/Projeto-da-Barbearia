package com.projetos.barbearia.view // Pacote onde a classe está localizada

import android.content.Intent // Importa a classe Intent para navegar entre atividades <button class="citation-flag" data-index="1">
import android.graphics.Color
import android.os.Build
import android.os.Bundle // Importa classes necessárias para gerenciar o ciclo de vida da atividade
import android.view.View
import androidx.activity.viewModels // Importa a função viewModels para inicializar ViewModel <button class="citation-flag" data-index="5">
import androidx.appcompat.app.AppCompatActivity // Importa a classe base para atividades compatíveis com a biblioteca de suporte
import androidx.recyclerview.widget.LinearLayoutManager // Importa o LinearLayoutManager para configurar o RecyclerView
import com.google.firebase.FirebaseApp // Importa a classe FirebaseApp para inicializar o Firebase <button class="citation-flag" data-index="3">
import com.projetos.barbearia.Activity.EscolhaServicoActivity // Importa a atividade EscolhaServicoActivity
import com.projetos.barbearia.adapter.BarberAdapter // Importa o adaptador para o RecyclerView
import com.projetos.barbearia.databinding.ActivityMainBinding // Importa o binding gerado automaticamente para acessar elementos do layout
import com.projetos.barbearia.viewmodel.MainViewModel // Importa o ViewModel associado a esta atividade

// Classe que representa a tela principal (MainActivity) do aplicativo
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Configuração da janela antes do super.onCreate()
        window.apply {
            // Permite que o conteúdo desenhe sob as barras do sistema
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

            // Para API 23+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

            // Para API 26+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                decorView.systemUiVisibility = decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }

            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
        }

        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajusta a altura da status bar background
        binding.statusBarBackground.layoutParams.height = getStatusBarHeight()

        // Seus listeners e configurações atuais
        binding.btnCard.setOnClickListener {
            startActivity(Intent(this, EscolhaServicoActivity::class.java))
        }

        binding.btnVoltarHome.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        observeViewModel()
    }

    // Método para obter a altura da status bar
    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.setHasFixedSize(true)
    }

    private fun observeViewModel() {
        viewModel.barberItems.observe(this) { items ->
            binding.recyclerView.adapter = BarberAdapter(items) { clickedItem ->
                val intent = Intent(this, EscolhaServicoActivity::class.java).apply {
                    putExtra("BARBER_ITEM", clickedItem)
                }
                startActivity(intent)
            }
        }
    }
}

