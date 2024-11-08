package com.example.eggenda.gamePetChoose

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferenceManager (context: Context) {

    //the fighting pets list
    private val fightingPetsSP : SharedPreferences = context.getSharedPreferences("PetsToFight", Context.MODE_PRIVATE)

    //gson that to make the arrays into string ans saved into shared preference
    private val gson = Gson()

    //to save the list
    fun savePetsList (petsList: List<Int?>){
        val json = gson.toJson(petsList)
        fightingPetsSP.edit().putString("selected_pets_key", json).apply()
    }

    //retrive the list of pets id to shared preference
    fun getPetsListByPhotoID(): List<Int?> {
        val json = fightingPetsSP.getString("PetsToFight", null)
        return if(json != null){
            val type = object : TypeToken<List<Int?>> (){}.type
            gson.fromJson(json,type)

        }
        else {
            emptyList()
        }

    }



}