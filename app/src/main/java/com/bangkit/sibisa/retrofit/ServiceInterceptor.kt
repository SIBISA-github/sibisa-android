package com.bangkit.sibisa.retrofit

import android.content.Context
import com.bangkit.sibisa.pref.UserPreference
import okhttp3.Interceptor
import okhttp3.Response

class ServiceInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if(request.header("No-Authentication") == null){
            val pref = UserPreference(context)
            val token = pref.getToken()

            if(!token.isNullOrEmpty())
            {
                val finalToken = "Bearer $token"
                request = request.newBuilder()
                    .addHeader("Authorization",finalToken)
                    .build()
            }
        }

        return chain.proceed(request)
    }
}