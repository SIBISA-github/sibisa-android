package com.bangkit.sibisa.di

import android.content.Context
import com.bangkit.sibisa.pref.UserPreference
import com.bangkit.sibisa.repository.AuthRepository
import com.bangkit.sibisa.retrofit.RetrofitConfig
import com.bangkit.sibisa.retrofit.RetrofitService

object Injection {
    fun provideService(context: Context): RetrofitService {
        return RetrofitConfig.getApiService(context)
    }

    fun provideRepository(context: Context): AuthRepository {
        return AuthRepository(provideService(context))
    }

    fun providePreferences(context: Context): UserPreference {
        return UserPreference(context)
    }
}