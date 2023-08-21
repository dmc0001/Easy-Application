package com.example.easy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.easy.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employer)
    }
}