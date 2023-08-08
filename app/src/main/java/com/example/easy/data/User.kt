package com.example.easy.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val firstName :String,
    val lastName :String,
    val email:String,
    val phoneNumber :String,
    val role :String = "",
    val imagePath :String =""
): Parcelable
{
    constructor() : this("","","","","","")
}
