package com.projetos.barbearia.model

data class Agendamento(
    val servico: String,
    val horario: String,
    val data: String,
    val barbeiro: String  // novo campo
)