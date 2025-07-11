package com.projetos.barbearia.viewmodel // Pacote onde a classe está localizada

import androidx.lifecycle.LiveData // Importa LiveData para observar mudanças nos dados <button class="citation-flag" data-index="1">
import androidx.lifecycle.MutableLiveData // Importa MutableLiveData para atualizar dados de forma segura <button class="citation-flag" data-index="6">
import androidx.lifecycle.ViewModel // Importa ViewModel para gerenciar dados relacionados à UI <button class="citation-flag" data-index="5">
import com.projetos.barbearia.Model.BarberItem // Importa o modelo BarberItem, que representa um item da barbearia
import com.projetos.barbearia.repository.BarberRepository // Importa o repositório que fornece dados de barbearias ou serviços

// Classe ViewModel que gerencia os dados para a tela principal (MainActivity)
class MainViewModel : ViewModel() {
    private val _barberItems = MutableLiveData<List<BarberItem>>() // Dados mutáveis privados para atualização segura <button class="citation-flag" data-index="6">
    val barberItems: LiveData<List<BarberItem>> get() = _barberItems // Dados públicos observáveis para a UI <button class="citation-flag" data-index="1">

    init {
        loadBarberItems() // Carrega os dados iniciais ao inicializar o ViewModel
    }

    // Método privado para carregar os itens da barbearia
    private fun loadBarberItems() {
        val repository = BarberRepository() // Instância do repositório para fornecer dados
        _barberItems.value = repository.getBarberItems() // Atualiza os dados com os itens fornecidos pelo repositório
    }
}