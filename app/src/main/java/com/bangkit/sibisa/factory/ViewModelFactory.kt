package com.bangkit.sibisa.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.sibisa.di.Injection
import com.bangkit.sibisa.ui.leaderboard.LeaderboardViewModel
import com.bangkit.sibisa.ui.lesson.QuizViewModel
import com.bangkit.sibisa.ui.login.LoginViewModel
import com.bangkit.sibisa.ui.profile.ProfileViewModel
import com.bangkit.sibisa.ui.register.RegisterViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            with(Injection) {
                return LoginViewModel(provideAuthRepository(context)) as T
            }
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            with(Injection) {
                return RegisterViewModel(provideAuthRepository(context)) as T
            }
        } else if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            with(Injection) {
                return LeaderboardViewModel(provideProfileRepository(context)) as T
            }
        } else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            with(Injection) {
                return ProfileViewModel(provideProfileRepository(context)) as T
            }
        } else if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            with(Injection) {
                return QuizViewModel(provideQuizRepository(context)) as T
            }
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}