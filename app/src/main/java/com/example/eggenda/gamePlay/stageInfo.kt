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
        element = 4,
        objectiveType = dict.STAGE_OBJECTIVE_BEST,
        turn = 12,
        damage = 230
    )
    val StageInfoMap: Map<Int, StageInfo> = mapOf(
        0 to Stage_1_Info
    )
}