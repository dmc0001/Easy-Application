package com.example.easy.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class JobInformation(
    val uid :String?,
    val jobTitle: String,
    val jobCategory: String,
    val jobDescription: String,
    val jobSkills: List<String>?,
    val jobImages: List<String>?,
    val location:String,
    val price:String?,
):Parcelable{
    constructor() : this("","","","", emptyList(), emptyList(),"","")
}
