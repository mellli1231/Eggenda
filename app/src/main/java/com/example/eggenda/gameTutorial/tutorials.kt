package com.example.eggenda.gameTutorial

import com.example.eggenda.R


class tutorials {

    val tutTotalNum = 3

    fun getTutorialById(id: Int): tutorial?{
        val tutorialMap: Map<Int, () -> tutorial> = mapOf(
            // TODO
            0 to {tutorial1()},
            1 to {tutorial2()},
            2 to {tutorial3()}

        )
        return tutorialMap[id]?.invoke()
    }
    interface tutorial{
        val coverDescription: String
        val coverImage: Int
        val pagesImage: IntArray
        val description: Array<String>
        val totalPages: Int
    }

    class tutorial1():tutorial{
        override val coverDescription: String = "Basic mechanic: put your pets"
        override val coverImage: Int = R.drawable.tut1_cover

        override val pagesImage: IntArray = intArrayOf(R.drawable.tut1_p1, R.drawable.tut1_p2, R.drawable.tut1_p3)
        override val description: Array<String> = arrayOf("Click a pet on your deck.",
            "Click a box on the board (the orange dot).",
            "Done")
        override val totalPages: Int = pagesImage.size
    }

    class tutorial2():tutorial{
        override val coverDescription: String = "Basic mechanic: push your pet"
        override val coverImage: Int = R.drawable.tut2_cover
        override val pagesImage: IntArray = intArrayOf(R.drawable.tut2_p1, R.drawable.tut2_p2, R.drawable.tut2_p3)
        override val description: Array<String> = arrayOf("if we put a pet on this position (orange dot)",
            "It will push all the pet away from it!",
            "The position of flame skull and bunny are changed. Notice that the bunny is returned to your deck because it was placed on the boundary")
        override val totalPages: Int = pagesImage.size
    }


    class tutorial3():tutorial{
        override val coverDescription: String = "Basic mechanic: Attack and Victory"
        override val coverImage: Int = R.drawable.tut3_cover
        override val pagesImage: IntArray = intArrayOf(R.drawable.tut3_p1, R.drawable.tut3_p2,R.drawable.tut3_p3, R.drawable.tut3_p4)
        override val description: Array<String> = arrayOf("Each pet has its unique skill, when the condition is met, the counting will become 0.",
            "Long press on the icon to check how to attack",
            "Your pet can deal damage when the condition is matched",
            "Generally you can win the game when the hp of the enemy become zero!")
        override val totalPages: Int = pagesImage.size
    }
}