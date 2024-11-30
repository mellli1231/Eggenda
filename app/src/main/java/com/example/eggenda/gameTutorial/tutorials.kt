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
        val totalPages: Int
        val pagesImage: IntArray
        val description: Array<String>
    }

    class tutorial1():tutorial{
        override val coverDescription: String = "Basic mechanic: put your pets"
        override val coverImage: Int = R.drawable.tut1_cover
        override val totalPages: Int = 2
        override val pagesImage: IntArray = intArrayOf(R.drawable.tut1_p1, R.drawable.tut1_p2)
        override val description: Array<String> = arrayOf("Click a pet on your deck.", "Click a box on the board.")
    }

    class tutorial2():tutorial{
        override val coverDescription: String = "Basic mechanic: push your pet"
        override val coverImage: Int = R.drawable.tut2_cover
        override val totalPages: Int = 2
        override val pagesImage: IntArray = intArrayOf(R.drawable.tut2_p1, R.drawable.tut2_p2)
        override val description: Array<String> = arrayOf("What happen if we put a pet on this position?", "It will push all the pet away from it! Notice that the bunny is returned to the deck, because it was on the boundary of the board.")
    }


    class tutorial3():tutorial{
        override val coverDescription: String = "Basic mechanic: Attack"
        override val coverImage: Int = R.drawable.tut3_cover
        override val totalPages: Int = 3
        override val pagesImage: IntArray = intArrayOf(R.drawable.tut3_p1, R.drawable.tut3_p2,R.drawable.tut3_p3)
        override val description: Array<String> = arrayOf("Each pet has its unique skill!.", "Long press on the icon to check it", "Your pet can deal damage when the condition is matched")
    }
}