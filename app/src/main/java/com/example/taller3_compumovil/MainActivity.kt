package com.example.taller3_compumovil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_compumovil.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginbutton.setOnClickListener { startActivity(Intent(this, MapaActivity::class.java)) }
        binding.registerbutton.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }
}