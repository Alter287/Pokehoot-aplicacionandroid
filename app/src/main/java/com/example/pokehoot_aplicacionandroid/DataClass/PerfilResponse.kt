package com.example.pokehoot_aplicacionandroid.DataClass

data class PerfilResponse(
    val success: Boolean,
    val perfil: PerfilData?,
    val error: String?
)