package com.example.taller3_compumovil.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taller3_compumovil.Data.Datos
import com.example.taller3_compumovil.Data.Usuario
import com.example.taller3_compumovil.R
import com.example.taller3_compumovil.databinding.ActivityMapaV2Binding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject
import kotlin.math.pow

class MapaV2Activity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapaV2Binding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth
    private var usrLocation: LatLng? = null
    private var myLocation: LatLng? = null
    private var usrUid: String? = null
    private var myMarker: Marker? = null
    //Realtime Database
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    val PATH_USERS="users/"

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapaV2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        myRef = database.getReference(PATH_USERS)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    myLocation = LatLng(location.latitude, location.longitude)
                    val distanceBetween = String.format("%.2f", distance(myLocation!!.latitude, myLocation!!.longitude, usrLocation!!.latitude, usrLocation!!.longitude))
                    binding.tvDistancia.text = "Distancia hasta su ubicación: " + distanceBetween + " metros"
                }
            }

        //Recibir datos del usuario
        val intent = intent
        val userLatitud = intent.getDoubleExtra("latitud", 0.0)
        val userLongitud = intent.getDoubleExtra("longitud", 0.0)
        val userUid = intent.getStringExtra("uid")
        usrLocation = LatLng(userLatitud, userLongitud)
        usrUid = userUid
        myMarker = mMap.addMarker(MarkerOptions().position(usrLocation!!).title("Usuario selecionado"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usrLocation!!, 15f))
        //Revisar permisos
        checkLocationPermission()
        loadAvailableUsers()
    }

    private fun loadAvailableUsers() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userFolderSnapshot in dataSnapshot.children) {
                    // Aquí, cada "userFolderSnapshot" representa una carpeta de usuario individual en la carpeta "/users"
                    val user = userFolderSnapshot.child("/").getValue(Usuario::class.java)
                    if(user!!.uid.equals(usrUid)){
                        //Borrar marcador anterior
                        if(myMarker != null){
                            myMarker!!.remove()
                        }
                        usrLocation = LatLng(user.latitud, user.longitud)
                        myMarker = mMap.addMarker(MarkerOptions().position(usrLocation!!).title("Ubicación del usuario seleccionado"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usrLocation!!, 15f))
                        //Actualizar distancia
                        val distanceBetween = String.format("%.2f", distance(myLocation!!.latitude, myLocation!!.longitude, usrLocation!!.latitude, usrLocation!!.longitude))
                        binding.tvDistancia.text = "Distancia hasta su ubicación: " + distanceBetween + " metros"
                    }
                    //Toast.makeText(this@UsuariosDisponiblesActivity, "Estado: " + user!!.estado, Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores de cancelación
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Datos.MY_PERMISSION_REQUEST_LOCATION){// Nuestros permisos
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){ // Validar que si se aceptaron ambos permisos
                // Permisos aceptados

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


        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            //El usuario ya ha rechazado los permisos
            Toast.makeText(this, "Permisos denegados :(", Toast.LENGTH_SHORT).show()
        }else{
            //Pedir permisos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                Datos.MY_PERMISSION_REQUEST_LOCATION
            )
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            // Navigate to the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371 // Radio medio de la Tierra en kilómetros

        val lat1Radians = Math.toRadians(lat1)
        val lon1Radians = Math.toRadians(lon1)
        val lat2Radians = Math.toRadians(lat2)
        val lon2Radians = Math.toRadians(lon2)

        val dlon = lon2Radians - lon1Radians
        val dlat = lat2Radians - lat1Radians

        val a = Math.sin(dlat / 2)
            .pow(2) + Math.cos(lat1Radians) * Math.cos(lat2Radians) * Math.sin(dlon / 2).pow(2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        val distanceInKm = earthRadius * c
        return distanceInKm * 1000 // Convertir a metros
    }
}