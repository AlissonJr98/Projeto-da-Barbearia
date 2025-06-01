package com.projetos.barbearia.view

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.projetos.barbearia.databinding.ActivityMeusAgendamentosBinding
import com.projetos.barbearia.model.AgendamentoManager
import com.projetos.barbearia.adapter.AgendamentoAdapter
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.firebase.auth.FirebaseAuth
import com.projetos.barbearia.R
import com.projetos.barbearia.model.Agendamento


class MeusAgendamentosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMeusAgendamentosBinding
    private lateinit var adapter: AgendamentoAdapter
    private var agendamentos = mutableListOf<Agendamento>()

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
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Carrega os agendamentos para uma lista mutável
        agendamentos = AgendamentoManager.getAgendamentos().toMutableList()

        adapter = AgendamentoAdapter(
            agendamentos,
            onEditarClick = { pos -> editarAgendamento(pos) },
            onCancelarClick = { pos -> cancelarAgendamento(pos) }
        )

        binding.recyclerViewAgendamentos.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAgendamentos.adapter = adapter

        binding.btnVoltarAgendamentos.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun editarAgendamento(pos: Int) {
        val agendamento = agendamentos[pos]

        // Infla o layout do diálogo de edição (crie dialog_editar_agendamento.xml conforme sugerido)
        val dialogView = layoutInflater.inflate(R.layout.dialog_editar_agendamento, null)
        val editServico = dialogView.findViewById<EditText>(R.id.editServico)
        val editData = dialogView.findViewById<EditText>(R.id.editData)
        val editHorario = dialogView.findViewById<EditText>(R.id.editHorario)
        val editBarbeiro = dialogView.findViewById<EditText>(R.id.editBarbeiro)

        // Preenche com os dados atuais
        editServico.setText(agendamento.servico)
        editData.setText(agendamento.data)
        editHorario.setText(agendamento.horario)
        editBarbeiro.setText(agendamento.barbeiro)

        AlertDialog.Builder(this)
            .setTitle("Editar Agendamento")
            .setView(dialogView)
            .setPositiveButton("Salvar") { _, _ ->
                val novoServico = editServico.text.toString()
                val novaData = editData.text.toString()
                val novoHorario = editHorario.text.toString()
                val novoBarbeiro = editBarbeiro.text.toString()

                if (novoServico.isNotBlank() && novaData.isNotBlank() &&
                    novoHorario.isNotBlank() && novoBarbeiro.isNotBlank()
                ) {
                    val novoAgendamento = agendamento.copy(
                        servico = novoServico,
                        data = novaData,
                        horario = novoHorario,
                        barbeiro = novoBarbeiro
                    )
                    // Atualiza o gerenciador e a lista local
                    AgendamentoManager.updateAgendamento(pos, novoAgendamento)
                    agendamentos[pos] = novoAgendamento
                    adapter.notifyItemChanged(pos)
                } else {
                    // Aqui você pode mostrar uma mensagem de erro se quiser
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cancelarAgendamento(pos: Int) {
        AgendamentoManager.removeAgendamento(pos)
        agendamentos.removeAt(pos)
        adapter.notifyItemRemoved(pos)
    }
}