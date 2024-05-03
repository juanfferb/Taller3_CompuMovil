package com.example.taller3_compumovil.Activities

import android.content.Intent
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.taller3_compumovil.Data.Datos.Companion.GALLERY_REQUEST_CODE
import com.example.taller3_compumovil.Data.Datos.Companion.MY_PERMISSION_REQUEST_LOCATION
import com.example.taller3_compumovil.Data.Usuario
import com.example.taller3_compumovil.databinding.ActivityRegisterBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import java.io.File

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    //Auth
    private lateinit var auth: FirebaseAuth
    //Realtime Database
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    val PATH_USERS="users/"
    //Location permissions
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var myLocation: Location? = null
    //Storage
    private var selectedImageUri: Uri? = null
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el cliente de ubicación fusionada
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Revisar permisos
        checkLocationPermission()

        auth = Firebase.auth
        storage = Firebase.storage("gs://taller3-compumovil-deed2.appspot.com")

        binding.buttonSelectImage.setOnClickListener { openGallery() }

        binding.registerbutton.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseRegister", "createUserWithEmail:onComplete:" + task.isSuccessful)
                        val user = auth.currentUser
                        if (user != null) {
                            // Update user info
                            //val upcrb = UserProfileChangeRequest.Builder()
                            //upcrb.setDisplayName(binding.editTextName.text.toString() + " " + binding.editTextLastName.text.toString())
                            //upcrb.setPhotoUri(Uri.parse("path/to/pic")) //fake uri, use Firebase Storage
                            //user.updateProfile(upcrb.build())
                            var usuario = Usuario()
                            usuario.uid = user.uid
                            usuario.profileImgUrl = selectedImageUri?.lastPathSegment ?: ""
                            usuario.nombre = binding.editTextName.text.toString()
                            usuario.apellido = binding.editTextLastName.text.toString()
                            usuario.noIdentificacion = binding.editTextId.text.toString().toInt()
                            usuario.estado = true
                            if(myLocation != null){
                                usuario.latitud = myLocation!!.latitude
                                usuario.longitud = myLocation!!.longitude
                            }
                            myRef = database.getReference(PATH_USERS+user.uid)
                            myRef.setValue(usuario)
                            updateUI(user)
                            uploadFile()
                        }
                    } else {
                        Toast.makeText(this, "createUserWithEmail:Failure: " + task.exception.toString(),
                            Toast.LENGTH_SHORT).show()
                        task.exception?.message?.let { Log.e("FirebaseRegister", it) }
                    }
                }
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MY_PERMISSION_REQUEST_LOCATION){// Nuestros permisos
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){ // Validar que si se aceptaron ambos permisos
                // Permisos aceptados
                setMyLocation()
            }else{
                //El permiso no ha sido aceptado
                Toast.makeText(this, "Permisos denegados :(", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no tienes permisos, solicitarlos al usuario
            requestLocationPermission()
        } else {
            // Si tienes permisos
            // Get the last known location
            setMyLocation()

        }
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocation() {
        // Crear una solicitud de ubicación
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    myLocation = location
                }
            }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            //El usuario ya ha rechazado los permisos
            Toast.makeText(this, "Permisos denegados :(", Toast.LENGTH_SHORT).show()
        }else{
            //Pedir permisos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_REQUEST_LOCATION)
        }
    }

    private fun updateUI(user: FirebaseUser) {
        val intent = Intent(this, MapaActivity::class.java)
        intent.putExtra("user", user.email)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    // Método para abrir la galería y seleccionar una imagen
    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Obtiene la imagen seleccionada por el usuario
            selectedImageUri = data.data
            // Muestra la imagen seleccionada en el ImageView
            binding.imageViewContact.setImageURI(selectedImageUri)

            Toast.makeText(this, "Imagen seleccionada: " + selectedImageUri?.lastPathSegment, Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadFile() {
        if (selectedImageUri != null) {
            val imageRef = storage.reference.child("images/profile/${auth.currentUser?.uid}/${selectedImageUri?.lastPathSegment}")
            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    Log.i("FBApp", "Successfully uploaded image")
                }
                .addOnFailureListener { exception ->
                    Log.e("FBApp", "Failed to upload image", exception)
                }
        } else {
            Log.e("FBApp", "No image selected")
        }
    }
}

