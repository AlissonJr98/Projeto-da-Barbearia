package com.projetos.barbearia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projetos.barbearia.databinding.ItemAgendamentoBinding
import com.projetos.barbearia.model.Agendamento

class AgendamentoAdapter(private val agendamentos: List<Agendamento>) :
    RecyclerView.Adapter<AgendamentoAdapter.AgendamentoViewHolder>() {

    inner class AgendamentoViewHolder(val binding: ItemAgendamentoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendamentoViewHolder {
        val binding = ItemAgendamentoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AgendamentoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AgendamentoViewHolder, position: Int) {
        val agendamento = agendamentos[position]
        holder.binding.servicoTextView.text = agendamento.servico
        holder.binding.dataTextView.text = agendamento.data
        holder.binding.horaTextView.text = agendamento.horario
    }

    override fun getItemCount() = agendamentos.size
}