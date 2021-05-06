package com.liviolopez.pricechecker.data.remote

import com.liviolopez.pricechecker.data.remote.response.ApiItemsResponse
import retrofit2.http.GET

// https://liviolopez.com/api/price-checker

interface ApiService {
    @GET("api/price-checker")
    suspend fun getItems(): ApiItemsResponse
}