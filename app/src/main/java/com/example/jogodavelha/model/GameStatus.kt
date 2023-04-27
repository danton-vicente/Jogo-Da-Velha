package com.example.jogodavelha.model

import com.example.jogodavelha.Characters
import com.example.jogodavelha.GameState

data class GameStatus(
    val status: GameState,
    val values: MutableList<Characters>
)