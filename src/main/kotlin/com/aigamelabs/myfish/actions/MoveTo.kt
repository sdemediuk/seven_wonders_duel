package com.aigamelabs.myfish.actions

import com.aigamelabs.game.Action
import com.aigamelabs.utils.RandomWithTracker
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.myfish.GameState
import java.util.logging.Logger

class MoveTo(playerTurn: PlayerTurn, private val penguinId: Int, private val location: Triple<Int, Int, Int>) : Action<GameState>(playerTurn) {
    override fun process(gameState: GameState, generator : RandomWithTracker, logger: Logger?) : GameState {
        // TODO
        return gameState
    }

    override fun toString(): String {
        return "Move penguin $penguinId to location $location"
    }
}