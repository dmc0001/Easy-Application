package com.example.easy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.easy.R
import com.example.easy.databinding.ActivityClientBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class ClientActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityClientBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        val navController = findNavController(R.id.clientHostFragment)
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}