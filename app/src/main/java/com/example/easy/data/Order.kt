package com.example.easy.data

data class Order(
    val jobInformation: JobInformation,
    var description:String,
    var date:String,
    var location:String
)
{
    constructor():this(JobInformation(),"","","")
}