package com.tsiambajojoh.mg.data

data class MotDePasse(
    val id: Long,
    val nom: String,
    val siteCle: String,
    val siteNom: String,
    val identifiant: String,
    val secret: String,
    val note: String,
    val horodatage: Long
)
