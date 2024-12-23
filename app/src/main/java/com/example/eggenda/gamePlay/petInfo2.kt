package com.example.eggenda.gamePlay

import com.example.eggenda.R
import kotlin.math.abs

class petInfo2 {

    private val TOTAL = 10

    private val GLUTINOUS_BUNNY= 0
    private val EVIL_WATER = 1
    private val FLAMING_SKULL = 2
    private val LIL_MOTHY = 3
    private val SHY_RACCOON = 4
    private val HEALING_SPRITE = 5
    private val BABY_OWLBEAR = 6
    private val AMBUSH_MOUSEVIPER = 7
    private val ANIMATED_NUTCRACKER = 8
    private val DEEPSEA_MERMAN = 9

    fun getPetInfoById(id: Int):Pet?{
        val petMap: Map<Int, () -> Pet> = mapOf(
            // TODO
            BABY_OWLBEAR to {babyOwlbear()},
            AMBUSH_MOUSEVIPER to {ambushMouseviper()},
            EVIL_WATER to {evilWater()},
            ANIMATED_NUTCRACKER to {animatedNutcracker()},
            DEEPSEA_MERMAN to {deepseaMerman()},
            FLAMING_SKULL to {flamingSkull()},
            GLUTINOUS_BUNNY to {glutinousBunny()},
            HEALING_SPRITE to {healingSprite()},
            LIL_MOTHY to {lilMothman()},
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
        val backgroundId: Int

        fun dealDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int):Int
        fun attackCountdown(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int):Int
        fun condition(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int):String
        fun nextDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int):String
        fun resetAfterDamage():Boolean
    }

    private class glutinousBunny :Pet {
        override val id: Int = 0
        override val name: String = "Glutinous Bunny"
        override val imageId: Int = R.drawable.pet_c_glutinousbunny
        override val element: Int = dict.ELEMENT_FOREST
        override val attackType: Int = dict.ATK_TYPE_STAY
        override val damage: Int = -30
        override val count: Int = 2
        override val skillName :String ="Sleepy..."
        override val description: String = "It heals the player " + (damage * -1)+ " hp only if it remains on the board for " + count.toString()+ " turn(s).\n\n" + "" +
                "If it’s knocked out, no healing occurs"
        override val rarity: Int = dict.RARITY_NORMAL
        override val backgroundId: Int = R.drawable.background_bunny_happyplains

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
                return "Ability after " +count.toString()+  " turns (not on board yet)"
//                return count.toString()+" more turn(s) to stay on the board to heal" + (damage * -1)+ "hp"
//                return "It heals the player " + (damage * -1)+ " hp only if it remains on the board for " + count.toString()+ " turn(s).\n\n" +
//                        "If it’s knocked out, no healing occurs."
            }
            else{
                return "Ability after " +attackCountdown(petStatus, petOrder,deckSize).toString()+  " turns"
//                return attackCountdown(petStatus, petOrder,deckSize).toString()+" more turn(s) to stay on the board"
//                return "This pet heals the player only if it remains on the board for [X] more turn(s). If it’s knocked out, no healing occurs"
//                return "It heals the player " + (damage * -1)+ " hp only if it remains on the board for " + attackCountdown(petStatus, petOrder,deckSize).toString()+ " more turn(s).\n\n" +
//                        "If it’s knocked out, no healing occurs."
//                return name + " has " + attackCountdown(petStatus, petOrder,deckSize).toString()+" more turn(s) to heal the player" + (damage * -1)+ "hp"
            }

        }

        override fun nextDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int): String {
            return "Heals "+(damage * -1).toString()+" hp"
        }

        override fun resetAfterDamage(): Boolean {
            return true
        }

    }

    private class evilWater() :Pet {
        override val id: Int = 1
        override val name: String = "Evil Water"
        override val imageId: Int = R.drawable.pet_b_evilwater
        override val element: Int = dict.ELEMENT_WATER
        override val attackType: Int = dict.ATK_TYPE_RETURN
        override val damage: Int = 20
        override val count: Int = 1
        override val skillName :String ="Fragile!"
        override val description: String = "If Evil Water is knocked out of the board within 1 turn it was placed, " +
                "deal " + damage.toString() + " Water Elemental damage to the Boss.\n\n" +
                "If it remains on the board after 1 turn, deal 0 damage to Boss."

//            "Deal "+damage.toString()+" "+dict.ELEMENT_STRING[element]+" damages " +
//                "when it is knocked out from the board" +
//                "on the next turn after it is placed on the board."
        override val rarity: Int = dict.RARITY_NORMAL
        override val backgroundId: Int = R.drawable.background_evilwater_lab

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
                return "Ability after " +count.toString()+  " turns (not on board yet)"
//                return "If it's knocked out of the board within 1 turn it was placed, deal " + damage.toString() + " Water Elemental damage to the Boss.\n\n"
//                        "If it remains on the board next turn, deal 0 damage to Boss."
            }
            else{
                val count = attackCountdown(petStatus, petOrder,deckSize)
                var countStr = "∞"
                if(count >= 0){
                    countStr = count.toString()
//                    return "Abilities after " + count.toString()+  " turns (not on board yet)"
//                    return "If Evil Water is knocked out of the board next turn, deal " + damage.toString() + " Water Elemental damage to the Boss.\n\n" +
//                            "If it remains on the board at next turn, deal 0 damage to Boss."

                }
                return "Ability after " + countStr+  " turns"
//                return "Evil Water is not knocked out of the board, no damages will deal until it's knocked out from the board."

//                return "Knocked out from the board after\n" +
//                        countStr+" more turn it stays on the board "
            }
        }

        override fun nextDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int): String {
            val count = attackCountdown(petStatus, petOrder,deckSize)
            if(count >= 0){
                return "Being knocked out to make " + damage.toString()+" " +dict.ELEMENT_STRING[element]+" damages"
            }
            return "Being knocked out to make 0 " +dict.ELEMENT_STRING[element]+" damages"
        }

        override fun resetAfterDamage(): Boolean {
            return true
        }
    }

    private class flamingSkull :Pet {
        override val id: Int = 2
        override val name: String = "Flaming Skull"
        override val imageId: Int = R.drawable.pet_c_flamingskull
        override val element: Int =  dict.ELEMENT_FIRE
        override val attackType: Int = dict.ATK_TYPE_BOUNCE
        override val damage: Int = 40
        override val count: Int = 2
        override val skillName:String = "You can't catch me!"
//        "When Infernal Skull's position changes (by allies or enemies) for 2 times, it deals "+damage+" "+dict.ELEMENT_STRING[element]+ Fire Elemental damage to the enemy. Infernal Counter resets to 0. "
        override val description: String = "When Flaming Skull's position changes (by allies or enemies), gain a Flam Counter.\n\n " +
        "Once it has " + count + " Flam Counters, it consumes them & deal " +
        damage + " "+dict.ELEMENT_STRING[element] + " damages to the enemy.\n Flaming Counter resets to 0."

        //            "Deal "+damage+" "+dict.ELEMENT_STRING[element]+" damage\n" +
//                "after every "+count+ " position change when it is on the board."
        override val rarity: Int = dict.RARITY_RARE
        override val backgroundId: Int = R.drawable.background_flameskull_darkdungeons

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
                return "Ability after " + count.toString()+  " turns(not on board yet)"
//                return count.toString()+" more bounce when it stay on the board\n"
            }
            else{
                val count = attackCountdown(petStatus, petOrder,deckSize)
//                return count.toString()+" more bounce when it stay on the board\n"
                return "Ability after " + count.toString()+  " turns"
            }
        }

        override fun nextDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
//            return damage.toString()+" "+dict.ELEMENT_STRING[element]+" damages"
            val numcount = attackCountdown(petStatus, petOrder,deckSize)
            return numcount.toString() + " more Counter(s) needed to deal " + damage.toString()+" "+dict.ELEMENT_STRING[element]+" damages"
        }

        override fun resetAfterDamage(): Boolean {
            return true
        }
    }

    private class lilMothman :Pet {
        override val id: Int = 3
        override val name: String = "Lil' Mothman"
        override val imageId: Int = R.drawable.pet_c_lilmothy
        override val element: Int =  dict.ELEMENT_FIRE
        override val attackType: Int = dict.ATK_TYPE_STAY
        override val damage: Int = 20
        override val count: Int = 1
        override val skillName: String = "Math is important! "
//        override val description: String = "dealing 9, 16, 21, 24, 25, 24, 21, 16, 9\n"+dict.ELEMENT_STRING[element]+" damages sequentially \n" +
//                "after it placed on the board"
        override val description: String ="Let x = number of turns it stays on the board,\n" +
        "it deals (x mod 10) * (10 - (x mod 10) ) "+ dict.ELEMENT_STRING[element]+" damages"
        override val rarity: Int = dict.RARITY_LEGENDARY
        override val backgroundId: Int = R.drawable.background_mothman_deadlands

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            if(petStatus[petOrder]!!.location == dict.onBoard){
                val stayNum = petStatus[petOrder]!!.stayNum
                return (stayNum % 10) * (10 - stayNum % 10)
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            if(petStatus[petOrder]!!.location == dict.onBoard && petStatus[petOrder]!!.stayNum >0 ){
                return 0
            }
            return 1
        }

        override fun condition(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
            if(petStatus[petOrder]!!.location == dict.onDECK){
//                return count.toString()+" more turns to stay on the board\n"
                return "Ability after " + count.toString()+  " turns(not on board yet)"
            }
            else{
                val count = attackCountdown(petStatus, petOrder,deckSize)
//                return count.toString()+" more turns to stay on the board\n"
                return "Ability after " + count.toString()+  " turns"
            }
        }

        override fun nextDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
            if(petStatus[petOrder]!!.location == dict.onDECK){
                return " Stay on board to deal 9 Fire Damages"
            }
            else{
                return "Stays on board to deal "+dealDamage(petStatus, petOrder,deckSize).toString()+" "+dict.ELEMENT_STRING[element]+" damages"
            }
        }

        override fun resetAfterDamage(): Boolean {
            return false
        }
    }
    private class shyRaccoon :Pet {
        override val id: Int = 4
        override val name: String = "Shy Raccoon"
        override val imageId: Int = R.drawable.pet_c_shyraccoon
        override val element: Int =  dict.ELEMENT_WATER
        override val attackType: Int = dict.ATK_TYPE_STAY
        override val damage: Int = 30
        override val count: Int = 1
        override val skillName: String = "Hello...(shy)"
        override val description: String = "When it stays on board for " + count +" turn, " +
        "dealing (number of pets on board * " + damage+" "+ dict.ELEMENT_STRING[element] + ") damages.\n\n"+
        "No further damage will be made after attacking, unless it's' knocked out from the board."
//
//            "Deal ("+damage+"x the number of pet on board) "+dict.ELEMENT_STRING[element]+" damages\n" +
//                "on the "+count+ "th turn\n" +
//                "after it is placed on the board"
        override val rarity: Int = dict.RARITY_LEGENDARY
        override val backgroundId: Int = R.drawable.background_raccoon_housetrash

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
//                return count.toString()+" more turns to stay on the board\n"
                return "Ability after " + count.toString()+" turns(not on board yet)"
            }
            else{
                val count = attackCountdown(petStatus, petOrder,deckSize)
                var countStr = "∞"
                if(count >= 0){
                    countStr = count.toString()
                }
//                return  countStr+" more turns to stay on the board\n"
                return "Ability after " + countStr+  " turns"
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
                return "Stay on board to make " + mulDmg.toString()+" " +dict.ELEMENT_STRING[element]+" damages"
            }
            return "Stay on board to make 0 " +dict.ELEMENT_STRING[element]+" damages"
        }

        override fun resetAfterDamage(): Boolean {
            return false
        }

    }

    private class healingSprite :Pet {
        // TODO: make pet unique
        override val id: Int = 5
        override val name: String = "Healing Sprite"
        override val imageId: Int = R.drawable.pet_c_healingsprite
        override val element: Int = dict.ELEMENT_WATER
        override val attackType: Int = dict.ATK_TYPE_STAY
        override val damage: Int = -20
        override val count: Int = 4
        override val skillName :String ="Light of the hospital"
        override val description: String = "Heals "+(damage * -1)+" hp if there are "+count+" pets on the board"
        override val rarity: Int = dict.RARITY_NORMAL
        override val backgroundId: Int = R.drawable.background_sprite_spiritrealm

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            var temp = 0
            for(i in 0..<deckSize){
                if (petStatus[i]?.location == dict.onBoard){
                    temp ++
                }
            }
            if(temp == count) {

                return damage
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
//        return (petStatus[petOrder]!!.stayNum % (catInfo.count) )
            var temp = 0
            for(i in 0..<deckSize){
                if (petStatus[i]?.location == dict.onBoard){
                    temp ++
                }
            }
            return abs(temp - count)
        }

        override fun condition(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
            var temp = 0
            for(i in 0..<deckSize){
                if (petStatus[i]?.location == dict.onBoard){
                    temp ++
                }
            }
            if(temp > count){
                return "Ability with " + (temp - count).toString()+" pets to knock out from the board."
            }
            else{
                return "Ability with " +(count - temp).toString()+" pets to put on the board."
            }

        }

        override fun nextDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int): String {
            return "Heals "+(damage * -1).toString()+" hp"
        }

        override fun resetAfterDamage(): Boolean {
            return false
        }

    }

    private class babyOwlbear :Pet {
        // TODO: make this pet unique
        override val id: Int = 6
        override val name: String = "Baby Owlbear"
        override val imageId: Int = R.drawable.pet_a_babyowlbear
        override val element: Int = dict.ELEMENT_FOREST
        override val attackType: Int = dict.ATK_TYPE_RETURN
        override val damage: Int = 15
        override val count: Int = 0
        override val skillName :String ="Good place to eat..."
        override val description: String = "Deals number of turns it stay on the board * "+damage+dict.ELEMENT_STRING[element]+" damage\n" +
                "when  knocked out from the board."
        override val rarity: Int = dict.RARITY_NORMAL
        override val backgroundId: Int = R.drawable.background_owlbear_temperateforest

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            if(petStatus[petOrder]!!.stayNum > 0 && petStatus[petOrder]!!.location == dict.onDECK ) {
                return damage * petStatus[petOrder]!!.stayNum
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            if(petStatus[petOrder]!!.location == dict.onDECK){
                return 1
            }

            return 0
        }

        override fun condition(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {

//            return count.toString()+" more turn(s) to stay on the board "
            return "Ability on Turn " + count.toString()+" to make damage."

        }

        override fun nextDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int): String {
            return "Deals "+(damage * petStatus[petOrder]!!.stayNum ).toString()+dict.ELEMENT_STRING[element]+" damage"
        }

        override fun resetAfterDamage(): Boolean {
            return true
        }
    }

    private class ambushMouseviper() :Pet {
        // TODO: make this pet unique
        override val id: Int = 7
        override val name: String = "Ambush Mouseviper"
        override val imageId: Int = R.drawable.pet_b_ambushmouseviper
        override val element: Int = dict.ELEMENT_FOREST
        override val attackType: Int = dict.ATK_TYPE_RETURN
        override val damage: Int = 30
        override val count: Int = 2
        override val skillName :String ="Toxic"
        override val description: String = "Deal "+damage.toString()+" "+dict.ELEMENT_STRING[element]+" damages\n" +
                "after every "+count+" turns"
        override val rarity: Int = dict.RARITY_NORMAL
        override val backgroundId: Int = R.drawable.background_mouseviper_noidea

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            if(petStatus[petOrder]!!.stayNum != 0 && petStatus[petOrder]!!.stayNum % count == 0 && petStatus[petOrder]!!.location == dict.onBoard) {
                return damage
            }
            return 0
        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
//            val stayNum = petStatus[petOrder]!!.stayNum
//            if( stayNum <= 1 ){
//                return count - stayNum
//            }
//            return -1

            if(petStatus[petOrder]!!.location == dict.onDECK){
                return 0
            }
//        return (petStatus[petOrder]!!.stayNum % (catInfo.count) )
            return count - petStatus[petOrder]!!.stayNum % (count+1)
        }

        override fun condition(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
//            if(petStatus[petOrder]!!.location == dict.onDECK){
//                return "Knocked out from the board after\n" +
//                        "1 more turn it stays on the board "
//            }
//            else{
//                val count = attackCountdown(petStatus, petOrder,deckSize)
//                var countStr = "∞"
//                if(count >= 0){
//                    countStr = count.toString()
//                }
//                return "Knocked out from the board after\n" +
//                        countStr+" more turn it stays on the board "
//            }

            if(petStatus[petOrder]!!.location == dict.onDECK){
//                return count.toString()+" more turn(s) to stay on the board "
                return "Ability after " + count.toString()+" turns(not on board yet)"
            }
            else{
                return "Ability after " + attackCountdown(petStatus, petOrder,deckSize)+" turns"
//                return attackCountdown(petStatus, petOrder,deckSize).toString()+" more turn(s) to stay on the board"
            }
        }

        override fun nextDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int): String {
//            val count = attackCountdown(petStatus, petOrder,deckSize)
//            if(count >= 0){
//                return damage.toString()+" " +dict.ELEMENT_STRING[element]+" damages"
//            }
//            return "0 " +dict.ELEMENT_STRING[element]+" damages"

            return "Stay on board to make "+(damage ).toString()+dict.ELEMENT_STRING[element]+" damages\n"
        }

        override fun resetAfterDamage(): Boolean {
            return true
        }
    }

    private class animatedNutcracker() :Pet {
        // TODO: make this pet unique
        override val id: Int = 8
        override val name: String = "Animated Nutcracker"
        override val imageId: Int = R.drawable.pet_c_animatednutcracker
        override val element: Int = dict.ELEMENT_FIRE
        override val attackType: Int = dict.ATK_TYPE_STAY
        override val damage: Int = 100
        override val count: Int = 0
        override val skillName :String ="Nut Cracker!"
        override val description: String = "Deal "+damage.toString()+" "+dict.ELEMENT_STRING[element]+" damages\n" +
                "If there is no pets on the deck when this pet is placed on the board"
        override val rarity: Int = dict.RARITY_NORMAL
        override val backgroundId: Int  = R.drawable.background_nutcracker_homelyhome

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            var petOnBoard = 0
            for(i in 0..<deckSize){
                if(petStatus[i]!!.location == dict.onBoard){
                    petOnBoard++
                }
            }
            if(petStatus[petOrder]!!.stayNum == 0 && petOnBoard == deckSize && petStatus[petOrder]!!.location == dict.onBoard){
                return damage
            }
            return 0

        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            val stayNum = petStatus[petOrder]!!.stayNum
            if( petStatus[petOrder]!!.location == dict.onBoard && stayNum == 0){
                return 0
            }
            else if(petStatus[petOrder]!!.location == dict.onDECK){
                return 1
            }
            return -1
        }

        override fun condition(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
            if(petStatus[petOrder]!!.location == dict.onDECK){
                return "Ability for 0 more turn to stay on the board\n"
            }
            else{
                val count = attackCountdown(petStatus, petOrder,deckSize)
                var countStr = "∞"
                if(count >= 0){
                    countStr = count.toString()
                }
                return  "Ability for " + countStr+" turn."

            }
        }

        override fun nextDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int): String {
//            var petOnBoard = 0
//            for(i in 0..<deckSize){
//                if(petStatus[i]!!.location == dict.onBoard){
//                    petOnBoard++
//                }
//            }
            if(attackCountdown(petStatus, petOrder,deckSize) != -1 ){
//                val mulDmg = petOnBoard * damage
                return damage.toString()+" " +dict.ELEMENT_STRING[element]+" damages"
            }
            return "0 " +dict.ELEMENT_STRING[element]+" damages"
        }

        override fun resetAfterDamage(): Boolean {
            return false
        }
    }

    private class deepseaMerman() :Pet {
        // TODO: make this pet unique
        override val id: Int = 9
        override val name: String = "Deap-sea Merman"
        override val imageId: Int = R.drawable.pet_c_deepseamerman
        override val element: Int = dict.ELEMENT_WATER
        override val attackType: Int = dict.ATK_TYPE_RETURN
        override val damage: Int = 15
        override val count: Int = 1
        override val skillName :String ="Golden Fork"
        override val description: String = "Deal "+damage.toString()+"x water pets on the board "+dict.ELEMENT_STRING[element]+" damages\n" +
                "after it is placed on the board.\n\n" +
                "No further damage will be made after attacking, unless it's' knocked out from the board."
        override val rarity: Int = dict.RARITY_NORMAL
        override val backgroundId: Int = R.drawable.background_merman_deepseacaverns

        override fun dealDamage(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {

            var petOnBoard = 0
            for(i in 0..<deckSize){
                if(petStatus[i]!!.location == dict.onBoard && petStatus[i]!!.element == dict.ELEMENT_WATER){
                    petOnBoard++
                }
            }
            if(attackCountdown(petStatus, petOrder,deckSize) == 0 && petStatus[petOrder]!!.location == dict.onBoard){
                return damage * petOnBoard
            }
            return 0

        }

        override fun attackCountdown(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): Int {
            val stayNum = petStatus[petOrder]!!.stayNum
            if( stayNum == 0 ){
                return stayNum
            }
            return -1
        }

        override fun condition(petStatus: Array<petStatus?>, petOrder: Int,deckSize:Int): String {
            if(petStatus[petOrder]!!.location == dict.onDECK){
                return "0 more turn to stay on the board\n"
            }
            else{
                val count = attackCountdown(petStatus, petOrder,deckSize)
                var countStr = "∞"
                if(count >= 0){
                    countStr = count.toString()
                }
                return  countStr+" more turn to stays on the board "

            }
        }

        override fun nextDamage(petStatus: Array<petStatus?>,petOrder:Int,deckSize:Int): String {
            var petOnBoard = 0
            for(i in 0..<deckSize){
                if(petStatus[i]!!.location == dict.onBoard && petStatus[i]!!.element == dict.ELEMENT_WATER){
                    petOnBoard++
                }
            }
            if(attackCountdown(petStatus, petOrder,deckSize) != -1){
                val mulDmg = petOnBoard * damage
                return mulDmg.toString()+" " +dict.ELEMENT_STRING[element]+" damages"
            }
            return "0 " +dict.ELEMENT_STRING[element]+" damages"
        }

        override fun resetAfterDamage(): Boolean {
            return false
        }
    }






}