package org.metchev.poker

data class CommunityCards(val cards: List<Card>) {
  constructor(
    card1: Card,
    card2: Card,
    card3: Card,
    card4: Card,
    card5: Card
  ) : this(listOf(card1, card2, card3, card4, card5))
}
