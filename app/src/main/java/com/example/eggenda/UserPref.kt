package com.example.eggenda

import android.content.Context
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE

object UserPref {
    private const val PREF_NAME = "id_user"

    // Function to get the user ID from SharedPreferences
    fun getId(context: Context): Long {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getLong("curr_id", 0L)
    }

    // Function to get the username from SharedPreferences
    fun getUsername(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("curr_username", "")
    }

    fun updateUsername(context: Context, newName: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("curr_username", newName)
        editor.apply()
    }

    fun setIdUser(context: Context, username: String, id: Long) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("curr_id", id)
        editor.putString("curr_username", username)
        editor.apply()
    }
}