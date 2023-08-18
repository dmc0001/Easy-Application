package com.example.easy.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.easy.R
import com.example.easy.databinding.FragmentRegisterOrLoginBinding

class RegisterOrLoginFragment : Fragment() {
    private lateinit var binding: FragmentRegisterOrLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterOrLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnLogin.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_registerOrLoginFragment_to_loginFragment)
            }
            btnRegister.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_registerOrLoginFragment_to_registerFragment)

            }
        }
    }

}