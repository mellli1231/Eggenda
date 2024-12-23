package com.example.eggenda.gamePlay
object dict {

    //For startPos, endPos, boardStatus and deckStatus
    const val outsideBoard = -4
    val hasRock = -3
    const val noPet = -2
    const val hasPet = -1


    //For location of pet
    const val onDECK = 0
    const val onBoard = 1

//    const val LOCATION_CHANGE = 3
//    val POS_CHANGE = 4

    const val ALLOW = 5
    const val NOT_ALLOW = 6

    //For pet
    const val ELEMENT_FIRE = 0
    const val ELEMENT_WATER = 1
    const val ELEMENT_FOREST = 2

    val ELEMENT_STRING = arrayOf("Fire", "Water", "Forest", "Fire, Water, Forest")

    const val RARITY_NORMAL = 0
    const val RARITY_RARE = 1
    const val RARITY_LEGENDARY = 2

    val RARETY_STRING = arrayOf("NORMAL", "RARE", "LEGENDARY")

    const val ATK_TYPE_STAY = 0
    const val ATK_TYPE_RETURN = 1
    const val ATK_TYPE_BOUNCE = 2
    val ATK_STRING = arrayOf("Stay", "Return", "Bounce")

    //For stage
    const val STAGE_OBJECTIVE_EXACT = 0     //exact value
    const val STAGE_OBJECTIVE_BEST = 1      //as long as you can kill them
    const val STAGE_OBJECTIVE_FIGHT = 2     //boss will fight you back

    val STAGE_STRING = arrayOf("Deal EXACT Damage", "No Damage Restriction", "No Damage Restriction + BOSS will HIT YOU ")

    const val STAGE_ACCEPT_ALL_ELEMENT = 3

    const val STAGE_ACTION_NO_ACTION = 0
    const val STAGE_ACTION_ATTACK = 1
    const val STAGE_ACTION_PUSH = 2

    const val STAGE_PUSH_NORTH = 0
    const val STAGE_PUSH_SOUTH = 1

    val PUSH_STRING = arrayOf("north","south")

    //For run stage
    const val GAME_NOT_START = -2
    const val GAME_START = -1
    const val GAME_NO_PET = 0
    const val GAME_WON = 1
    const val GAME_TURN_EXCEED = 2
    const val GAME_DAMAGE_EXCEED = 3
    const val GAME_PLAYER_HP_ZERO = 4




}