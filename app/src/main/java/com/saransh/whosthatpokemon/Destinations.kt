package com.saransh.whosthatpokemon

interface Destinations{
    val route : String
    val title : String?
}

object Home : Destinations{
    override val route = "home"
    override val title = "Quiz"
}

object Easy : Destinations{
    override val route = "easy"
    override val title: String? = null
}

object Medium : Destinations{
    override val route = "medium"
    override val title: String? = null
}

object Hard : Destinations{
    override val route = "hard"
    override val title: String? = null
}

object VeryHard : Destinations{
    override val route = "very hard"
    override val title: String? = null
}

object Settings : Destinations{
    override val route = "settings"
    override val title = "Settings"
}