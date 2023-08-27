package com.example.easy.data

sealed class Category(val category: String) {
    object Education : Category("Education")
    object LawAndGovernment : Category("Law and government")
    object HealthCare : Category("Health care")
    object ServiceIndustry : Category("Service industry")
    object Transportation : Category("Transportation")
    object Construction : Category("Construction")
    object Manufacturing : Category("Manufacturing")
    object BusinessAdministration : Category("Business administration")
    object Technology : Category("Technology")
}