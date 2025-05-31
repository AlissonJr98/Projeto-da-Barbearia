package com.projetos.barbearia.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.projetos.barbearia.adapter.AgendamentoAdapter
import com.projetos.barbearia.databinding.ActivityPerfilBinding
import com.projetos.barbearia.model.AgendamentoManager

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                systemBarsInsets.bottom
            )
            insets
        }

        binding.profileName.text = "Alisson"
        binding.profileEmail.text = "alisson@email.com"

        val agendamentos = AgendamentoManager.getAgendamentos()
        binding.recyclerViewAgendamentos.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAgendamentos.adapter = AgendamentoAdapter(agendamentos)

        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Funcionalidade de editar perfil ainda não implementada", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            finish()
        }
    }
}
