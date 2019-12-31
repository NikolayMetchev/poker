package org.metchev.poker

@ExperimentalUnsignedTypes
fun main() {
  println(computeOdds(Card.`7_OF_HEARTS`, Card.`2_OF_DIAMONDS`, Card.QUEEN_OF_DIAMONDS, Card.JACK_OF_SPADES))
  println(computeOdds(Card.`7_OF_HEARTS`, Card.`2_OF_DIAMONDS`, Card.QUEEN_OF_SPADES, Card.JACK_OF_DIAMONDS))
}
