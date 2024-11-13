package com.example.eggenda.gamePlay

import com.example.eggenda.R


class stageInfo {

    fun StageInfoMap(id: Int): stage?{
        val petMap: Map<Int, () -> stage> = mapOf(
            0 to { stage_0() },
            1 to { stage_1() },
            2 to { stage_2() },
            3 to { stage_3() },
        )
        return petMap[id]?.invoke()
    }


    interface stage{
        val id: Int //id of the stage
        val name: String    //name of the boss
        val bossImageId: Int    //boss image id
        val acceptElement: Int    //accept element from player
        val objectiveType: Int  //obj type
        val maxTurn: Int    //max turn for player to fight
        val damageRequirement: Int  //boss hp
        val description: String //for UI use
        val deckSize: Int   //deckSize limitation for players

        //for game play use
        fun actionType(turn:Int ):Int
        fun actionAmount(turn: Int, petStatus: Array<petStatus?>):Int
        fun actionDescription(turn: Int,petStatus: Array<petStatus?>):String

    }

    private class stage_0():stage{
        override val id: Int = 0
        override val name: String = "Dummy"
        override val bossImageId: Int = R.drawable.game_enemy_stamp
        override val acceptElement: Int = dict.STAGE_ACCEPT_ALL_ELEMENT
        override val objectiveType: Int = dict.STAGE_OBJECTIVE_BEST
        override val maxTurn: Int = 9
        override val damageRequirement: Int = 150
        override val description: String = ""
        override val deckSize:Int = 3

        override fun actionType(turn: Int): Int {
            return dict.STAGE_ACTION_NO_ACTION
        }

        override fun actionAmount(turn: Int,petStatus: Array<petStatus?>): Int {
            return 0
        }

        override fun actionDescription(turn: Int,petStatus: Array<petStatus?>): String {
            return ""
        }
    }


    private class stage_1():stage{
        override val id: Int = 1
        override val name: String = "Dummy"
        override val bossImageId: Int = R.drawable.game_enemy_stamp
        override val acceptElement: Int = dict.STAGE_ACCEPT_ALL_ELEMENT
        override val objectiveType: Int = dict.STAGE_OBJECTIVE_EXACT
        override val maxTurn: Int = 8
        override val damageRequirement: Int = 230
        override val description: String = ""
        override val deckSize:Int = 4

        override fun actionType(turn: Int): Int {
            return dict.STAGE_ACTION_NO_ACTION
        }

        override fun actionAmount(turn: Int,petStatus: Array<petStatus?>): Int {
            return 0
        }

        override fun actionDescription(turn: Int,petStatus: Array<petStatus?>): String {
            return ""
        }
    }

    private class stage_2():stage{
        override val id: Int = 2
        override val name: String = "Goblin"
        override val bossImageId: Int = R.drawable.game_enemy_goblin
        override val acceptElement: Int = dict.STAGE_ACCEPT_ALL_ELEMENT
        override val objectiveType: Int = dict.STAGE_OBJECTIVE_FIGHT
        override val maxTurn: Int = 10
        override val damageRequirement: Int = 260
        override val description: String = ""
        override val deckSize:Int = 4

        override fun actionType(turn: Int): Int {
            if(turn % 2 == 0){
                return dict.STAGE_ACTION_ATTACK
            }
            return dict.STAGE_ACTION_NO_ACTION
        }

        override fun actionAmount(turn: Int,petStatus: Array<petStatus?>): Int {
            if(turn % 2 == 0){
                return 20
            }
            return 0
        }

        override fun actionDescription(turn: Int, petStatus: Array<petStatus?>): String {
            if(turn % 2 == 0){
                return name+" dealt "+actionAmount(turn,petStatus)+" damages to you!!"
            }
            return ""
        }
    }

    private class stage_3():stage{
        override val id: Int = 3
        override val name: String = "Icy"
        override val bossImageId: Int = R.drawable.game_enemy_icy
        override val acceptElement: Int = dict.STAGE_ACCEPT_ALL_ELEMENT
        override val objectiveType: Int = dict.STAGE_OBJECTIVE_FIGHT
        override val maxTurn: Int = 6
        override val damageRequirement: Int = 160
        override val description: String = "Icy will pull your pet for comfy hug :3"
        override val deckSize:Int = 3

        override fun actionType(turn: Int): Int {
            if(turn % 3 == 0){
                return dict.STAGE_ACTION_PUSH
            }
            return dict.STAGE_ACTION_NO_ACTION
        }

        override fun actionAmount(turn: Int,petStatus: Array<petStatus?>): Int {
            if(turn % 3 == 0){
                return dict.STAGE_PUSH_NORTH
            }
            return 0
        }

        override fun actionDescription(turn: Int,petStatus: Array<petStatus?>): String {
            if(turn % 3 == 0){
                return name+" pull your pet to the "+dict.PUSH_STRING[actionAmount(turn,petStatus)]+"!!"
            }
            return ""
        }
    }


}