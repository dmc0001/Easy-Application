package com.example.easy.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val jobTitle:String,
    val jobInformationUid: String,
    var description: String ,
    var date: String ,
    var location: String
): Parcelable {
    constructor() : this( "", "", "", "","")
}