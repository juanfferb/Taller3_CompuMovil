package com.example.taller3_compumovil

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.example.taller3_compumovil.R

class UsuariosDisponiblesAdapter (context: Context?, c: Cursor?, flags: Int) : CursorAdapter(context, c, flags) {

    private val FOTO_ID_INDEX = 0
    private val DISPLAY_NAME_INDEX = 1

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(
            R.layout
            .activity_usuarios_disponibles_adapter, parent, false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val tvfotoPerfil = view?.findViewById<TextView>(R.id.idContacto)
        val tvNombre = view?.findViewById<TextView>(R.id.nombre)
        val foto = cursor?.getInt(FOTO_ID_INDEX)
        val nombre = cursor?.getString(DISPLAY_NAME_INDEX)
        tvNombre?.text = nombre

    }
}