package com.projetos.barbearia.adapter // Pacote onde a classe está localizada

import android.view.LayoutInflater // Importa a classe LayoutInflater para inflar layouts XML <button class="citation-flag" data-index="1">
import android.view.ViewGroup // Importa a classe ViewGroup para manipular grupos de views
import androidx.recyclerview.widget.RecyclerView // Importa o RecyclerView para criar listas eficientes
import com.projetos.barbearia.Model.BarberItem // Modelo de dados que representa um item da barbearia
import com.projetos.barbearia.databinding.ItemBarberBinding // Classe gerada pelo View Binding para acessar os elementos do layout

class BarberAdapter(
    private val items: List<BarberItem>, // Lista de itens a serem exibidos no RecyclerView
    private val onItemClick: (BarberItem) -> Unit // Função de callback para lidar com cliques nos itens
) : RecyclerView.Adapter<BarberAdapter.BarberViewHolder>() { // Adapter para o RecyclerView

    // ViewHolder interno que mantém as referências das views
    inner class BarberViewHolder(private val binding: ItemBarberBinding) :
        RecyclerView.ViewHolder(binding.root) { // ViewHolder inicializado com o root do binding
        init {
            // Configurar o clique no item
            binding.root.setOnClickListener { // Define o listener de clique no item
                val position = adapterPosition // Obtém a posição atual do item no adapter
                if (position != RecyclerView.NO_POSITION) { // Verifica se a posição é válida
                    onItemClick(items[position]) // Chama a função de callback com o item clicado
                }
            }
        }

        // Método para vincular os dados ao ViewHolder
        fun bind(item: BarberItem) {
            binding.titleTextView.text = item.title // Define o título no TextView
            binding.subtitleTextView.text = item.subtitle // Define o subtítulo no TextView
            binding.horario.text = item.horario // Define o horário no TextView
            binding.imageView.setImageResource(item.imageResId) // Define a imagem no ImageView
        }
    }

    // Método chamado para criar novos ViewHolder quando necessário
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberViewHolder {
        val binding = ItemBarberBinding.inflate(LayoutInflater.from(parent.context), parent, false) // Infla o layout usando o LayoutInflater <button class="citation-flag" data-index="1">
        return BarberViewHolder(binding) // Retorna um novo ViewHolder com o binding
    }

    // Método chamado para vincular os dados a um ViewHolder existente
    override fun onBindViewHolder(holder: BarberViewHolder, position: Int) {
        holder.bind(items[position]) // Vincula os dados do item na posição especificada ao ViewHolder
    }

    // Retorna o número total de itens na lista
    override fun getItemCount(): Int = items.size // Retorna o tamanho da lista de itens
}