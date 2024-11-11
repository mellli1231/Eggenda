package com.example.eggenda.gamePlay

import com.example.eggenda.R
import kotlin.math.abs

class petInfo2 {

    private val TOTAL = 5

    private val CHUBBY_BUNNY = 0
    private val EVIL_WATER = 1
    private val FLAMING_SKULL = 2
    private val LITTLE_MOTHMAN = 3
    private val SHY_RACCOON = 4

    fun getPetInfoById(id: Int):Pet?{
        val petMap: Map<Int, () -> Pet> = mapOf(

            CHUBBY_BUNNY to{chubbyBunny()},
            EVIL_WATER to {evilWater()},
            FLAMING_SKULL to {flamingSkull()},
            LITTLE_MOTHMAN to {littleMothman()},
            SHY_RACCOON to {shyRaccoon()}

        )
        return petMap[id]?.invoke()
    }

    fun getTotalPetAmount() : Int{return TOTAL}

    interface Pet{
        val id: Int
        val name: String
        val imageId: Int
        val element: Int
        val attackType: Int
        val damage: Int
        val count: Int
        val description: String
        val rarity: Int

        fun dealDamage(petStatus: Array<petStatus?>,petOrder:Int):Int
        fun attackCountdown(petStatus: Array<petStatus?>,petOrder:Int):Int
    }

    private class chubbyBunny :Pet {
        override val id: Int = 0
        override val name: String = "Chubby Bunny"
        override val imageId: Int = R.drawable.pet_chubby_bunny_large
        override val element: Int = dict.ELEMENT_FIRE
        override val attackType: Int = dict.ATK_TYPE_STAY
        override val damage: Int = 30
        override val count: Int = 3
        override val description: String = ""
        override val rarity: Int = dict.RARITY_NORMAL

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int): Int {
            if(petStatus[petOrder]!!.stayNum != 0 && petStatus[petOrder]!!.stayNum % count == 0 ) {
                return damage
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int): Int {
            if(petStatus[petOrder]!!.location == dict.onDECK){
                return 0
            }
//        return (petStatus[petOrder]!!.stayNum % (catInfo.count) )
            return count - petStatus[petOrder]!!.stayNum % (count+1)
        }
    }

    private class evilWater :Pet {
        override val id: Int = 1
        override val name: String = "Evil Water"
        override val imageId: Int = R.drawable.pet_evil_water_large
        override val element: Int = dict.ELEMENT_WATER
        override val attackType: Int = dict.ATK_TYPE_RETURN
        override val damage: Int = 20
        override val count: Int = 1
        override val description: String = ""
        override val rarity: Int = dict.RARITY_NORMAL

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int): Int {
            if(petStatus[petOrder]!!.stayNum == count && petStatus[petOrder]!!.location == dict.onDECK ) {
                return damage
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int): Int {
            val stayNum = petStatus[petOrder]!!.stayNum
            if( stayNum <= 1 ){
                return count - stayNum
            }
            return -1
        }
    }

    private class flamingSkull :Pet {
        override val id: Int = 2
        override val name: String = "Flaming Skull"
        override val imageId: Int = R.drawable.pet_flaming_skull_large
        override val element: Int =  dict.ELEMENT_FOREST
        override val attackType: Int = dict.ATK_TYPE_BOUNCE
        override val damage: Int = 40
        override val count: Int = 2
        override val description: String = ""
        override val rarity: Int = dict.RARITY_RARE

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int): Int {
            val skullStatus = petStatus[petOrder]!!
            if(skullStatus.bounceNum !=0 && skullStatus.bounceNum % count == 0 && skullStatus.location == dict.onBoard) {
                return damage
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int): Int {
            return count - petStatus[petOrder]!!.bounceNum
        }
    }

    private class littleMothman :Pet {
        override val id: Int = 3
        override val name: String = "Little Mothman"
        override val imageId: Int = R.drawable.pet_little_mothman_large
        override val element: Int =  dict.ELEMENT_FIRE
        override val attackType: Int = dict.ATK_TYPE_STAY
        override val damage: Int = 25
        override val count: Int = 1
        override val description: String = ""
        override val rarity: Int = dict.RARITY_LEGENDARY

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int): Int {
            if(petStatus[petOrder]!!.location == dict.onBoard){
                return petStatus[petOrder]!!.stayNum * abs(10-petStatus[petOrder]!!.stayNum)
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int): Int {
            if(petStatus[petOrder]!!.location == dict.onBoard && petStatus[petOrder]!!.stayNum >0 ){
                return 0
            }
            return 1
        }
    }

    private class shyRaccoon :Pet {
        override val id: Int = 4
        override val name: String = "Shy Raccoon"
        override val imageId: Int = R.drawable.pet_shy_raccoon_large
        override val element: Int =  dict.ELEMENT_WATER
        override val attackType: Int = dict.ATK_TYPE_STAY
        override val damage: Int = 100
        override val count: Int = 5
        override val description: String = ""
        override val rarity: Int = dict.RARITY_LEGENDARY

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int): Int {
            if(petStatus[petOrder]!!.stayNum == count){
                return damage
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int): Int {
            if(petStatus[petOrder]!!.stayNum >count ){
                return -1
            }
            return count - petStatus[petOrder]!!.stayNum
        }
    }






}