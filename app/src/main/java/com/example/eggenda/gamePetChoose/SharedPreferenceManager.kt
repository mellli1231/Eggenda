package com.example.eggenda.gamePetChoose

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferenceManager (context: Context) {

    //the amount of pets that can use
    val maxAmountPetsSP:SharedPreferences = context.getSharedPreferences("Max_pets_allow", Context.MODE_PRIVATE)

    //the fighting pets list
    private val fightingPetsSP : SharedPreferences = context.getSharedPreferences("PetsToFight", Context.MODE_PRIVATE)

    //gson that to make the arrays into string ans saved into shared preference
    private val gson = Gson()


    //to save the amount of the max pets can choose
    fun savePetsAmount (petAmount: Int){
        maxAmountPetsSP.edit().putInt("max_key", petAmount)
    }

    fun getPetsAmount() : Int{
        return maxAmountPetsSP.getInt("max_key", 3)
    }

    //to save the list
    fun savePetsList (petsList: IntArray){
        val json = gson.toJson(petsList)
        fightingPetsSP.edit().putString("selected_pets_key", json).apply()
    }

    //retrive the list of pets id to shared preference
    fun getPetsList(): IntArray {

        val jsonString = fightingPetsSP.getString("selected_pets_key", null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<IntArray>() {}.type)  // Convert JSON string to IntArray
        } else {
            intArrayOf()  // Return empty array if nothing is saved
        }

    }



}