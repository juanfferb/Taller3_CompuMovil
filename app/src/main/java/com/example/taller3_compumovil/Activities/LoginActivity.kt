package com.example.taller3_compumovil.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.taller3_compumovil.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.File


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.loginButton.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            if (validateForm()) {
                if(isEmailValid(email)){
                    signIn(email, password)
                } else {
                    Toast.makeText(this, "Invalid email.",
                        Toast.LENGTH_SHORT).show()
                    binding.editTextEmail.text.clear()
                    binding.editTextPassword.text.clear()
                }
            }

        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Sign in task
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful) {
                    Log.w("FirebaseLogin", "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    binding.editTextEmail.text.clear()
                    binding.editTextPassword.text.clear()
                }else{
                    Log.d("FirebaseLogin", "signInWithEmail:onComplete:" + task.isSuccessful)
                    val user = auth.currentUser
                    updateUI(user)
                }
            }
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email = binding.editTextEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.editTextEmail.error = "Required."
            valid = false
        } else {
            binding.editTextEmail.error = null
        }
        val password = binding.editTextPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.editTextPassword.error = "Required."
            valid = false
        } else {
            binding.editTextPassword.error = null
        }
        return valid
    }

    private fun isEmailValid(email: String): Boolean {
        if (!email.contains("@") ||
            !email.contains(".") ||
            email.length < 5)
            return false
        return true
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            // Navigate to the principal activity
            val intent = Intent(this, MapaActivity::class.java)
            intent.putExtra("user"
                , currentUser.email)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            binding.editTextEmail.text.clear()
            binding.editTextPassword.text.clear()
        }
    }
}