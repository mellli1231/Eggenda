package com.example.eggenda.gamePlay


import com.example.eggenda.R
import kotlin.math.abs

class petInfo {

    private val TOTAL = 5
    //Elements
    private val FIRE = "Fire"
    private val WATER = "Water"
    private val FOREST = "Forest"

    // IDs for pets
    private val CAT = 0
    private val WORM = 1
    private val TIGER = 2
    private val DRAGON = 3
    private val MEWTWO = 4



    data class PetInfo(
        val name: String,
        val imageId: Int,  // Assuming this is a drawable resource ID
        val element: Int,
        val type: Int,
        val damage: Int,
        val count: Int,
        val description: String
    )

    private val catInfo = PetInfo(
        name = "Cat",
        imageId = R.drawable.game_cat,  // Replace with actual drawable resource ID
        element = dict.ELEMENT_FIRE,
        type = dict.ATK_TYPE_STAY,
        damage = 30,
        count = 3,
        description =""
    )

//    // Function to get the pet info by ID
//    fun getViewInfoById(id: Int): petViewInfo? {
//        return petInfoMap[id]?.invoke() // Returns an instance of the specified pet class
//    }

    //deal 10 damage every 3 turns on board
    private fun catDealDamage(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.stayNum != 0 && petStatus[petOrder]!!.stayNum % catInfo.count == 0 ) {
            return catInfo.damage
        }
        return 0
    }

    //return the count number on UI
    private fun catCount(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.location == dict.onDECK){
            return 0
        }
        return (petStatus[petOrder]!!.stayNum % (catInfo.count) )
    }

    private val wormInfo = PetInfo(
        name = "Worm",
        imageId = R.drawable.game_worm,  // Replace with actual drawable resource ID
        element = dict.ELEMENT_WATER,
        type = dict.ATK_TYPE_RETURN,
        damage = 5,
        count = 1,
        description =""
    )

    //deal damage when it return to deck at after it stay on the board for exactly 1 turn
    private fun wormDealDamage(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.stayNum == wormInfo.count && petStatus[petOrder]!!.location == dict.onDECK ) {
            return wormInfo.damage
        }
        return 0
    }

    //return the count number on UI
    private fun wormCount(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.stayNum == 1 ){
            return wormInfo.count
        }
        return 0
    }

    private val tigerInfo = PetInfo(
        name = "Tiger",
        imageId = R.drawable.game_tiger,  // Replace with actual drawable resource ID
        element = dict.ELEMENT_FOREST,
        type = dict.ATK_TYPE_BOUNCE,
        damage = 20,
        count = 2,
        description =""
    )

    //need fixed
    private fun tigerDealDamage(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.bounceNum !=0 && petStatus[petOrder]!!.bounceNum %  tigerInfo.count ==0) {
            return tigerInfo.damage
        }
        return 0
    }

    //return the count number on UI
    private fun tigerCount(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.stayNum == 0 ){
            return 0
        }
        return (petStatus[petOrder]!!.stayNum % tigerInfo.count )+ 1
    }

    private val dragonInfo = PetInfo(
        name = "Dragon",
        imageId = R.drawable.game_dragon,  // Replace with actual drawable resource ID
        element = dict.ELEMENT_FIRE,
        type = dict.ATK_TYPE_STAY,
        damage = 25,
        count = 1,
        description =""
    )

    //
    private fun dragonDealDamage(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.location == dict.onBoard){
            return petStatus[petOrder]!!.stayNum * abs(10-petStatus[petOrder]!!.stayNum)
        }
        return 0
    }

    //return the count number on UI
    private fun dragonCount(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.location == dict.onBoard){
            return dragonInfo.count
        }
        return 0
    }

    private val mewtwoInfo = PetInfo(
        name = "Mewtwo",
        imageId = R.drawable.game_mewtwo,  // Replace with actual drawable resource ID
        element = dict.ELEMENT_WATER,
        type = dict.ATK_TYPE_STAY,
        damage = 100,
        count = 5,
        description =""
    )

    //
    private fun mewtwoDealDamage(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.stayNum == 5){
            return 100
        }
        return 0
    }

    //return the count number on UI
    private fun mewtwoCount(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.stayNum >5 ){
            return 0
        }
        return petStatus[petOrder]!!.stayNum
    }

    private val petInfoMap: Map<Int, PetInfo> = mapOf(
        CAT to catInfo,
        WORM to wormInfo,
        TIGER to tigerInfo,
        DRAGON to dragonInfo,
        MEWTWO to mewtwoInfo
    )

    fun getPetInfoById(id: Int): PetInfo? {
        return petInfoMap[id]  // Returns the corresponding AnimalInfo object or null if not found
    }

    private val petDamageMap: Map<Int, (Array<petStatus?>,Int) -> Int> = mapOf(
        CAT to ::catDealDamage,
        WORM to :: wormDealDamage,
        TIGER to :: tigerDealDamage,
        DRAGON to :: dragonDealDamage,
        MEWTWO to :: mewtwoDealDamage
    )

    fun getPetDamage(petStatus: Array<petStatus?>,petOrder:Int): Int {
        return petDamageMap[petStatus[petOrder]!!.unitId]!!.invoke(petStatus, petOrder)
    }

    private val petCountMap: Map<Int, (Array<petStatus?>, Int) -> Int> = mapOf(
        CAT to ::catCount,
        WORM to :: wormCount,
        TIGER to :: tigerCount,
        DRAGON to :: dragonCount,
        MEWTWO to :: mewtwoCount

    )

    fun getPetCount(petStatus: Array<petStatus?>,petOrder:Int): Int {
        return petCountMap[petStatus[petOrder]!!.unitId]!!.invoke(petStatus,petOrder)
    }

//    fun getViewInfoById(id: Int): petViewInfo? {
//        return petInfoMap[id]?.invoke() // Returns an instance of the specified pet class
//    }

    fun getAllPetImageIds(): List<Int> {
        return (0 until TOTAL).mapNotNull { getPetInfoById(it)?.imageId }
//
//        val ret = MutableList<Int>()
//        for(i in 0..TOTAL-1){
//            ret.add(getPetInfoById(i)!!.imageId)
//        }
    }

}