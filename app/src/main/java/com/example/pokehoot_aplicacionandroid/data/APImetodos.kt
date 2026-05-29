package com.example.pokehoot_aplicacionandroid.data

import com.example.pokehoot_aplicacionandroid.DataClass.IntentarLogear
import com.example.pokehoot_aplicacionandroid.DataClass.respuestalogeo
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Call
interface APImetodos {
    @POST("login")
    suspend fun login(@Body request: IntentarLogear): respuestalogeo

}