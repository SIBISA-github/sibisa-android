package com.bangkit.sibisa.retrofit

import android.content.Context
import com.bangkit.sibisa.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConfig {
    companion object {
        private var BASE_URL = "https://api-sibisa.herokuapp.com/api/v1/"
        private var PROD_URL = "https://sibisa-351215.et.r.appspot.com/api/v1"

        fun getApiService(context: Context): RetrofitService {
            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            val serviceInterceptor = ServiceInterceptor(context)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(serviceInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(PROD_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(RetrofitService::class.java)
        }
    }
}