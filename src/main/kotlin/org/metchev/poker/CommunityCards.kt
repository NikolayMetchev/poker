package org.metchev.poker

data class CommunityCards(val cards: Array<Card>) {
  constructor(
    card1: Card,
    card2: Card,
    card3: Card,
    card4: Card,
    card5: Card
  ) : this(arrayOf(card1, card2, card3, card4, card5))
}
