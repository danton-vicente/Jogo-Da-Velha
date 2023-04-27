package com.example.jogodavelha

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.jogodavelha.components.InfoBottomSheet
import com.example.jogodavelha.databinding.ActivityMainBinding
import com.example.jogodavelha.utils.Constants.SWITCH_CROSS
import com.example.jogodavelha.utils.Constants.SWITCH_DOT
import com.example.jogodavelha.utils.Constants.SWITCH_EASY
import com.example.jogodavelha.utils.Constants.SWITCH_HARD
import com.example.jogodavelha.utils.Constants.SWITCH_MEDIUM
import com.example.jogodavelha.utils.isVisible
import com.example.jogodavelha.utils.setTextAnimation
import com.example.jogodavelha.utils.setTextSubTitleAnimation
import com.example.jogodavelha.utils.updateTextWithAnimation
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var previousUiState: JogoDaVelhaState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        initListeners()
    }

    private fun initListeners() {

        binding.apply {
            topAppBar.isClickable = true
            topAppBar.setOnClickListener {
                viewModel.toolbarClicked()
            }
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.expandOrCollapse -> {
                        viewModel.toolbarClicked()
                        true
                    }

                    else -> false
                }
            }
            val difficultySwitches = listOf(swEasy, swMedium, swHard)
            val characterSwitches = listOf(swCross, swDot)

            val switches = mapOf(
                swEasy to SWITCH_EASY,
                swMedium to SWITCH_MEDIUM,
                swHard to SWITCH_HARD,
                swCross to SWITCH_CROSS,
                swDot to SWITCH_DOT
            )

            switches.forEach { (switch, switchName) ->
                switch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.switchClicked(switchName)
                    } else {
                        val switchGroup = when (switch) {
                            in difficultySwitches -> difficultySwitches
                            in characterSwitches -> characterSwitches
                            else -> null
                        }
                        if (switchGroup != null && switchGroup.none { it.isChecked }) {
                            switch.isChecked = true
                        }
                    }
                }
            }

            val playButtons = listOf(
                casa0, casa1, casa2,
                casa3, casa4, casa5,
                casa6, casa7, casa8
            )
            playButtons.forEachIndexed { index, button ->
                button.setOnClickListener {
                    viewModel.onPositionClicked(index)
                }
            }
            btPlayOrReset.setOnClickListener {
                viewModel.buttonClicked()
            }
        }
    }

    private fun setupViews() = lifecycleScope.launch {
        viewModel.uiState.collect {
            binding.apply {
                if (it.appBarExpanded != previousUiState?.appBarExpanded) {


                    if (!it.appBarExpanded) {
                        topAppBar.setTextSubTitleAnimation(getString(R.string.toolbar_subtitle_expand))
                        topAppBar.menu.getItem(0).icon =
                            ContextCompat.getDrawable(applicationContext, R.drawable.ic_arrow_down)
                    } else {
                        topAppBar.setTextSubTitleAnimation(getString(R.string.toolbar_subtitle_collapse))
                        topAppBar.menu.getItem(0).icon =
                            ContextCompat.getDrawable(applicationContext, R.drawable.ic_arrow_up)
                    }
                    toolbarMoreActions.isVisible(it.appBarExpanded)
                }
            }
            if (it.difficulty != previousUiState?.difficulty) {
                updateDifficultySwitchs(
                    isEasy = it.difficulty == Difficulties.EASY,
                    it.difficulty == Difficulties.MEDIUM,
                    it.difficulty == Difficulties.HARD
                )
            }
            if (it.userCharacters != previousUiState?.userCharacters) {

                updateCharacterSwitchs(
                    isCross = it.userCharacters == Characters.CROSS,
                    isDot = it.userCharacters == Characters.DOT
                )
            }
            it.values.map { newChar ->
                previousUiState?.values?.map { oldChar ->
                    if (newChar != oldChar) updateGame(it.values)
                }
            }
            if (it.gameState != previousUiState?.gameState) {
                when (it.gameState) {
                    GameState.IDLE -> {
                        binding.btPlayOrReset.isVisible(true)
                        binding.btPlayOrReset.text = getString(R.string.init_game)
                        binding.viewPlaying.isVisible(false)
                        enableOrDisableButtons(isEnabled = false)
                        enableOrDisableSwitchs(isEnabled = true)
                    }

                    GameState.PRIZE_INIT_PLAYER -> {
                        val infoBottomSheet = createBottomSheet(R.string.prize_init, R.raw.sorteio)
                        infoBottomSheet.show(supportFragmentManager, InfoBottomSheet.TAG)
                        delay(3.seconds)
                        viewModel.prizeInitPlayer()
                        infoBottomSheet.dismiss()
                        enableOrDisableSwitchs(isEnabled = false)
                    }

                    GameState.YOU_INIT -> {
                        binding.btPlayOrReset.isVisible(false)
                        binding.viewPlaying.isVisible(true)
                        binding.tvPlayingNow.setTextAnimation(getString(R.string.you_init))
                        enableOrDisableButtons(isEnabled = true)
                    }

                    GameState.OPPONENT_INIT_CROSS -> {
                        binding.btPlayOrReset.isVisible(false)
                        binding.viewPlaying.isVisible(true)
                        binding.tvPlayingNow.setTextAnimation(getString(R.string.the_cross_init))
                        enableOrDisableButtons(isEnabled = false)
                        viewModel.playAsCpuByDifficulty()
                    }

                    GameState.OPPONENT_INIT_DOT -> {
                        binding.btPlayOrReset.isVisible(false)
                        binding.viewPlaying.isVisible(true)
                        binding.tvPlayingNow.setTextAnimation(getString(R.string.the_dot_init))
                        enableOrDisableButtons(isEnabled = false)
                        viewModel.playAsCpuByDifficulty()
                    }

                    GameState.YOUR_TURN -> {
                        binding.tvPlayingNow.setTextAnimation(getString(R.string.is_your_turn))
                        enableOrDisableButtons(isEnabled = true)
                    }

                    GameState.OPPONENT_TURN -> {
                        binding.tvPlayingNow.setTextAnimation(getString(R.string.is_opponent_turn))
                        enableOrDisableButtons(isEnabled = false)
                        delay(2.seconds)
                        viewModel.playAsCpuByDifficulty(it.values)
                    }

                    GameState.YOU_WIN -> {
                        val infoBottomSheet = createBottomSheet(R.string.result_win, R.raw.vencedor)
                        infoBottomSheet.show(supportFragmentManager, InfoBottomSheet.TAG)
                        enableOrDisableButtons(isEnabled = false)
                        binding.btPlayOrReset.isVisible(true)
                        binding.viewPlaying.isVisible(false)
                        binding.btPlayOrReset.text = getString(R.string.reset_game)
                    }

                    GameState.OPPONENT_WIN -> {
                        val infoBottomSheet = createBottomSheet(R.string.result_lose, R.raw.derrota)
                        infoBottomSheet.show(supportFragmentManager, InfoBottomSheet.TAG)
                        enableOrDisableButtons(isEnabled = false)
                        binding.btPlayOrReset.isVisible(true)
                        binding.viewPlaying.isVisible(false)
                        binding.btPlayOrReset.text = getString(R.string.reset_game)
                    }

                    GameState.DRAW -> {
                        val infoBottomSheet = createBottomSheet(R.string.result_draw, R.raw.derrota)
                        infoBottomSheet.show(supportFragmentManager, InfoBottomSheet.TAG)
                        enableOrDisableButtons(isEnabled = false)
                        binding.btPlayOrReset.isVisible(true)
                        binding.viewPlaying.isVisible(false)
                        binding.btPlayOrReset.text = getString(R.string.reset_game)
                    }
                }
            }
            previousUiState = it
        }
    }

    private fun updateCharacterSwitchs(isCross: Boolean, isDot: Boolean) {
        binding.swCross.isChecked = isCross
        binding.swDot.isChecked = isDot
    }

    private fun updateGame(values: MutableList<Characters>) {
        binding.apply {
            val playButtons = listOf(
                casa0, casa1, casa2,
                casa3, casa4, casa5,
                casa6, casa7, casa8,
            )
            playButtons.forEachIndexed { index, button ->
                setCorrectChar(button, values[index], button.text)
            }
        }
    }

    private fun setCorrectChar(house: MaterialButton, characters: Characters, text: CharSequence) {
        when (characters) {
            Characters.DOT -> if (text != getString(R.string.dot)) house.updateTextWithAnimation(
                getString(R.string.dot)
            )

            Characters.CROSS -> if (text != getString(R.string.cross)) house.updateTextWithAnimation(
                getString(R.string.cross)
            )

            Characters.EMPTY -> if (text != "") house.updateTextWithAnimation("")
        }
    }

    private fun createBottomSheet(text: Int, gif: Int) = InfoBottomSheet(
        text = text,
        gif = gif
    )

    private fun updateDifficultySwitchs(isEasy: Boolean, isMedium: Boolean, isHard: Boolean) {
        binding.swEasy.isChecked = isEasy
        binding.swMedium.isChecked = isMedium
        binding.swHard.isChecked = isHard
    }

    private fun enableOrDisableButtons(isEnabled: Boolean) {
        binding.apply {
            val playButtons = listOf(
                casa0, casa1, casa2,
                casa3, casa4, casa5,
                casa6, casa7, casa8,
            )
            playButtons.map {
                it.isEnabled = isEnabled
            }
        }
    }

    private fun enableOrDisableSwitchs(isEnabled: Boolean) {
        binding.apply {
            val switches = listOf(
                swDot, swMedium, swHard,
                swEasy, swCross,
            )
            switches.map {
                it.isEnabled = isEnabled
            }
        }
    }

}