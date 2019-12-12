package org.metchev.poker

data class Hand(val cards: Pair<Card, Card>) {
  constructor(card1: Card, card2: Card) : this(Pair(card1, card2))
}
