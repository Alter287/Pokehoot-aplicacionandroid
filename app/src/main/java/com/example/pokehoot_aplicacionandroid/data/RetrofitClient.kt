package com.example.pokehoot_aplicacionandroid.data

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okio.Buffer

object RetrofitClient {
    private const val BASE_URL = "https://pokehoot-server-production.up.railway.app/"
    private const val API_KEY = "hola"
    // interceptor pàra colocar la apikey en todas las peticones de la api  automaticamente
    private val apiKeyInterceptor = Interceptor { chain ->
        val original = chain.request()

        val buffer = okio.Buffer()
        original.body?.writeTo(buffer)
        Log.d("nose", buffer.readUtf8())
        Log.d("nose", original.headers.toString())

        val  requestKey = chain.request().newBuilder()
            .addHeader("KEY",API_KEY)
            .build()
        chain.proceed(requestKey)

    }
    // http mezclado con la funcion anterior
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor (apiKeyInterceptor)
        .build()




    val instance: APImetodos by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(APImetodos::class.java)
    }
}