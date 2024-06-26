package com.example.taller3_compumovil.Activities
import android.content.Context
import android.content.Intent
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.taller3_compumovil.Data.Usuario
import com.example.taller3_compumovil.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UsuariosDisponiblesAdapter(
    context: Context,
    private val userList: List<Usuario>,
    private val fotoUrlList: List<String> // Lista de URLs de las fotos de los usuarios
) : ArrayAdapter<Usuario>(context, 0, userList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                R.layout.activity_usuarios_disponibles_adapter,
                parent,
                false
            )
        }

        val usuario = userList[position]
        val fotoUrl = fotoUrlList[position] // Obtener la URL de la foto correspondiente al usuario
        val foto = if (fotoUrl.isEmpty()) R.drawable.contactos_de_google else fotoUrl
        // Mostrar la foto de perfil
        val ivFotoPerfil = view!!.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.fotoUsuario)

        Glide.with(context)
            .load(foto) // Utilizar la URL de la foto
            .placeholder(R.drawable.placeholder_image) // Placeholder mientras se carga la imagen
            .error(R.drawable.error_image) // Imagen de error si la carga falla
            .into(ivFotoPerfil)

        // Mostrar el nombre de usuario
        view.findViewById<TextView>(R.id.nombreUsuario).text = usuario.nombre

        // Manejar el clic en el botón
        view.findViewById<Button>(R.id.ubicacionUsuario).setOnClickListener {
            val intent = Intent(context, MapaV2Activity::class.java)
            intent.putExtra("latitud", usuario.latitud)
            intent.putExtra("longitud", usuario.longitud)
            intent.putExtra("uid", usuario.uid)

            context.startActivity(intent)
        }

        return view
    }
}
