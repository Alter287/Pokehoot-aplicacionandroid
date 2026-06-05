package com.example.pokehoot_aplicacionandroid.DataClass

data class Salas(
    val codigo: String,
    val jugadoresActuales: Int,
    val jugadoresMaximos: Int = 8
)