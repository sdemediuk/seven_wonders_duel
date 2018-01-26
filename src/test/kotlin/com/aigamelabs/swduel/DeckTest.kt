package com.aigamelabs.swduel

import com.aigamelabs.swduel.enums.CardColor
import com.aigamelabs.swduel.enums.CardGroup
import com.aigamelabs.swduel.enums.Resource
import io.vavr.collection.Vector
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.*

class DeckTest : Spek ({
    given("a deck") {
        val firstAgeCards = CardFactory.createFromFirstAge()
        val deck = Deck("", firstAgeCards)

        on("changing name") {
            val newName = "New deck"
            val newDeck = deck.update(name_ = newName)

            it("should return a new deck with the new name") {
                assertEquals(newDeck.name, newName)
            }
        }

        on("changing cards") {
            val secondAgeCards = CardFactory.createFromSecondAge()
            val newDeck = deck.update(cards_ = secondAgeCards)

            it("should return a new deck with the new cards") {
                assertEquals(newDeck.cards, secondAgeCards)
            }
        }

        on("removing a card") {
            val card = firstAgeCards[0]
            val newDeck = deck.removeCard(card)

            it("should return a new deck with the card removed") {
                assertTrue(deck.cards.contains(card))
                assertTrue(!newDeck.cards.contains(card))
            }
        }

        on("drawing a card") {
            val drawOutcome = deck.drawCard()
            val drawnCard = drawOutcome.first
            val newDeck = drawOutcome.second

            it("should return a new deck with the card removed") {
                assertTrue(deck.cards.contains(drawnCard))
                assertTrue(!newDeck.cards.contains(drawnCard))
            }
        }

        on("drawing multiple cards") {
            val drawOutcome = deck.drawCards(3)
            val drawnCards = drawOutcome.first
            val newDeck = drawOutcome.second

            it("should return a new deck with the cards removed") {
                assertTrue(deck.cards.containsAll(drawnCards))
                drawnCards.forEach { c -> assertFalse(newDeck.cards.contains(c)) }
            }
        }

        on("adding a card") {
            val card = Card(cardGroup = CardGroup.SECOND_AGE, name = "Sawmill", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.WOOD to 2))
            val newDeck = deck.add(card)

            it("should return a new deck with the card added") {
                assertFalse(deck.cards.contains(card))
                assertTrue(newDeck.cards.contains(card))
            }
        }

        on("adding multiple cards") {
            val newCards = Vector.of(
                    Card(cardGroup = CardGroup.SECOND_AGE, name = "Sawmill", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.WOOD to 2)),
                    Card(cardGroup = CardGroup.SECOND_AGE, name = "Brickyard", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.CLAY to 2)),
                    Card(cardGroup = CardGroup.SECOND_AGE, name = "Shelf quarry", color = CardColor.BROWN, coinCost = 2, resourcesProduced = hashMapOf(Resource.STONE to 2))
            )
            val newDeck = deck.addAll(newCards)

            it("should return a new deck with the cards added") {
                newCards.forEach { c -> assertFalse(deck.cards.contains(c)) }
                assertTrue(newDeck.cards.containsAll(newCards))
            }
        }

        on("merging two decks") {
            val secondAgeCards = CardFactory.createFromSecondAge()
            val otherDeck = Deck("", secondAgeCards)
            val newDeck = deck.merge(otherDeck)

            it("should return a new deck with the cards added") {
                secondAgeCards.forEach { c -> assertFalse(deck.cards.contains(c)) }
                assertTrue(otherDeck.cards.containsAll(secondAgeCards))
                assertTrue(newDeck.cards.containsAll(firstAgeCards))
                assertTrue(newDeck.cards.containsAll(secondAgeCards))
            }
        }

        on("a querying for the size of a deck") {

            it("should return the number of cards in the deck") {
                assertEquals(deck.size(), 23)
            }
        }
    }
})