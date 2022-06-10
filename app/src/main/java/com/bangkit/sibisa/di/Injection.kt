package com.bangkit.sibisa.di

import android.content.Context
import com.bangkit.sibisa.repository.AuthRepository
import com.bangkit.sibisa.repository.ProfileRepository
import com.bangkit.sibisa.repository.QuizRepository
import com.bangkit.sibisa.retrofit.RetrofitConfig
import com.bangkit.sibisa.retrofit.RetrofitService

object Injection {
    private fun provideHerokuService(context: Context): RetrofitService {
        return RetrofitConfig.getHerokuApiService(context)
    }

    private fun provideGCPService(context: Context): RetrofitService {
        return RetrofitConfig.getApiService(context)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        return AuthRepository(provideGCPService(context))
    }

    fun provideProfileRepository(context: Context): ProfileRepository {
        return ProfileRepository(provideGCPService(context), provideHerokuService(context))
    }

    fun provideQuizRepository(context: Context): QuizRepository {
        return QuizRepository(provideGCPService(context))
    }

}