package com.example.pokehoot_aplicacionandroid.DataClass

data class respuestalogeo(
    val success: Boolean,
    val token: String?,
    val username: String?,
    val userId: Int?,
    val error: String?
)