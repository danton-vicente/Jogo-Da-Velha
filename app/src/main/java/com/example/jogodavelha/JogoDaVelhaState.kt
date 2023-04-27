package com.example.jogodavelha

data class JogoDaVelhaState(
    val difficulty: Difficulties = Difficulties.EASY,
    val userCharacters: Characters = Characters.CROSS,
    val values: MutableList<Characters> = ArrayList(),
    val gameState: GameState = GameState.IDLE,
    val appBarExpanded: Boolean = false
)

enum class Difficulties {
    EASY, MEDIUM, HARD
}

enum class Characters {
    DOT, CROSS, EMPTY
}

enum class GameState {
    IDLE, PRIZE_INIT_PLAYER, YOU_INIT, OPPONENT_INIT_CROSS, OPPONENT_INIT_DOT,
    YOUR_TURN, OPPONENT_TURN, YOU_WIN, OPPONENT_WIN, DRAW
}
