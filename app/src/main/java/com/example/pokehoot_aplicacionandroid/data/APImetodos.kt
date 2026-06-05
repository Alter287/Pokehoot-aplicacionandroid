package com.example.pokehoot_aplicacionandroid.data

import com.example.pokehoot_aplicacionandroid.DataClass.IntentarLogear
import com.example.pokehoot_aplicacionandroid.DataClass.PerfilResponse
import com.example.pokehoot_aplicacionandroid.DataClass.RegistroRequest
import com.example.pokehoot_aplicacionandroid.DataClass.RegistroResponse
import com.example.pokehoot_aplicacionandroid.DataClass.SalasResponse
import com.example.pokehoot_aplicacionandroid.DataClass.respuestalogeo
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.Path

interface APImetodos {
    @POST("login")
    suspend fun login(@Body request: IntentarLogear): respuestalogeo

    @POST("registro")
    suspend fun registro(@Body request: RegistroRequest): RegistroResponse

    @GET("salas")
    suspend fun getSalas(): SalasResponse

    @GET("perfil/{userId}")
    suspend fun getPerfil(@Path("userId") userId: Int): PerfilResponse
}