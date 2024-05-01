package com.example.taller3_compumovil

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taller3_compumovil.R
import com.example.taller3_compumovil.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var bindingLogin: ActivityLoginBinding

    private lateinit var autenticacion: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindingLogin = ActivityLoginBinding.inflate(layoutInflater)
        autenticacion = FirebaseAuth.getInstance()

        val email = bindingLogin.editTextEmail.text.toString()
        val password = bindingLogin.editTextPassword.text.toString()

        bindingLogin.loginbutton.setOnClickListener {
            Log.i("CORRECCION", "Botón oprimido")
            iniciarSesion(email, password)
        }

        bindingLogin.registerbutton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("EMAIL", email)
            intent.putExtra("PASSWORD", password)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val usuarioActual = autenticacion.currentUser
        //updateUI(usuarioActual)
    }

    private fun iniciarSesion(email: String, password: String) {
        Log.i("CORRECCION", "Ingreso a inicioSesion")
        if (validarCampos() && emailValido(email)) {
            Log.i("CORRECCION", "Ingreso al if")
            autenticacion.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    Log.i("CORRECCION", "Ingreso al lambda")
                    Log.d(TAG, "inicioCorreoSesion:onComplete:" + task.isSuccessful)
                    if (task.isSuccessful) {
                        Log.d(TAG, "inicioCorreoSesion: success")
                        val usuario = autenticacion.currentUser
                        updateUI(usuario)
                    } else {
                        Log.w(TAG, "inicioCorreoSesion: failure", task.exception)
                        Toast.makeText(this, "Inicio de sesión fallido", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
    }

    private fun validarCampos(): Boolean {
        var valid = true
        val email = bindingLogin.editTextEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            bindingLogin.editTextEmail.error = "Requerido."
            valid = false
        } else {
            bindingLogin.editTextEmail.error = null
        }

        val password = bindingLogin.editTextPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            bindingLogin.editTextPassword.error = "Requerido."
            valid = false
        } else {
            bindingLogin.editTextPassword.error = null
        }
        return valid
    }

    private fun emailValido(email: String): Boolean {
        if (!email.contains("@") || !email.contains(".") || email.length < 5) {
            return false
        }
        return true
    }

    private fun updateUI(usuario: FirebaseUser?) {
        if(usuario != null) {
            val intent = Intent(this, MapaActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            }
    }

}
