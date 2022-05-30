package com.bangkit.sibisa.pref

import android.content.Context

class UserPreference(context: Context) {
    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val TOKEN = "token"
        private const val USER_ID = "user_id"
    }
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUserID(value: String) {
        val editor = preferences.edit()
        editor.putString(USER_ID, value)
        editor.apply()
    }

    fun setToken(value: String) {
        val editor = preferences.edit()
        editor.putString(TOKEN, value)
        editor.apply()
    }

    fun getToken(): String? {
        return preferences.getString(TOKEN, "")
    }

    fun getUserID(): String? {
        return preferences.getString(USER_ID, "")
    }

    fun clearUser() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }


}