package com.projetos.barbearia.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.projetos.barbearia.R
import com.projetos.barbearia.adapter.AgendamentoAdapter
import com.projetos.barbearia.databinding.ActivityPerfilBinding
import com.projetos.barbearia.model.AgendamentoManager

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uploadImageToFirebase(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        carregarFotoPerfil()
        binding.profileName.text = user?.displayName ?: "Nome não definido"
        binding.profileEmail.text = user?.email ?: "Email não definido"
        carregarDadosExtrasDoPerfil()

        binding.profileImage.setOnClickListener { pickImageLauncher.launch("image/*") }

        val agendamentos = AgendamentoManager.getAgendamentos()
        binding.recyclerViewAgendamentos.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAgendamentos.adapter = AgendamentoAdapter(agendamentos)

        binding.btnEditProfile.setOnClickListener { showEditNameDialog() }
        binding.btnEditEmail.setOnClickListener { showEditEmailDialog() }
        binding.profileAddress.setOnClickListener { showEditAddressDialog() }
        binding.profilePhone.setOnClickListener { showEditPhoneDialog() }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show()

            // Redireciona para a tela de login e limpa a pilha de activities
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun carregarFotoPerfil() {
        user?.photoUrl?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.default_profile)
                .into(binding.profileImage)
        }
    }

    private fun carregarDadosExtrasDoPerfil() {
        user?.uid?.let { uid ->
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        binding.profileAddress.text = doc.getString("address") ?: "Endereço não definido"
                        binding.profilePhone.text = doc.getString("phone") ?: "Telefone não definido"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar dados: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun salvarDadosExtrasDoPerfil(address: String, phone: String) {
        user?.uid?.let { uid ->
            val data = hashMapOf("address" to address, "phone" to phone)
            firestore.collection("users").document(uid).set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show()
                    binding.profileAddress.text = address
                    binding.profilePhone.text = phone
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao atualizar dados: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        val storageRef = FirebaseStorage.getInstance().reference
        val profileImagesRef = storageRef.child("profileImages/${user.uid}.jpg")

        profileImagesRef.putFile(imageUri)
            .addOnSuccessListener {
                profileImagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val profileUpdates = userProfileChangeRequest { photoUri = downloadUri }
                    user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Glide.with(this).load(downloadUri).placeholder(R.drawable.default_profile).into(binding.profileImage)
                            Toast.makeText(this, "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Erro ao atualizar perfil: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha no upload da imagem: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditNameDialog() {
        val editText = EditText(this).apply {
            hint = "Digite seu novo nome"
            setText(binding.profileName.text?.toString() ?: "")
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Nome")
            .setView(editText)
            .setPositiveButton("Salvar") { dialog, _ ->
                val novoNome = editText.text.toString().trim()
                if (novoNome.isEmpty()) {
                    Toast.makeText(this, "Nome não pode ser vazio", Toast.LENGTH_SHORT).show()
                } else {
                    atualizarNomeFirebase(novoNome)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun atualizarNomeFirebase(novoNome: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val updates = userProfileChangeRequest { displayName = novoNome }
            user.updateProfile(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.profileName.text = novoNome
                    Toast.makeText(this, "Nome atualizado com sucesso!", Toast.LENGTH_SHORT).show()

                    // Atualiza no Firestore também
                    firestore.collection("users").document(user.uid).update("name", novoNome)
                } else {
                    Toast.makeText(this, "Erro ao atualizar nome: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
    }

    private fun showEditAddressDialog() {
        val editText = EditText(this).apply {
            hint = "Digite seu endereço"
            setText(binding.profileAddress.text?.toString() ?: "")
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Endereço")
            .setView(editText)
            .setPositiveButton("Salvar") { dialog, _ ->
                val novoEndereco = editText.text.toString().trim()
                if (novoEndereco.isEmpty()) {
                    Toast.makeText(this, "Endereço não pode ser vazio", Toast.LENGTH_SHORT).show()
                } else {
                    salvarDadosExtrasDoPerfil(novoEndereco, binding.profilePhone.text?.toString() ?: "")
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showEditPhoneDialog() {
        val editText = EditText(this).apply {
            hint = "Digite seu telefone"
            setText(binding.profilePhone.text?.toString() ?: "")
            inputType = InputType.TYPE_CLASS_PHONE
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Telefone")
            .setView(editText)
            .setPositiveButton("Salvar") { dialog, _ ->
                val novoTelefone = editText.text.toString().trim()
                if (novoTelefone.isEmpty()) {
                    Toast.makeText(this, "Telefone não pode ser vazio", Toast.LENGTH_SHORT).show()
                } else {
                    salvarDadosExtrasDoPerfil(binding.profileAddress.text?.toString() ?: "", novoTelefone)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showEditEmailDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val emailInput = EditText(this).apply {
            hint = "Novo email"
            setText(binding.profileEmail.text?.toString() ?: "")
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        val passwordInput = EditText(this).apply {
            hint = "Digite sua senha para confirmar"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        layout.addView(emailInput)
        layout.addView(passwordInput)

        AlertDialog.Builder(this)
            .setTitle("Editar Email")
            .setView(layout)
            .setPositiveButton("Salvar") { dialog, _ ->
                val novoEmail = emailInput.text.toString().trim()
                val senha = passwordInput.text.toString()

                if (novoEmail.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(this, "Email e senha são obrigatórios", Toast.LENGTH_SHORT).show()
                } else {
                    reauthenticateAndChangeEmail(novoEmail, senha)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun reauthenticateAndChangeEmail(novoEmail: String, senha: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(user.email ?: "", senha)
        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                user.updateEmail(novoEmail).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        binding.profileEmail.text = novoEmail
                        Toast.makeText(this, "Email atualizado com sucesso!", Toast.LENGTH_SHORT).show()

                        // Atualiza no Firestore também
                        firestore.collection("users").document(user.uid).update("email", novoEmail)
                    } else {
                        Toast.makeText(this, "Falha ao atualizar email: ${updateTask.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Falha na autenticação: ${reauthTask.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
