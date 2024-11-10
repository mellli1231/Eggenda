package com.example.eggenda.gamePlay


import com.example.eggenda.R
import kotlin.math.abs

class petInfo {

    val TOTAL = 6
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
    private val MEWONE = 5


    private val allId = intArrayOf(0,1,2,3,4,5)

    data class PetInfo(
        val name: String,
        val imageId: Int,  // Assuming this is a drawable resource ID
        val element: Int,
        val type: Int,
        val damage: Int,
        val count: Int,
        val description: String,
//        val rarity:Int
    )

    private val catInfo = PetInfo(
        name = "Cat",
        imageId = R.drawable.pet_chubby_bunny_large,  // Replace with actual drawable resource ID
        element = dict.ELEMENT_FIRE,
        type = dict.ATK_TYPE_STAY,
        damage = 30,
        count = 3,
        description =""
//        rarity = dict.RARITY_LEGENDARY
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
//        return (petStatus[petOrder]!!.stayNum % (catInfo.count) )
        return catInfo.count - petStatus[petOrder]!!.stayNum % (catInfo.count+1)
    }

    private val wormInfo = PetInfo(
        name = "Worm",
        imageId = R.drawable.pet_evil_water_large,  // Replace with actual drawable resource ID
        element = dict.ELEMENT_WATER,
        type = dict.ATK_TYPE_RETURN,
        damage = 20,
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
        val stayNum = petStatus[petOrder]!!.stayNum
        if( stayNum <= 1 ){
            return wormInfo.count - stayNum
        }

        return -1
    }

    private val tigerInfo = PetInfo(
        name = "Tiger",
        imageId = R.drawable.pet_flaming_skull_large,  // Replace with actual drawable resource ID
        element = dict.ELEMENT_FOREST,
        type = dict.ATK_TYPE_BOUNCE,
        damage = 20,
        count = 2,
        description =""
    )

    //need fixed
    private fun tigerDealDamage(petStatus: Array<petStatus?>,petOrder:Int):Int{
        val tigerStatus = petStatus[petOrder]!!
        if(tigerStatus.bounceNum !=0 && tigerStatus.bounceNum % tigerInfo.count == 0) {
            return tigerInfo.damage
        }
        return 0
    }

    //return the count number on UI
    private fun tigerCount(petStatus: Array<petStatus?>,petOrder:Int):Int{
//        if(petStatus[petOrder]!!.stayNum == 0 ){
//            return 0
//        }
//        return (petStatus[petOrder]!!.stayNum % tigerInfo.count )+ 1
//
        return tigerInfo.count - petStatus[petOrder]!!.bounceNum
//        return tigerInfo.count-petStatus[petOrder]!!.bounceNum % (tigerInfo.count+1)
//        return petStatus[petOrder]!!.bounceNum
    }


    private val dragonInfo = PetInfo(
        name = "Dragon",
        imageId = R.drawable.pet_little_mothman_large,  // Replace with actual drawable resource ID
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
//        if(petStatus[petOrder]!!.location == dict.onBoard){
//            return dragonInfo.count
//        }
//        return 0

        if(petStatus[petOrder]!!.location == dict.onBoard && petStatus[petOrder]!!.stayNum >0 ){
            return 0
        }
        return 1
    }

    private val mewtwoInfo = PetInfo(
        name = "Mewtwo",
        imageId = R.drawable.pet_shy_raccoon_large,  // Replace with actual drawable resource ID
        element = dict.ELEMENT_WATER,
        type = dict.ATK_TYPE_STAY,
        damage = 100,
        count = 2,
        description =""
    )

    //
    private fun mewtwoDealDamage(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.stayNum == mewtwoInfo.count){
            return mewtwoInfo.damage
        }
        return 0
    }

    //return the count number on UI
    private fun mewtwoCount(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.stayNum >mewtwoInfo.count ){
            return -1
        }
        return mewtwoInfo.count - petStatus[petOrder]!!.stayNum
    }

    private val mewOneInfo = PetInfo(
        name = "Mewone",
        imageId = R.drawable.pet_shy_raccoon_large,  // Replace with actual drawable resource ID
        element = dict.ELEMENT_WATER,
        type = dict.ATK_TYPE_STAY,
        damage = 50,
        count = 2,
        description =""
    )

    //
    private fun mewoneDealDamage(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.stayNum == mewtwoInfo.count){
            return mewOneInfo.damage
        }
        return 0
    }

    //return the count number on UI
    private fun mewoneCount(petStatus: Array<petStatus?>,petOrder:Int):Int{
        if(petStatus[petOrder]!!.stayNum >mewtwoInfo.count ){
            return -1
        }
        return mewtwoInfo.count - petStatus[petOrder]!!.stayNum
    }

    private val petInfoMap: Map<Int, PetInfo> = mapOf(
        CAT to catInfo,
        WORM to wormInfo,
        TIGER to tigerInfo,
        DRAGON to dragonInfo,
        MEWTWO to mewtwoInfo,
        MEWONE to mewOneInfo
    )

    fun getPetInfoById(id: Int): PetInfo? {
        return petInfoMap[id]  // Returns the corresponding AnimalInfo object or null if not found
    }

    private val petDamageMap: Map<Int, (Array<petStatus?>,Int) -> Int> = mapOf(
        CAT to ::catDealDamage,
        WORM to :: wormDealDamage,
        TIGER to :: tigerDealDamage,
        DRAGON to :: dragonDealDamage,
        MEWTWO to :: mewtwoDealDamage,
        MEWONE to :: mewoneDealDamage
    )

    fun getPetDamage(petStatus: Array<petStatus?>,petOrder:Int): Int {
        return petDamageMap[petStatus[petOrder]!!.unitId]!!.invoke(petStatus, petOrder)
    }

    private val petCountMap: Map<Int, (Array<petStatus?>, Int) -> Int> = mapOf(
        CAT to ::catCount,
        WORM to :: wormCount,
        TIGER to :: tigerCount,
        DRAGON to :: dragonCount,
        MEWTWO to :: mewtwoCount,
        MEWONE to :: mewoneCount

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