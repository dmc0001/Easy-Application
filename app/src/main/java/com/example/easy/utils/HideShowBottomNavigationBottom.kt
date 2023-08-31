package com.example.easy.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.example.easy.R
import com.example.easy.activities.ClientActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNav(){
    val bottomNavigationView =
        (activity as ClientActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    bottomNavigationView.visibility = View.GONE
}
fun Fragment.showBottomNav(){
    val bottomNavigationView =
        (activity as ClientActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    bottomNavigationView.visibility = View.VISIBLE
}

