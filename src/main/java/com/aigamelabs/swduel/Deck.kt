package com.aigamelabs.swduel

import java.util.concurrent.ThreadLocalRandom

import com.aigamelabs.swduel.enums.CardGroup
import io.vavr.collection.Vector
import io.vavr.control.Try

open class Deck(private val name: String, private val cards: Vector<Card>) {

    constructor(name: String) : this(name, Vector.empty())

    fun drawCard() : Try<Pair<Card, Deck>> {
        return if (cards.size() > 0) {
            val card_index = ThreadLocalRandom.current().nextInt(0, cards.size() + 1)
            val drawnCard = cards[card_index]
            val newDeck = Deck(name, cards.removeAt(card_index))
            Try.success(Pair(drawnCard, newDeck))
        }
        else {
            Try.failure(Exception("The cardGroup is empty."))
        }
    }

    fun add(first: Card) : Deck {
        return Deck(name, cards.append(first))
    }

    fun addAll(deck : Deck) : Deck {
        return Deck(name, cards.appendAll(deck.cards))
    }
}