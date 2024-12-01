package com.example.eggenda.gameTutorial

import com.example.eggenda.R


class tutorials {

    val tutTotalNum = 5

    fun getTutorialById(id: Int): tutorial?{
        val tutorialMap: Map<Int, () -> tutorial> = mapOf(
            // TODO
            0 to {tutorial1()},
            1 to {tutorial2()},
            2 to {tutorial3()},
            3 to {tutorial4()},
            4 to {tutorial5()},

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

    class tutorial4():tutorial{
        override val coverDescription: String = "Advanced mechanic: Objectives in the game"
        override val coverImage: Int = R.drawable.tut4_cover
        override val pagesImage: IntArray = intArrayOf(R.drawable.tut4_p1, R.drawable.tut4_p2,R.drawable.tut4_p3, R.drawable.tut4_p4)
        override val description: Array<String> = arrayOf("Each stage has different requirements, you can click STAGE INFO to check it.",
            "The first type of objective is: No damage restriction. Means you have to kill the enemy within the max turn, but you can deal more damage than the hp of them",
            "The second type of objective is: Deal EXACT damage. Means you have to deal damage within the max turn EXACTLY equals to the HP of the enemy",
            "The last type of objective is: Boss fight. Means you have to kill the boss within the max turn with no damage restriction, but the boss can fight you back or move you pets position.")
        override val totalPages: Int = pagesImage.size
    }

    class tutorial5():tutorial{
        override val coverDescription: String = "Advanced mechanic: Elements"
        override val coverImage: Int = R.drawable.tut5_cover
        override val pagesImage: IntArray = intArrayOf(R.drawable.tut5_p1, R.drawable.tut5_p2,R.drawable.tut5_p3)
        override val description: Array<String> = arrayOf("We have three elements in this game: Forest, Water and Fire.",
            "Some stages allow you to deal damage with all kind of elements, but some stages allow accept one of those three",
            "We will add more interesting stuff for elements once we get more fund!")
        override val totalPages: Int = pagesImage.size
    }
}