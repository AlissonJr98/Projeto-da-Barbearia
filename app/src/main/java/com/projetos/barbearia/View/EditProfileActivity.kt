package com.projetos.barbearia.View

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.projetos.barbearia.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val auth = FirebaseAuth.getInstance()

    private val PICK_IMAGE_REQUEST = 123
    private var imageUri: Uri? = null  // Uri da nova foto selecionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = auth.currentUser
        if (user != null) {
            binding.editName.setText(user.displayName)
            binding.editEmail.setText(user.email)
            // Se quiser carregar foto:
            user.photoUrl?.let { uri ->
                Glide.with(this).load(uri).into(binding.imageEditProfile)
            }
        }

        binding.btnChangePhoto.setOnClickListener {
            openGalleryForImage()
        }

        binding.btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageUri = data?.data
            binding.imageEditProfile.setImageURI(imageUri)
        }
    }

    private fun saveProfile() {
        val newName = binding.editName.text.toString()
        val newEmail = binding.editEmail.text.toString()

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        // Atualizar nome e email
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Atualizar email requer reautenticação (simplificado aqui)
                user.updateEmail(newEmail).addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        // Se tem foto nova, envia para Storage
                        if (imageUri != null) {
                            uploadImageToStorage(user.uid, imageUri!!) { downloadUrl ->
                                val photoUpdate = UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUrl)
                                    .build()

                                user.updateProfile(photoUpdate).addOnCompleteListener { photoTask ->
                                    if (photoTask.isSuccessful) {
                                        Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Erro ao atualizar foto.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Erro ao atualizar email: ${emailTask.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Erro ao atualizar nome: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uploadImageToStorage(userId: String, imageUri: Uri, callback: (Uri) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("profileImages/$userId.jpg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao enviar imagem", Toast.LENGTH_SHORT).show()
            }
    }
}
