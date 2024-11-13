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

    fun getTotalPetAmount():Int{return TOTAL}

    interface Pet{
        val id: Int
        val name: String
        val imageId: Int
        val element: Int
        val attackType: Int
        val damage: Int
        val count: Int
        val skillName: String
        val description: String
        val rarity: Int

        fun dealDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int):Int
        fun attackCountdown(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int):Int
        fun condition(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int):String
        fun nextDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int):String
    }

    private class chubbyBunny :Pet {
        override val id: Int = 0
        override val name: String = "Chubby Bunny"
        override val imageId: Int = R.drawable.pet_chubby_bunny_large
        override val element: Int = dict.ELEMENT_FOREST
        override val attackType: Int = dict.ATK_TYPE_STAY
        override val damage: Int = 30
        override val count: Int = 2
        override val skillName :String ="Sleepy..."
        override val description: String = "Deal"+damage+" "+dict.ELEMENT_STRING[element]+" damage\n" +
                "on every "+count +" turns\n" +
                "it stay on the board."
        override val rarity: Int = dict.RARITY_NORMAL

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            if(petStatus[petOrder]!!.stayNum != 0 && petStatus[petOrder]!!.stayNum % count == 0 && petStatus[petOrder]!!.location == dict.onBoard) {

                return damage
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            if(petStatus[petOrder]!!.location == dict.onDECK){
                return 0
            }
//        return (petStatus[petOrder]!!.stayNum % (catInfo.count) )
            return count - petStatus[petOrder]!!.stayNum % (count+1)
        }

        override fun condition(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
           if(petStatus[petOrder]!!.location == dict.onDECK){
               return count.toString()+" more turn(s) to stay on the board "
           }
           else{
                return attackCountdown(petStatus, petOrder,deckSize).toString()+" more turn(s) to stay on the board"
           }

        }

        override fun nextDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int): String {
            return damage.toString()+" "+dict.ELEMENT_STRING[element]+" damages"
        }

    }

    private class evilWater() :Pet {
        override val id: Int = 1
        override val name: String = "Evil Water"
        override val imageId: Int = R.drawable.pet_evil_water_large
        override val element: Int = dict.ELEMENT_WATER
        override val attackType: Int = dict.ATK_TYPE_RETURN
        override val damage: Int = 20
        override val count: Int = 1
        override val skillName :String ="Fragile!"
        override val description: String = "Deal "+damage.toString()+" "+dict.ELEMENT_STRING[element]+" damages\n" +
                "when it is knocked out from the board\n" +
                "on the 1st turn after it is placed on the board."
        override val rarity: Int = dict.RARITY_NORMAL

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            if(petStatus[petOrder]!!.stayNum == count && petStatus[petOrder]!!.location == dict.onDECK ) {
                return damage
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            val stayNum = petStatus[petOrder]!!.stayNum
            if( stayNum <= 1 ){
                return count - stayNum
            }
            return -1
        }

        override fun condition(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
            if(petStatus[petOrder]!!.location == dict.onDECK){
                return "Knocked out from the board after\n" +
                        "1 more turn it stays on the board "
            }
            else{
                val count = attackCountdown(petStatus, petOrder,deckSize)
                var countStr = "∞"
                if(count >= 0){
                    countStr = count.toString()
                }
                return "Knocked out from the board after\n" +
                        countStr+" more turn it stays on the board "
            }
        }

        override fun nextDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int): String {
            val count = attackCountdown(petStatus, petOrder,deckSize)
            if(count >= 0){
                return damage.toString()+" " +dict.ELEMENT_STRING[element]+" damages"
            }
            return "0 " +dict.ELEMENT_STRING[element]+" damages"
        }
    }

    private class flamingSkull :Pet {
        override val id: Int = 2
        override val name: String = "Flaming Skull"
        override val imageId: Int = R.drawable.pet_flaming_skull_large
        override val element: Int =  dict.ELEMENT_FIRE
        override val attackType: Int = dict.ATK_TYPE_BOUNCE
        override val damage: Int = 40
        override val count: Int = 2
        override val skillName:String = "You can't catch me!"
        override val description: String = "Deal "+damage+" "+dict.ELEMENT_STRING[element]+" damage\n" +
                "after every "+count+ " bounces\n" +
                "when it stay on the board."
        override val rarity: Int = dict.RARITY_RARE

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            val skullStatus = petStatus[petOrder]!!
            if(skullStatus.bounceNum !=0 && skullStatus.bounceNum % count == 0 && skullStatus.location == dict.onBoard) {
                return damage
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            return count - petStatus[petOrder]!!.bounceNum
        }

        override fun condition(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
            if(petStatus[petOrder]!!.location == dict.onDECK){
                return count.toString()+" more bounce when it stay on the board\n"
            }
            else{
                val count = attackCountdown(petStatus, petOrder,deckSize)
                return count.toString()+" more bounce when it stay on the board\n"
            }
        }

        override fun nextDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
            return damage.toString()+" "+dict.ELEMENT_STRING[element]+" damages"
        }
    }

    private class littleMothman :Pet {
        override val id: Int = 3
        override val name: String = "Little Mothman"
        override val imageId: Int = R.drawable.pet_little_mothman_large
        override val element: Int =  dict.ELEMENT_FIRE
        override val attackType: Int = dict.ATK_TYPE_STAY
        override val damage: Int = 20
        override val count: Int = 1
        override val skillName: String = "Math is important! "
//        override val description: String = "dealing 9, 16, 21, 24, 25, 24, 21, 16, 9\n"+dict.ELEMENT_STRING[element]+" damages sequentially \n" +
//                "after it placed on the board"
        override val description: String ="Let x = number of turns it stays on the board,\n" +
        "deals (x mod 10) * (10 - (x mod 10) ) "+ dict.ELEMENT_STRING[element]+" damages"
        override val rarity: Int = dict.RARITY_LEGENDARY

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            if(petStatus[petOrder]!!.location == dict.onBoard){
                val stayNum = petStatus[petOrder]!!.stayNum
                return (stayNum % 10) * (10 - stayNum % 10)
//                return 0
            }
            return 0
            //he
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            if(petStatus[petOrder]!!.location == dict.onBoard && petStatus[petOrder]!!.stayNum >0 ){
                return 0
            }
            return 1
        }

        override fun condition(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
            if(petStatus[petOrder]!!.location == dict.onDECK){
                return count.toString()+" more turns to stay on the board\n"
            }
            else{
                val count = attackCountdown(petStatus, petOrder,deckSize)
                return count.toString()+" more turns to stay on the board\n"
            }
        }

        override fun nextDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
            if(petStatus[petOrder]!!.location == dict.onDECK){
                return " 9 fire damages"
            }
            else{
                return dealDamage(petStatus, petOrder,deckSize).toString()+" "+dict.ELEMENT_STRING[element]+" damages"
            }
        }
    }

    private class shyRaccoon :Pet {
        override val id: Int = 4
        override val name: String = "Shy Raccoon"
        override val imageId: Int = R.drawable.pet_shy_raccoon_large
        override val element: Int =  dict.ELEMENT_WATER
        override val attackType: Int = dict.ATK_TYPE_STAY
        override val damage: Int = 30
        override val count: Int = 1
        override val skillName: String = "Hello..."
        override val description: String = "Deal ("+damage+"x the number of pet on board) "+dict.ELEMENT_STRING[element]+" damages\n" +
                "on the "+count+ "th turn\n" +
                "after it is placed on the board"
        override val rarity: Int = dict.RARITY_LEGENDARY

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            var petOnBoard = 0
            for(i in 0..<deckSize){
                if(petStatus[i]!!.location == dict.onBoard){
                    petOnBoard++
                }
            }
            if(attackCountdown(petStatus, petOrder,deckSize) == 0){
                return damage * petOnBoard
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            if(petStatus[petOrder]!!.stayNum < 2  && petStatus[petOrder]!!.location == dict.onBoard){
                return count - petStatus[petOrder]!!.stayNum
            }
            else if (petStatus[petOrder]!!.location == dict.onDECK){
                return count
            }
            return -1
        }

        override fun condition(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
            if(petStatus[petOrder]!!.location == dict.onDECK){
                return count.toString()+" more turns to stay on the board\n"
            }
            else{
                val count = attackCountdown(petStatus, petOrder,deckSize)
                var countStr = "∞"
                if(count >= 0){
                    countStr = count.toString()
                }
                return  countStr+" more turns to stay on the board\n"
            }
        }

        override fun nextDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
            val count = attackCountdown(petStatus, petOrder,deckSize)
            var petOnBoard = 0
            for(i in 0..<deckSize){
                if(petStatus[i]!!.location == dict.onBoard){
                    petOnBoard++
                }
            }
            if(attackCountdown(petStatus, petOrder,deckSize) != -1){
                val mulDmg = petOnBoard * damage
                return mulDmg.toString()+" " +dict.ELEMENT_STRING[element]+" damages"
            }
            return "0 " +dict.ELEMENT_STRING[element]+" damages"
        }

    }






}