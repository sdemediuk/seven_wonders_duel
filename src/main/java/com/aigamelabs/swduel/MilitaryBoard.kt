package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.PlayerTurn

/**
 * Represents the military situation.
 */
data class MilitaryBoard(
        val conflictPawnPosition: Int,
        val token1P1Present : Boolean,
        val token2P1Present : Boolean,
        val token1P2Present : Boolean,
        val token2P2Present : Boolean
) {
    /*
    Stores an integer representing the position of the conflict pawn, with positive values indicating an advantage for
    player 1 and negative values indicating an advantage for player 2.

    Also, stores four booleans, one for each military token.
     */
    constructor() : this(0, true, true, true, true)

    /**
     * Creates a new instance, with every field updated as specified. Null values are ignored.
     *
     * @param conflictPawnPosition_ the new position of the conflict pawn
     * @param token1P1Present_ the new value for the flag indicating whether token 1 of player 1 is present
     * @param token1P2Present_ the new value for the flag indicating whether token 1 of player 2 is present
     * @param token2P1Present_ the new value for the flag indicating whether token 2 of player 1 is present
     * @param token2P2Present_ the new value for the flag indicating whether token 2 of player 2 is present
     */
    fun update(
            conflictPawnPosition_: Int? = null,
            token1P1Present_ : Boolean? = null,
            token2P1Present_ : Boolean? = null,
            token1P2Present_ : Boolean? = null,
            token2P2Present_ : Boolean? = null
    ) : MilitaryBoard {
        return MilitaryBoard(
                conflictPawnPosition_ ?: conflictPawnPosition,
                token1P1Present_ ?: token1P1Present,
                token2P1Present_ ?: token2P1Present,
                token1P2Present_ ?: token1P2Present,
                token2P2Present_ ?: token2P2Present
        )
    }

    /**
     * Adds military points to the given player.
     *
     * @param n the amount of military points
     * @param playerTurn the player of interest
     * @return the cost in coins to be paid and an updated instance of the military board
     */
    fun addMilitaryPointsTo(n : Int, playerTurn: PlayerTurn) : Pair<Int,MilitaryBoard> {
        return when (playerTurn) {
            PlayerTurn.PLAYER_1 -> {
                addPointsToPlayer1(n)
            }
            PlayerTurn.PLAYER_2 -> {
                addPointsToPlayer2(n)
            }
        }
    }

    private fun addPointsToPlayer1(n : Int) : Pair<Int, MilitaryBoard> {
        val newPosition = conflictPawnPosition - n
        var cost = 0
        cost += if (token1P1Present && newPosition <= -3) 2 else 0
        cost += if (token1P1Present && newPosition <= -6) 5 else 0
        val newToken1P1Present = token1P1Present && newPosition >= -3
        val newToken2P1Present = token2P1Present && newPosition >= -6
        val newBoard = update(
                conflictPawnPosition_ = newPosition,
                token1P1Present_ = newToken1P1Present,
                token2P1Present_ = newToken2P1Present
        )
        return Pair(cost, newBoard)
    }

    private fun addPointsToPlayer2(n : Int) : Pair<Int, MilitaryBoard> {
        val newPosition = conflictPawnPosition + n
        var cost = 0
        cost += if (token1P2Present && newPosition >= +3) 2 else 0
        cost += if (token1P2Present && newPosition >= +6) 5 else 0
        val newToken1P2Present = token1P2Present && newPosition >= +3
        val newToken2P2Present = token2P2Present && newPosition >= +6
        val newBoard = update(
                conflictPawnPosition_ = newPosition,
                token1P2Present_ = newToken1P2Present,
                token2P2Present_ = newToken2P2Present
        )
        return Pair(cost, newBoard)
    }

    fun isMilitarySupremacy() : Boolean {
        return Math.abs(conflictPawnPosition) >= 9
    }

    fun getDisadvantagedPlayer() : PlayerTurn? {
        return if (conflictPawnPosition > 0) {
            PlayerTurn.PLAYER_2
        } else if (conflictPawnPosition < 0)
            PlayerTurn.PLAYER_1
        else {
            null
        }
    }
}