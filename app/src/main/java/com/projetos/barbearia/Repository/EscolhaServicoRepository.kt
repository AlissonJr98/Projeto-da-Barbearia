package com.projetos.barbearia.Repository // Pacote onde a classe está localizada

import com.projetos.barbearia.R // Importa os recursos de layout e imagens do projeto
import com.projetos.barbearia.Model.BarberItem // Importa a classe BarberItem, que representa um item da barbearia

// Classe responsável por fornecer dados de serviços ou barbearias para a tela de escolha de serviço
class EscolhaServicoRepository {
    // Método que retorna uma lista de itens (barbearias ou serviços)
    fun getBarberItems(): List<BarberItem> {
        return listOf(
            // Cada item representa uma barbearia ou serviço com título, subtítulo, horário e imagem
            BarberItem("CLUBE DE CAVALHEIROS", "Horário disponível", "10am - 10pm", R.drawable.img_02), // Primeiro item da lista
            BarberItem("CLUBE DE CAVALHEIROS", "Horário disponível", "10am - 10pm", R.drawable.img_03), // Segundo item da lista
            BarberItem("CLUBE DE CAVALHEIROS", "Horário disponível", "10am - 10pm", R.drawable.img_02), // Terceiro item da lista
            BarberItem("CLUBE DE CAVALHEIROS", "Horário disponível", "10am - 10pm", R.drawable.img_03)  // Quarto item da lista
        ) // Retorna uma lista imutável de objetos BarberItem <button class="citation-flag" data-index="1">
    }
}