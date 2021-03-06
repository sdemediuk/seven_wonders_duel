package com.aigamelabs.swduel.players

import com.aigamelabs.game.Action
import com.aigamelabs.game.GameData
import com.aigamelabs.game.Player
import com.aigamelabs.game.PlayerTurn
import com.aigamelabs.mcts.ActionSelection
import com.aigamelabs.mcts.ActionSelector
import com.aigamelabs.mcts.NodeScoreMapper
import com.aigamelabs.mcts.NodeScoreMapping
import com.aigamelabs.mcts.uctparallelization.UctParallelizationManager
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.enums.Resource
import com.aigamelabs.swduel.enums.ResourcesAlternative
import com.aigamelabs.swduel.opponent
import com.aigamelabs.utils.RandomWithTracker
import java.util.*
import kotlin.collections.HashMap

class KeyboardPlayer(
        player: PlayerTurn,
        playerId: String,
        gameId: String,
        gameData: GameData,
        outPath: String? = null
) : Player<GameState>(playerId, gameData) {
    private val scanner = Scanner(System.`in`)
    private val generator = RandomWithTracker(Random().nextLong())
    private var manager = UctParallelizationManager(
            player,
            ActionSelection.get(ActionSelector.HIGHEST_SCORE),
            PlayerTurn.getPlayers(gameData.controllers.size)
                    .map { Pair(it, NodeScoreMapping.get(NodeScoreMapper.IDENTITY)) }
                    .toMap(),
            PlayerTurn.getPlayers(gameData.controllers.size)
                    .map { Pair(it, StateEvaluation.getVictoryEvaluator(it)) }
                    .toMap(),
            outPath,
            false,
            gameId,
            playerId
    )

    override fun decide(gameState: GameState): Action<GameState> {
        val (_, thisDecision) = gameState.dequeDecision()
        val options = thisDecision.options
        println("Decide one of the following options:")
        println("  0. Run MCTS and print analysis")
        options.forEachIndexed { idx, action ->
            println("  ${idx+1}. ${getActionMessage(action, gameState, thisDecision.player)}")
        }
        println("Player info:\n${getHelpersMessage(gameState, thisDecision.player)}")
        println("Opponent info:\n${getHelpersMessage(gameState, thisDecision.player.opponent())}")
        var choice = readInt(0, options.size())
        if (choice == 0) {
            manager.run(gameState)
            println("MCTS analysis:")
            println(manager.rootNode!!)
            choice = readInt(1, options.size())
        }
        return options[choice - 1]
    }

    private fun getHelpersMessage(gameState: GameState, player: PlayerTurn): String {
        val playerCity = gameState.getPlayerCity(player)
        val opponentCity = gameState.getPlayerCity(player.opponent())

        val production = HashMap<Resource,Int>()
        val altProduction = HashMap<ResourcesAlternative,Int>()
        for (building in playerCity.buildings.plus(playerCity.wonders)) {
            // Check linking symbols
            // Calculate resources production
            building.resourceProduction.forEach { resource, tot ->
                production[resource] = if (production.containsKey(resource))
                    production[resource]!! + tot
                else
                    tot
            }
            // Calculate resource alternatives production
            if (building.resourceAlternativeProduction != ResourcesAlternative.NONE) {
                val altPr = building.resourceAlternativeProduction
                altProduction[altPr] = if (altProduction.containsKey(altPr))
                    altProduction[altPr]!! + 1
                else
                    1
            }
        }

        val costs = HashMap<Resource,Int>()
        Resource.values().map {
            val opponentProduction = opponentCity.pureResourceProduction(it)
            if (playerCity.hasTradingAgreement(it)) 1 else 2 + opponentProduction
        }

        val ret = StringBuilder()
        ret.append("Coins: ${playerCity.coins}\n")

        if (!production.isEmpty() || !altProduction.isEmpty()) {
            ret.append("Production:\n")
            production
                    .map {
                        when (it.key) {
                            Resource.WOOD -> "Wood: ${it.value}"
                            Resource.STONE -> "Stone: ${it.value}"
                            Resource.CLAY -> "Clay: ${it.value}"
                            Resource.GLASS -> "Glass: ${it.value}"
                            Resource.PAPER -> "Paper: ${it.value}"
                        }
                    }
                    .forEach {
                        ret.append(it)
                        ret.append("\n")
                    }

            altProduction
                    .filter { it.key != ResourcesAlternative.NONE && it.key != ResourcesAlternative.ANY }
                    .map {
                        when (it.key) {
                            ResourcesAlternative.WOOD_OR_CLAY_OR_STONE -> "${it.value} of Wood, Clay or Stone"
                            ResourcesAlternative.GLASS_OR_PAPER -> "${it.value} of Glass or Paper"
                            else -> throw Exception("This should be impossible")
                        }
                    }
                    .forEach {
                        ret.append(it)
                        ret.append("\n")
                    }
        }

        if (!costs.isEmpty()) {
            ret.append("\nCosts:\n")
            costs.map {
                when (it.key) {
                    Resource.WOOD -> "Wood: ${it.value}"
                    Resource.STONE -> "Stone: ${it.value}"
                    Resource.CLAY -> "Clay: ${it.value}"
                    Resource.GLASS -> "Glass: ${it.value}"
                    Resource.PAPER -> "Paper: ${it.value}"
                }
            }
                    .forEach {
                        ret.append(it)
                        ret.append("\n")
                    }
        }

        return ret.toString()
    }

    private fun getActionMessage(action: Action<GameState>, gameState: GameState, player: PlayerTurn): String {
        val origCoins = gameState.getPlayerCity(player).coins
        val updatedGameState = gameState.applyAction(action, generator)
        val newCoins = updatedGameState.getPlayerCity(player).coins
        val diff = newCoins - origCoins
        return if (diff == 0)
            "$action [for free]"
        else
            "$action [$diff coins: $origCoins -> $newCoins]"
    }

    private fun readInt(inclusiveLowerBound: Int, inclusiveUpperBound: Int): Int {
        do {
            try {
                val value = scanner.nextInt()
                if (value in inclusiveLowerBound..inclusiveUpperBound)
                    return value
            } catch (e: NoSuchElementException) {}
        } while (true)
    }

    override fun init() {}
    override fun finalize(gameState: GameState) {}
    override fun close() {}
}