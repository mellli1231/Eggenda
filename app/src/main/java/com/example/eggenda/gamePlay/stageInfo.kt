package com.example.eggenda.gamePlay

import com.example.eggenda.R


class stageInfo {

    fun StageInfoMap(id: Int): stage?{
        val petMap: Map<Int, () -> stage> = mapOf(
            0 to { stage_0() },
            1 to { stage_1() },
            2 to { stage_2() },
        )
        return petMap[id]?.invoke()
    }


    interface stage{
        val id: Int
        val name: String
        val bossImageId: Int
        val element: Int
        val objectiveType: Int
        val maxTurn: Int
        val damageRequirement: Int
        val description: String

        fun actionType(turn:Int ):Int
        fun actionAmount(turn: Int):Int
        fun actionDescription(turn: Int):String

    }

    private class stage_0():stage{
        override val id: Int = 0
        override val name: String = "Dummy"
        override val bossImageId: Int = R.drawable.game_enemy_stamp
        override val element: Int = dict.STAGE_ACCEPT_ALL_ELEMENT
        override val objectiveType: Int = dict.STAGE_OBJECTIVE_BEST
        override val maxTurn: Int = 9
        override val damageRequirement: Int = 150
        override val description: String = ""

        override fun actionType(turn: Int): Int {
            return dict.STAGE_ACTION_NO_ACTION
        }

        override fun actionAmount(turn: Int): Int {
            return 0
        }

        override fun actionDescription(turn: Int): String {
            return ""
        }
    }


    private class stage_1():stage{
        override val id: Int = 1
        override val name: String = "Dummy"
        override val bossImageId: Int = R.drawable.game_enemy_stamp
        override val element: Int = dict.ELEMENT_FIRE
        override val objectiveType: Int = dict.STAGE_OBJECTIVE_EXACT
        override val maxTurn: Int = 9
        override val damageRequirement: Int = 134
        override val description: String = ""

        override fun actionType(turn: Int): Int {
            return dict.STAGE_ACTION_NO_ACTION
        }

        override fun actionAmount(turn: Int): Int {
            return 0
        }

        override fun actionDescription(turn: Int): String {
            return ""
        }
    }

    private class stage_2():stage{
        override val id: Int = 2
        override val name: String = "Goblin"
        override val bossImageId: Int = R.drawable.game_enemy_goblin
        override val element: Int = dict.STAGE_ACCEPT_ALL_ELEMENT
        override val objectiveType: Int = dict.STAGE_OBJECTIVE_FIGHT
        override val maxTurn: Int = 10
        override val damageRequirement: Int = 230
        override val description: String = ""

        override fun actionType(turn: Int): Int {
            if(turn % 2 == 0){
                return dict.STAGE_ACTION_ATTACK
            }
            return dict.STAGE_ACTION_NO_ACTION
        }

        override fun actionAmount(turn: Int): Int {
            if(turn % 2 == 0){
                return 20
            }
            return 0
        }

        override fun actionDescription(turn: Int): String {
            if(turn % 2 == 0){
                return name+" dealt "+actionAmount(turn)+" damages to you!!"
            }
            return ""
        }
    }


}