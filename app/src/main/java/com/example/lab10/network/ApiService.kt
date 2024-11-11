package com.example.lab10.network

import com.example.lab10.models.Item
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("api/items/")
    fun getItems(): Call<List<Item>>

    @POST("api/items/")
    fun createItem(@Body item: Item): Call<Item>

    @PUT("api/items/{id}/")
    fun updateItem(@Path("id") id: Int, @Body item: Item): Call<Item>

    @DELETE("api/items/{id}/")
    fun deleteItem(@Path("id") id: Int): Call<Void>
}