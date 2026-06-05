package com.example.pokehoot_aplicacionandroid

import android.content.Context

object VerificarSesion {

    fun estaSesionActiva(context: Context): Boolean {
        val prefs = context.getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        return !token.isNullOrEmpty()
    }

    fun cerrarSesion(context: Context) {
        val prefs = context.getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        return prefs.getString("token", null)
    }

    fun getUsername(context: Context): String? {
        val prefs = context.getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        return prefs.getString("username", null)
    }
}