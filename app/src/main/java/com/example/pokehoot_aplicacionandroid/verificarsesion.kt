package com.example.pokehoot_aplicacionandroid

import android.content.Context

object VerificarSesion {
    fun estaSesionActiva(context: Context): Boolean {
        val prefs = context.getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        return !token.isNullOrEmpty()
    }
}