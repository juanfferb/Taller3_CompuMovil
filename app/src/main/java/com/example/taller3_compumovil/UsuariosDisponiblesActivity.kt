package com.example.taller3_compumovil

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_compumovil.Data.Usuario
import com.example.taller3_compumovil.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UsuariosDisponiblesActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersReference: DatabaseReference
    private lateinit var userList: MutableList<Usuario>
    private var mUsuariosAdapter: UsuariosDisponiblesAdapter? = null
    private var mlista: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios_disponibles)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersReference = database.reference.child("users")
        userList = mutableListOf()

        // Inicializar la lista y el adaptador
        mlista = findViewById(R.id.listaUsuarios)
        mUsuariosAdapter = UsuariosDisponiblesAdapter(this, userList)
        mlista?.adapter = mUsuariosAdapter

        // Cargar la lista de usuarios disponibles
        loadAvailableUsers()
    }

    private fun loadAvailableUsers() {
        usersReference.orderByChild("estado").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(Usuario::class.java)
                        user?.let {
                            userList.add(it)
                        }
                    }
                    // Notificar al adaptador que los datos han cambiado
                    mUsuariosAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejar errores de cancelación
                }
            })
    }
}
