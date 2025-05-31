package com.projetos.barbearia.Activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.projetos.barbearia.R
import com.projetos.barbearia.databinding.ActivityEscolhaServicoBinding
import com.projetos.barbearia.model.Agendamento
import com.projetos.barbearia.model.AgendamentoManager
import com.projetos.barbearia.viewmodel.EscolhaServicoViewModel
import java.text.SimpleDateFormat
import java.util.*

class EscolhaServicoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEscolhaServicoBinding
    private val viewModel: EscolhaServicoViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEscolhaServicoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVoltarServico.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Exemplo para popular títulos via ViewModel
        viewModel.barberItems.observe(this) { items ->
            if (items.isNotEmpty()) {
                val barberItem = items[0]
                binding.titleTextView.text = "Bem-vindo a Barbearia Inosuke ${barberItem.title}"
                binding.clubeTextView.text = barberItem.title
                binding.descricaoTextView.text = barberItem.subtitle
                binding.abertoTextView.text = barberItem.horario
            }
        }

        // Seleção de data com DatePickerDialog
        binding.horarioTextView.text = "Selecione a data do agendamento"
        binding.horarioTextView.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.horarioTextView.text = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }

        binding.reservarButton.setOnClickListener {
            val selectedService = when (binding.servicosRadioGroup.checkedRadioButtonId) {
                R.id.corteCabeloRadioButton -> "Corte de Cabelo"
                R.id.barbaRadioButton -> "Barba"
                R.id.corteSeniorRadioButton -> "Corte Senior"
                R.id.corteTesouraRadioButton -> "Corte de Tesoura"
                R.id.corteInfantilRadioButton -> "Corte Infantil"
                else -> null
            }
            val selectedTime = binding.horarioSpinner.selectedItem.toString()
            val selectedDate = binding.horarioTextView.text.toString()

            if (selectedService != null &&
                selectedTime.isNotEmpty() &&
                selectedDate != "Selecione a data do agendamento") {
                val agendamento = Agendamento(selectedService, selectedTime, selectedDate)
                AgendamentoManager.addAgendamento(agendamento)

                Toast.makeText(
                    this,
                    "Reserva confirmada: $selectedService em $selectedDate às $selectedTime",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this, "Por favor, selecione serviço, data e horário.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
