package com.example.eggenda.gamePetChoose

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.eggenda.UserPref
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferenceManager (context: Context) {

    val userSharePref: SharedPreferences = context.getSharedPreferences("user_${UserPref.getId(context)}", Context.MODE_PRIVATE)

    //the amount that the pets are owned now : hatching -> choose (not done)
    val sharedPreferences : SharedPreferences= context.getSharedPreferences("eggenda_prefs", Context.MODE_PRIVATE)

    //the amount of pets that can use :  monster -> choose  (done)
    val maxAmountPetsSP:SharedPreferences = context.getSharedPreferences("Max_pets_allow", Context.MODE_PRIVATE)

    val filteredAmtSP : SharedPreferences = context.getSharedPreferences("FilteredInfo", Context.MODE_PRIVATE)

    //the fighting pets list : choose -> gameplay  (not done)
    private val fightingPetsSP : SharedPreferences = context.getSharedPreferences("PetsToFight", Context.MODE_PRIVATE)

    //the stage choose:
    private val stageSP: SharedPreferences = context.getSharedPreferences("stageChoose", Context.MODE_PRIVATE)

    //gson that to make the arrays into string ans saved into shared preference
    private val gson = Gson()

    fun savePetOwnership(petOwnership: Array<Int>) {
//        val editor = sharedPreferences.edit()
        val json = gson.toJson(petOwnership)

//        sharedPreferences.edit().putString("pet_ownership", json).apply()
        userSharePref.edit().putString("pet_ownership", json).apply()
    }

    fun getPetOwnership() : ArrayList<Int>{
        val json = userSharePref.getString("pet_ownership", null)
//        val json = sharedPreferences.getString("pet_ownership", null)
        val type = object : TypeToken<ArrayList<Int>>() {}.type
        return if (json != null){
            gson.fromJson(json,type)
        } else {
            ArrayList()
        }
    }

    //get the total number that pets owned by user
    fun getPetOwnershipNum() : Int {
        val petOwnership : ArrayList<Int> = getPetOwnership()
        val ownedPets : IntArray = petOwnership.toIntArray()
        var filteredPetsList = ownedPets.filterIndexed { index, _->
            index < ownedPets.size && ownedPets[index] == 1
        }

        return filteredPetsList.size

    }

    //get the total number that use done stage
    fun getStageDoneNum () : Int{
        val getStageDone : ArrayList<Int> = getStageDone()
        val stageDone: IntArray = getStageDone.toIntArray()

        var filteredStageList = stageDone.filterIndexed { index, _->
            index < stageDone.size && stageDone[index] == 1
        }
        return filteredStageList.size
    }


    fun saveFilteredPetsAmount (filteredAmt : Int){
//        filteredAmtSP.edit().putInt("filtered_pets", filteredAmt).apply()
        userSharePref.edit().putInt("filtered_pets", filteredAmt).apply()
    }

    fun getFilteredPetsAmount() : Int{
        val filteredAmt =  userSharePref.getInt("filtered_pets", 3)
//        val filteredAmt =  filteredAmtSP.getInt("filtered_pets", 3)
        if(filteredAmt < 5){
            return filteredAmt
        } else {
            return 5
        }
    }

    //to save the amount of the max pets can choose
    fun savePetsAmount (petAmount: Int){
//        maxAmountPetsSP.edit().putInt("max_key", petAmount).apply()
        userSharePref.edit().putInt("max_key", petAmount).apply()
    }

    fun getPetsAmount() : Int{
//        return maxAmountPetsSP.getInt("max_key", 1)
        return userSharePref.getInt("max_key", 1)
    }

    //to save the list
    fun savePetsList (petsList: IntArray){
        val json = gson.toJson(petsList)
//        fightingPetsSP.edit().putString("selected_pets_key", json).apply()
        userSharePref.edit().putString("selected_pets_key", json).apply()
    }

    //retrive the list of pets id to shared preference
    fun getPetsList(): IntArray {
        val jsonString = userSharePref.getString("selected_pets_key", null)
//        val jsonString = fightingPetsSP.getString("selected_pets_key", null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<IntArray>() {}.type)  // Convert JSON string to IntArray
        } else {
            intArrayOf()  // Return empty array if nothing is saved
        }

    }

    @SuppressLint("CommitPrefEdits")
    fun saveStageChoose(stageId: Int){
//        stageSP.edit().putInt("stageId", stageId).apply()
        userSharePref.edit().putInt("stageId", stageId).apply()
    }

    fun getStageChoose():Int{
//        return stageSP.getInt("stageId", 0)
        return userSharePref.getInt("stageId", 0)
    }

    // for the stage done by the user. 0 = unfinished, 1 = finished
    fun saveStageDone(stageDone: ArrayList<Int>) {
//        val editor = sharedPreferences.edit()
        val json = gson.toJson(stageDone)
//        stageSP.edit().putString("stageDone", json).apply()
        return userSharePref.edit().putString("stageDone", json).apply()
    }

    fun getStageDone() : ArrayList<Int>{
        val json = userSharePref.getString("stageDone", null)
//        val json = stageSP.getString("stageDone", null)
        val type = object : TypeToken<ArrayList<Int>>() {}.type
        return if (json != null){
            gson.fromJson(json,type)
        } else {
            ArrayList()
        }
    }

}