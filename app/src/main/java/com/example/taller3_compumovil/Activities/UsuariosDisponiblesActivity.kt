package com.example.taller3_compumovil.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_compumovil.Data.Usuario
import com.example.taller3_compumovil.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.delay
import java.io.File

class UsuariosDisponiblesActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var userList: MutableList<Usuario>
    private lateinit var fotoUrlList: MutableList<String>
    private var mUsuariosAdapter: UsuariosDisponiblesAdapter? = null
    private var mlista: ListView? = null
    //Realtime Database
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    val PATH_USERS="users/"
    //Storage
    private lateinit var storage: FirebaseStorage

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios_disponibles)

        myRef = database.getReference(PATH_USERS)
        storage = Firebase.storage("gs://taller3-compumovil-deed2.appspot.com")

        userList = mutableListOf()

        // Crear una lista vacía de URLs de fotos
        fotoUrlList = mutableListOf<String>()

        // Inicializar la lista y el adaptador
        mlista = findViewById(R.id.listaUsuarios)
        mUsuariosAdapter = UsuariosDisponiblesAdapter(this, userList, fotoUrlList)
        mlista?.adapter = mUsuariosAdapter

        //val fotoUrl = "url_de_la_foto_del_usuario"
        //fotoUrlList.add(fotoUrl)

        // Cargar la lista de usuarios disponibles
        loadAvailableUsers()

    }

    private fun loadAvailableUsers() {
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userFolderSnapshot in dataSnapshot.children) {
                    // Aquí, cada "userFolderSnapshot" representa una carpeta de usuario individual en la carpeta "/users"
                    val user = userFolderSnapshot.child("/").getValue(Usuario::class.java)
                    //Toast.makeText(this@UsuariosDisponiblesActivity, "Estado: " + user!!.estado, Toast.LENGTH_SHORT).show()
                    user?.let {
                        // Aquí puedes trabajar con los datos del usuario
                        if (it.estado) userList.add(it)
                    }
                }
                downloadFiles()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores de cancelación
            }
        })
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            // Navigate to the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun downloadFiles() {

        for(user in userList){
            if(user.profileImgUrl.isEmpty()){
                fotoUrlList.add("")
                mUsuariosAdapter?.notifyDataSetChanged()
                continue
            }
            val localFile = File.createTempFile("profile_image", "jpeg")
            val imageRef = storage.reference.child("images/profile/${user.uid}/${user.profileImgUrl}")
            fotoUrlList.add(localFile.absolutePath)

            imageRef.getFile(localFile)
                .addOnSuccessListener { taskSnapshot ->
                    // Successfully downloaded data to local file
                    //fotoUrlList.add(localFile.absolutePath)
                    // Notificar al adaptador que los datos han cambiado
                    mUsuariosAdapter?.notifyDataSetChanged()
                    Log.i("FBApp", "succesfully downloaded")
                    // Update UI using the localFile
                }.addOnFailureListener { exception ->
                    // Handle failed download
                    // ...
                }

        }

    }
}
