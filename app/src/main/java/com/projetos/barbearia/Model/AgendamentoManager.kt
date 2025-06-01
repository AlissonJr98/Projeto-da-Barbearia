package com.projetos.barbearia.model

object AgendamentoManager {
    private val agendamentos = mutableListOf<Agendamento>()

    fun addAgendamento(agendamento: Agendamento) {
        agendamentos.add(agendamento)
    }

    fun getAgendamentos(): List<Agendamento> = agendamentos.toList()
}
