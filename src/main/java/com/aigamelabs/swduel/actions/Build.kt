package com.aigamelabs.swduel.actions

import com.aigamelabs.swduel.Card
import com.aigamelabs.swduel.GameState
import com.aigamelabs.swduel.MilitaryBoard
import com.aigamelabs.swduel.enums.CardColor
import com.aigamelabs.swduel.enums.PlayerTurn

class Build(playerTurn: PlayerTurn, val card : Card) : Action(playerTurn) {
    override fun process(gameState: GameState) : GameState {

        // Remove card from appropriate deck
        val newCardStructure = gameState.cardStructure!!.pickUpCard(card)

        // Add card to appropriate player city
        val playerCity = gameState.getPlayerCity(playerTurn)
        val newPlayerCity = playerCity.update(buildings_ = playerCity.buildings.add(card))
        var newPlayerCities = gameState.playerCities.put(playerTurn, newPlayerCity)

        // Handle military cards
        val newMilitaryBoard: MilitaryBoard
        if (card.color == CardColor.RED) {
            val additionOutcome = gameState.militaryBoard.addMilitaryPointsTo(card.militaryPoints, playerTurn)
            newMilitaryBoard = additionOutcome.second
            // Apply penalty to opponent city, if any
            val opponentPenalty = additionOutcome.first
            if (opponentPenalty > 0) {
                val opponentCity = gameState.getPlayerCity(playerTurn.opponent())
                val newPlayer2City = opponentCity.update(coins_ = opponentCity.coins - opponentPenalty)
                newPlayerCities = gameState.playerCities.put(playerTurn.opponent(), newPlayer2City)
            }
        } else {
            // Unchanged
            newMilitaryBoard = gameState.militaryBoard
        }

        val newGameState = gameState.update(cardStructure_ = newCardStructure, playerCities_ = newPlayerCities,
                militaryBoard_ = newMilitaryBoard)

        return when {
            card.color == CardColor.GREEN -> newGameState.checkScienceSupremacy(playerTurn)
            card.color == CardColor.RED -> newGameState.checkMilitarySupremacy()
            else -> newGameState
        }
    }
}