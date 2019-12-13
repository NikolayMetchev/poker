package org.metchev.poker

import org.metchev.poker.Face.*
import org.metchev.poker.Suit.*
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NonAsciiCharacters")
enum class Suit() {
  `♠`, `♥`, `♦`, `♣`
}

enum class Face {
  `2`, `3`, `4`, `5`, `6`, `7`, `8`, `9`, `10`, JACK, QUEEN, KING, ACE;

  fun next() = values()[(ordinal + 1) % values().size]
  fun previous() = when (ordinal) {
    0 -> values()[values().size - 1]
    else -> values()[(ordinal - 1)]
  }
}


enum class Card(val face: Face, val suit: Suit) {
  ACE_OF_HEARTS(ACE, `♥`),
  KING_OF_HEARTS(KING, `♥`),
  QUEEN_OF_HEARTS(QUEEN, `♥`),
  JACK_OF_HEARTS(JACK, `♥`),
  `10_OF_HEARTS`(`10`, `♥`),
  `9_OF_HEARTS`(`9`, `♥`),
  `8_OF_HEARTS`(`8`, `♥`),
  `7_OF_HEARTS`(`7`, `♥`),
  `6_OF_HEARTS`(`6`, `♥`),
  `5_OF_HEARTS`(`5`, `♥`),
  `4_OF_HEARTS`(`4`, `♥`),
  `3_OF_HEARTS`(`3`, `♥`),
  `2_OF_HEARTS`(`2`, `♥`),

  ACE_OF_DIAMONDS(ACE, `♦`),
  KING_OF_DIAMONDS(KING, `♦`),
  QUEEN_OF_DIAMONDS(QUEEN, `♦`),
  JACK_OF_DIAMONDS(JACK, `♦`),
  `10_OF_DIAMONDS`(`10`, `♦`),
  `9_OF_DIAMONDS`(`9`, `♦`),
  `8_OF_DIAMONDS`(`8`, `♦`),
  `7_OF_DIAMONDS`(`7`, `♦`),
  `6_OF_DIAMONDS`(`6`, `♦`),
  `5_OF_DIAMONDS`(`5`, `♦`),
  `4_OF_DIAMONDS`(`4`, `♦`),
  `3_OF_DIAMONDS`(`3`, `♦`),
  `2_OF_DIAMONDS`(`2`, `♦`),

  ACE_OF_SPADES(ACE, `♠`),
  KING_OF_SPADES(KING, `♠`),
  QUEEN_OF_SPADES(QUEEN, `♠`),
  JACK_OF_SPADES(JACK, `♠`),
  `10_OF_SPADES`(`10`, `♠`),
  `9_OF_SPADES`(`9`, `♠`),
  `8_OF_SPADES`(`8`, `♠`),
  `7_OF_SPADES`(`7`, `♠`),
  `6_OF_SPADES`(`6`, `♠`),
  `5_OF_SPADES`(`5`, `♠`),
  `4_OF_SPADES`(`4`, `♠`),
  `3_OF_SPADES`(`3`, `♠`),
  `2_OF_SPADES`(`2`, `♠`),

  ACE_OF_CLUBS(ACE, `♣`),
  KING_OF_CLUBS(KING, `♣`),
  QUEEN_OF_CLUBS(QUEEN, `♣`),
  JACK_OF_CLUBS(JACK, `♣`),
  `10_OF_CLUBS`(`10`, `♣`),
  `9_OF_CLUBS`(`9`, `♣`),
  `8_OF_CLUBS`(`8`, `♣`),
  `7_OF_CLUBS`(`7`, `♣`),
  `6_OF_CLUBS`(`6`, `♣`),
  `5_OF_CLUBS`(`5`, `♣`),
  `4_OF_CLUBS`(`4`, `♣`),
  `3_OF_CLUBS`(`3`, `♣`),
  `2_OF_CLUBS`(`2`, `♣`);

  companion object {
    private val map: Map<Pair<Face, Suit>, Card> = values().associateByTo(HashMap()) { Pair(it.face, it.suit) }
    fun get(face: Face, suit: Suit): Card = map.getValue(Pair(face, suit))
  }
}

class Deck() {
  private val cards: MutableList<Card> = Card.values().mapTo(ArrayList(Card.values().size)) { it }

  fun contains(card: Card) = cards.contains(card)
  fun deal(): Card {
    if (cards.isEmpty()) throw RuntimeException("Cannot deal from an empty deck")
    return cards.removeAt(Random().nextInt(cards.size))
  }

  fun remove(cards: Pair<Card, Card>) {
    remove(cards.first)
    remove(cards.second)
  }

  fun remove(cards: Iterable<Card>) {
    for (card in cards) {
      remove(card)
    }
  }

  fun remove(card: Card) {
    if (!cards.remove(card)) throw java.lang.RuntimeException("The card $card isn't in the deck")
  }

  fun size() = cards.size
  fun put(card: Card) {
    if (contains(card)) {
      throw java.lang.RuntimeException("The deck already contains the card $card")
    }
    cards.add(card)
  }

  fun pairs(): Sequence<Pair<Card, Card>> {
    val myCards = ArrayList<Card>(cards)
    var x = 0
    var y = 1
    return generateSequence {
      if (x > cards.size - 2) {
        return@generateSequence null
      }
      Pair(myCards[x], myCards[y]).also {
        if (++y == cards.size) {
          x++
          y = x + 1
        }
      }
    }
  }

  fun triples(): Sequence<Triple<Card, Card, Card>> {
    val myCards = ArrayList<Card>(cards)
    var x = 0
    var y = 1
    var z = 2
    return generateSequence {
      if (x > cards.size - 3) {
        return@generateSequence null
      }
      Triple(myCards[x], myCards[y], myCards[z]).also {
        if (++z == cards.size) {
          if (++y == cards.size - 1) {
            x++
            y = x + 1
          }
          z = y + 1
        }
      }
    }
  }

  @ExperimentalUnsignedTypes
  fun nTuples(n: UInt) = nTuples(n, cards)
}
