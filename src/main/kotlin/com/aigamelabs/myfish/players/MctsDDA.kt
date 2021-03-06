package com.aigamelabs.myfish.players

import com.aigamelabs.game.GameData
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.mcts.*
import com.aigamelabs.myfish.GameState

class MctsDDA(
        player: PlayerTurn,
        playerId: String,
        gameId: String,
        gameData: GameData,
        logFileName: String? = null
) : MctsBasedBot<GameState>(
        player,
        playerId,
        gameId,
        gameData,
        ActionSelection.get(ActionSelector.HIGHEST_SCORE),
        PlayerTurn.getPlayers(gameData.controllers.size)
                .map {
                    if (player == it)
                        Pair(it, NodeScoreMapping.get(NodeScoreMapper.DISTANCE_FROM_MIDPOINT))
                    else
                        Pair(it, NodeScoreMapping.get(NodeScoreMapper.IDENTITY))
                }
                .toMap(),
        PlayerTurn.getPlayers(gameData.controllers.size)
                .map { Pair(it, StateEvaluation.getVictoryEvaluator(it)) }
                .toMap(),
        logFileName
)