package com.example.easy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.easy.R
import com.example.easy.databinding.ActivityClientBinding
import com.example.easy.databinding.ActivityEmployerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployerActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityEmployerBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navController = findNavController(R.id.employerHostFragment)
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}