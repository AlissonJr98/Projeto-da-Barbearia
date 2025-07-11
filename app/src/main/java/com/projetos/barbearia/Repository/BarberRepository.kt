package com.projetos.barbearia.repository // Pacote onde a classe está localizada

import com.projetos.barbearia.Model.BarberItem // Importa a classe BarberItem, que representa um item da barbearia
import com.projetos.barbearia.R // Importa os recursos de layout e imagens do projeto

// Classe responsável por fornecer dados de barbearias ou serviços
class BarberRepository {
    // Método que retorna uma lista de itens (barbearias ou serviços)
    fun getBarberItems(): List<BarberItem> {
        return listOf(
            // Cada item representa uma barbearia ou serviço com título, subtítulo, horário e imagem
            BarberItem("BARBEIRO TANJIRO KAMADO", "Horário disponível", "10am - 10pm", R.drawable.img_02), // Primeiro item da lista
            BarberItem("BARBEIRO ZENITSU AGATSUMA", "Horário disponível", "10am - 10pm", R.drawable.img_03), // Segundo item da lista
            BarberItem("BARBEIRO TENGEN UZUI", "Horário disponível", "10am - 10pm", R.drawable.img_02), // Terceiro item da lista
            BarberItem("BARBEIRO KYOJURO RENGOKU", "Horário disponível", "10am - 10pm", R.drawable.img_03)  // Quarto item da lista
        ) // Retorna uma lista imutável de objetos BarberItem <button class="citation-flag" data-index="1">
    }
}