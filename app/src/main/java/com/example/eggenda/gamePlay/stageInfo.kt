package com.example.eggenda.gamePlay

import com.example.eggenda.R

class stageInfo {

    data class StageInfo(
        val name: String,
        val bossImageId: Int,
        val bossHurtImageId: Int,
        val element: Int,
        val objectiveType: Int,
        val turn: Int,
        val damage: Int
    )

    private val Stage_1_Info = StageInfo(
        name = "Goblin",
        bossImageId = R.drawable.game_stage1,
        bossHurtImageId = R.drawable.game_stage1,
        element = dict.STAGE_ACCEPT_ALL_ELEMENT,
        objectiveType = dict.STAGE_OBJECTIVE_BEST,
        turn = 13,
        damage = 230
    )

    private val Stage_2_Info = StageInfo(
        name = "Icy ice",
        bossImageId = R.drawable.game_stage2,
        bossHurtImageId = R.drawable.game_stage2,
        element = dict.ELEMENT_FIRE,
        objectiveType = dict.STAGE_OBJECTIVE_EXACT,
        turn = 9,
        damage = 134
    )
    val StageInfoMap: Map<Int, StageInfo> = mapOf(
        0 to Stage_1_Info,
        1 to Stage_2_Info
    )


}