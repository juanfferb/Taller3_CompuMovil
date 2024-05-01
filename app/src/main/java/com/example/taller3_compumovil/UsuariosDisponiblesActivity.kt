package com.example.taller3_compumovil

import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taller3_compumovil.UsuariosDisponiblesAdapter
import com.example.taller3_compumovil.R

class UsuariosDisponiblesActivity : AppCompatActivity() {
    var mProjection: Array<String>? = null
    var mCursor: Cursor? = null
    var mUsuariosAdapter: UsuariosDisponiblesAdapter? = null
    var mlista: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios_disponibles)

        // 1. Variables
        mlista = findViewById(R.id.listaUsuarios)

        //2. Proyección
        mProjection =
            arrayOf(ContactsContract.Profile._ID, ContactsContract.Profile.DISPLAY_NAME_PRIMARY)

        //3. Adaptador
        mUsuariosAdapter = UsuariosDisponiblesAdapter(this, null, 0)
        mlista?.adapter = mUsuariosAdapter
        initView()
    }

    fun initView() {
        mCursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, mProjection, null, null, null
        )
        mUsuariosAdapter?.changeCursor(mCursor)
    }
}
