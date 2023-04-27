package com.example.jogodavelha.usecase

import com.example.jogodavelha.Characters
import com.example.jogodavelha.GameState
import com.example.jogodavelha.model.GameStatus
import javax.inject.Inject

class EasyGameUseCase @Inject constructor() {

    lateinit var machine: Characters
    private val winCombinations = listOf(
        listOf(0, 1, 2),
        listOf(3, 4, 5),
        listOf(6, 7, 8),
        listOf(0, 3, 6),
        listOf(1, 4, 7),
        listOf(2, 5, 8),
        listOf(0, 4, 8),
        listOf(2, 4, 6)
    )

    fun nextMove(gameState: MutableList<Characters>, opponent: Characters): GameStatus {
        machine = if (opponent == Characters.CROSS) Characters.DOT else Characters.CROSS

        val possibleMoves =
            gameState.mapIndexedNotNull { index, c -> if (c == Characters.EMPTY) index else null }
        if (possibleMoves.isNotEmpty()) {
            gameState[possibleMoves.random()] = machine
        }
        return updateGameStatus(gameState, opponent)
    }

    private fun updateGameStatus(
        gameState: MutableList<Characters>,
        opponent: Characters
    ): GameStatus {

        for (combination in winCombinations) {
            val line = combination.map { gameState[it] }
            if (line.count { it == opponent } == 3) {
                return GameStatus(status = GameState.YOU_WIN, gameState)
            } else if (line.count { it == machine } == 3) {
                return GameStatus(status = GameState.OPPONENT_WIN, gameState)
            }
        }

        if (!hasPossibleWin(gameState, opponent)) {
            return GameStatus(status = GameState.DRAW, gameState)
        }
        return GameStatus(status = GameState.YOUR_TURN, gameState)
    }

    private fun hasPossibleWin(gameState: List<Characters>, currentPlayer: Characters): Boolean {
        val nextPlayer = if (currentPlayer == Characters.CROSS) Characters.DOT else Characters.CROSS

        for (combination in winCombinations) {
            val line = combination.map { gameState[it] }
            if (line.count { it == currentPlayer } == 3) {
                return true
            }
        }
        val possibleMoves =
            gameState.mapIndexedNotNull { index, c -> if (c == Characters.EMPTY) index else null }
        for (move in possibleMoves) {
            val newGameState = gameState.toMutableList()
            newGameState[move] = currentPlayer
            if (hasPossibleWin(newGameState.toList(), nextPlayer)) {
                return true
            }
        }
        return false
    }

    fun verifyWin(gameState: MutableList<Characters>, opponent: Characters): GameStatus {
        for (combination in winCombinations) {
            val line = combination.map { gameState[it] }
            if (line.count { it == opponent } == 3) {
                return GameStatus(status = GameState.YOU_WIN, gameState)
            }
        }
        return GameStatus(status = GameState.OPPONENT_TURN, gameState)
    }
}