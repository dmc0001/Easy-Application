package com.example.easy.data

data class JobInformation(
    val uid :String?,
    val jobTitle: String,
    val jobCategory: String,
    val jobDescription: String,
    val jobSkills: List<String>?,
    val jobImages: List<String>?,
    val resumeEmployer:String?,
    val price:String?,
){
    constructor() : this("","","","", emptyList(), emptyList(),"","")
}