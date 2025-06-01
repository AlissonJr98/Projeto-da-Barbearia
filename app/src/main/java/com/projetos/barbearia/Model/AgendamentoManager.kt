package com.projetos.barbearia.model

object AgendamentoManager {
    private val agendamentos = mutableListOf<Agendamento>()

    fun addAgendamento(agendamento: Agendamento) {
        agendamentos.add(agendamento)
    }

    fun getAgendamentos(): List<Agendamento> = agendamentos.toList()

    // Atualiza um agendamento existente pelo índice
    fun updateAgendamento(index: Int, novoAgendamento: Agendamento) {
        if (index in agendamentos.indices) {
            agendamentos[index] = novoAgendamento
        }
    }

    // Remove um agendamento pelo índice
    fun removeAgendamento(index: Int) {
        if (index in agendamentos.indices) {
            agendamentos.removeAt(index)
        }
    }

    // Remove um agendamento pelo objeto, se existir
    fun removeAgendamento(agendamento: Agendamento) {
        agendamentos.remove(agendamento)
    }
}

