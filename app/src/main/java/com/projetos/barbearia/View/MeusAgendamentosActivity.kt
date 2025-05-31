package com.projetos.barbearia.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.projetos.barbearia.databinding.ActivityMeusAgendamentosBinding
import com.projetos.barbearia.model.AgendamentoManager
import com.projetos.barbearia.adapter.AgendamentoAdapter
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.firebase.auth.FirebaseAuth

class MeusAgendamentosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMeusAgendamentosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeusAgendamentosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            view.updatePadding(top = topInset)
            insets
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // Usuário não logado, redireciona para login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val agendamentos = AgendamentoManager.getAgendamentos()

        binding.recyclerViewAgendamentos.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAgendamentos.adapter = AgendamentoAdapter(agendamentos)

        binding.btnVoltarAgendamentos.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}