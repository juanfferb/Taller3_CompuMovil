package com.example.taller3_compumovil

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.taller3_compumovil.Data.Usuario
import com.example.taller3_compumovil.R
import com.example.taller3_compumovil.databinding.ActivityFirebaseBinding
import com.example.taller3_compumovil.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream

class RegisterActivity : AppCompatActivity() {
    private val PATH_USERS = "users/"
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var myRef: DatabaseReference
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("EMAIL")
        val password = intent.getStringExtra("PASSWORD")

        val button = findViewById<Button>(R.id.registerbutton)
        button.setOnClickListener {

            val nombre = findViewById<EditText>(R.id.editTextName).text.toString()
            val apellido = findViewById<EditText>(R.id.editTextLastName).text.toString()
            val edadText = findViewById<EditText>(R.id.editTextEdad).text.toString()
            val numeroIdText = findViewById<EditText>(R.id.editTextId).text.toString()

            val edad = edadText.toIntOrNull()
            val numeroId = numeroIdText.toIntOrNull()

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && edad != null && edad > 0 && numeroId != null && numeroId > 0) {
                if (email != null) {
                    if (password != null) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    val userId = user?.uid
                                    val usr1 = Usuario(nombre, apellido, edad, numeroId)
                                    if (userId != null) {
                                        database.getReference(PATH_USERS + userId).setValue(usr1)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(
                                                        this,
                                                        "Usuario registrado exitosamente",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    loadUsers()
                                                } else {
                                                    Toast.makeText(
                                                        this,
                                                        "Error al registrar usuario en la base de datos",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                    }
                                } else {
                                    Toast.makeText(
                                        baseContext, "Error al registrar usuario: ${task.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }
            } else {
                Toast.makeText(
                    baseContext, "Por favor, complete todos los campos correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        findViewById<Button>(R.id.buttonSelectImage).setOnClickListener {

            val imageView = findViewById<ImageView>(R.id.imageViewContact)
            val imageUri: Uri? = getImageUriFromImageView(imageView)

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {

                val storage = FirebaseStorage.getInstance()
                val storageRef = storage.reference.child("images/$userId/profile.jpg")
                val uploadTask = storageRef.putFile(imageUri!!)

                uploadTask.addOnSuccessListener { taskSnapshot ->
                    // Imagen subida con éxito, obtener la URL de descarga
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        // Guardar la URL de la imagen en la base de datos
                        database.getReference("users/$userId/profileImageUrl").setValue(imageUrl)
                            .addOnSuccessListener {
                                // Imagen y URL guardadas con éxito en la base de datos
                                // Aquí puedes continuar con el resto del proceso de registro
                            }
                            .addOnFailureListener { e ->
                                // Error al guardar la URL de la imagen en la base de datos
                            }
                    }
                }.addOnFailureListener { e ->
                    // Error al subir la imagen a Firebase Storage
                }
            } else {
                // No hay usuario autenticado
                Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getImageUriFromImageView(imageView: ImageView): Uri? {
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val file = File.createTempFile("image", ".jpg", applicationContext.cacheDir)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return FileProvider.getUriForFile(applicationContext, applicationContext.packageName + ".provider", file)
        } else {
            return null
        }
    }


    private fun loadUsers() {
        val usersRef = database.getReference(PATH_USERS)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    val myUser = singleSnapshot.getValue(Usuario::class.java)
                    myUser?.let {
                        val name = it.nombre
                        val age = it.edad
                        Toast.makeText(baseContext, "$name: $age", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    baseContext, "Error en la consulta",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

