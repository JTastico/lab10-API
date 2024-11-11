package com.example.lab10.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    private val retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL) // Aquí se debe agregar la barra al final de la URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: SerieApiService by lazy {
        retrofit.create(SerieApiService::class.java)
    }
}
