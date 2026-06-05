package com.example.pokehoot_aplicacionandroid.DataClass

data class RegistroResponse(
    val success: Boolean,
    val usuario: UsuarioData?,
    val error: String?
)