package com.example.pokehoot_aplicacionandroid.DataClass

data class EstadisticasData(
    val partidasJugadas: Int,
    val partidasGanadas: Int,
    val preguntasCorrectas: Int,
    val puntuacionTotal: Int,
    val mejorPuntuacion: Int,
    val rachaActual: Int,
    val mejorRacha: Int
)