package com.example.eggenda


import android.content.Context
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE


object UserPref {
    private const val PREF_NAME = "id_user"

    // Function to get the user ID from SharedPreferences
    fun getId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("curr_id", "")
    }


    // Function to get the username from SharedPreferences
    fun getUsername(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("curr_username", "")
    }

    fun getPassword(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("password", "")
    }

    fun getPoints(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt("points", 0)
    }


    //updates in sharedpref
    fun updateUsername(context: Context, newName: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("curr_username", newName)
        editor.apply()
    }


    fun setUser(context: Context, username: String, id: String?, password: String?, points: Int?) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("curr_id", id)
        editor.putString("curr_username", username)
        editor.putString("password", password)
        if (points != null) {
            editor.putInt("points", points)
        }
        editor.apply()
    }
}
