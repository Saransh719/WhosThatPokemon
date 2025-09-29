package com.saransh.whosthatpokemon

interface Destinations{
    val route : String;
}

object Home : Destinations{
    override val route = "home"
}

object Easy : Destinations{
    override val route = "easy"
}

object Medium : Destinations{
    override val route = "medium"
}

object Hard : Destinations{
    override val route = "hard"
}
