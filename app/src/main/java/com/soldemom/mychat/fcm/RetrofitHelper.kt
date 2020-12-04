package com.soldemom.mychat.fcm

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    companion object {
        fun getRetrofit() =
            Retrofit.Builder()
                .baseUrl("http://192.168.0.6:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}