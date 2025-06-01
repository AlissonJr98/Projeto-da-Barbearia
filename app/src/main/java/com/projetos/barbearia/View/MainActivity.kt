package com.projetos.barbearia.view

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.projetos.barbearia.Activity.EscolhaServicoActivity
import com.projetos.barbearia.R
import com.projetos.barbearia.adapter.BarberAdapter
import com.projetos.barbearia.databinding.ActivityMainBinding
import com.projetos.barbearia.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

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

        binding.statusBarBackground.layoutParams.height = getStatusBarHeight()

        buscarNomeUsuario() // 🔹 Atualiza o nome do usuário

        binding.btnCard.setOnClickListener {
            startActivity(Intent(this, EscolhaServicoActivity::class.java))
        }

        binding.btnVoltarHome.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        observeViewModel()
    }

    private fun buscarNomeUsuario() {
        val user = auth.currentUser
        val nome = user?.displayName ?: "Usuário"
        binding.textView2.text = "Bem-vindo $nome"
    }

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

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_list -> {
                    startActivity(Intent(this, MeusAgendamentosActivity::class.java))
                    true
                }
                R.id.nav_search -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
