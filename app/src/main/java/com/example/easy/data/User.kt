package com.example.easy.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val firstName :String,
    val lastName :String,
    val email:String,
    val role :String ="",
    val phoneNumber :String ="",
    val imagePath :String =""
): Parcelable
{
    constructor() : this("","","","","","")
}
