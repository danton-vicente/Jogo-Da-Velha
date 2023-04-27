package com.example.jogodavelha

import androidx.lifecycle.ViewModel
import com.example.jogodavelha.model.GameStatus
import com.example.jogodavelha.usecase.EasyGameUseCase
import com.example.jogodavelha.usecase.HardGameUseCase
import com.example.jogodavelha.usecase.MediumGameUseCase
import com.example.jogodavelha.utils.Constants.SWITCH_CROSS
import com.example.jogodavelha.utils.Constants.SWITCH_DOT
import com.example.jogodavelha.utils.Constants.SWITCH_EASY
import com.example.jogodavelha.utils.Constants.SWITCH_HARD
import com.example.jogodavelha.utils.Constants.SWITCH_MEDIUM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val hardUseCase: HardGameUseCase,
    private val mediumGameUseCase: MediumGameUseCase,
    private val easyGameUseCase: EasyGameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(JogoDaVelhaState())
    val uiState: StateFlow<JogoDaVelhaState> = _uiState.asStateFlow()

    init {
        _uiState.value = uiState.value.copy(
            values = initGameArray()
        )
    }

    private fun initGameArray(): ArrayList<Characters> {
        val array = ArrayList<Characters>()
        for (i in 0..8) {
            array.add(Characters.EMPTY)
        }
        return array
    }

    fun toolbarClicked() {
        _uiState.value = uiState.value.copy(
            appBarExpanded = !uiState.value.appBarExpanded
        )
    }

    fun switchClicked(switchName: String) {
        val newUiState = when (switchName) {
            SWITCH_EASY -> uiState.value.copy(difficulty = Difficulties.EASY)
            SWITCH_MEDIUM -> uiState.value.copy(difficulty = Difficulties.MEDIUM)
            SWITCH_HARD -> uiState.value.copy(difficulty = Difficulties.HARD)
            SWITCH_CROSS -> uiState.value.copy(userCharacters = Characters.CROSS)
            SWITCH_DOT -> uiState.value.copy(userCharacters = Characters.DOT)
            else -> null
        }
        if (newUiState != null) {
            _uiState.value = newUiState
        }
    }

    fun buttonClicked() {
        val newGameState = when (_uiState.value.gameState) {
            GameState.IDLE -> GameState.PRIZE_INIT_PLAYER
            GameState.YOU_WIN, GameState.OPPONENT_WIN, GameState.DRAW -> GameState.IDLE
            else -> null
        }
        if (newGameState == GameState.IDLE) {
            _uiState.value = uiState.value.copy(values = initGameArray(), gameState = newGameState)

        } else if (newGameState != null) {
            _uiState.value = uiState.value.copy(gameState = newGameState)
        }
    }

    fun onPositionClicked(position: Int) {
        if (uiState.value.values[position] != Characters.EMPTY) return
        val updatedArray = uiState.value.values
        updatedArray[position] = uiState.value.userCharacters
        verifyWinByDifficulty()
    }

    private fun verifyWinByDifficulty() {
        val gameState = when (uiState.value.difficulty) {
            Difficulties.EASY -> easyGameUseCase.verifyWin(
                uiState.value.values,
                uiState.value.userCharacters
            )

            Difficulties.MEDIUM -> mediumGameUseCase.verifyWin(
                uiState.value.values,
                uiState.value.userCharacters
            )

            Difficulties.HARD -> hardUseCase.verifyWin(
                uiState.value.values,
                uiState.value.userCharacters
            )
        }
        _uiState.value = uiState.value.copy(
            values = gameState.values,
            gameState = gameState.status
        )
    }

    fun playAsCpuByDifficulty(
        updatedArray: MutableList<Characters>? = null
    ) {
        val nextMove = when (_uiState.value.difficulty) {
            Difficulties.EASY -> easyGameUseCase.nextMove(
                gameState = updatedArray ?: uiState.value.values,
                opponent = uiState.value.userCharacters
            )

            Difficulties.MEDIUM -> mediumGameUseCase.nextMove(
                gameState = updatedArray ?: uiState.value.values,
                opponent = uiState.value.userCharacters
            )

            Difficulties.HARD -> hardUseCase.nextMove(
                gameState = updatedArray ?: uiState.value.values,
                opponent = uiState.value.userCharacters
            )
        }
        updateViewWithCpuPlay(nextMove)
    }

    private fun updateViewWithCpuPlay(gameStatus: GameStatus) {
        _uiState.value = uiState.value.copy(
            gameState = gameStatus.status,
            values = gameStatus.values
        )
    }

    fun prizeInitPlayer() {
        val initChar = if (Math.random() < 0.5) Characters.CROSS else Characters.DOT
        val gameState = when {
            initChar == uiState.value.userCharacters -> GameState.YOU_INIT
            uiState.value.userCharacters == Characters.CROSS -> GameState.OPPONENT_INIT_DOT
            else -> GameState.OPPONENT_INIT_CROSS
        }
        _uiState.value = uiState.value.copy(gameState = gameState)
    }
}
