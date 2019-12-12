package org.metchev.poker

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import java.util.*

@ExperimentalUnsignedTypes
fun computeOdds(player1Hand: Hand, player2Hand: Hand): Map<HandResult, Int> {
  val deck = Deck()
  deck.remove(player1Hand.cards)
  deck.remove(player2Hand.cards)
  val nTuples = deck.nTuples(5u)
  return nTuples
    .map { computeWinner(player1Hand, player2Hand, CommunityCards(it)) }
    .groupingBy { it.first }
    .eachCountTo(TreeMap())
}

@ExperimentalUnsignedTypes
fun main() {
  println(LocalDateTime.now())
  println(computeOdds(Hand(Card.ACE_OF_DIAMONDS, Card.ACE_OF_CLUBS), Hand(Card.ACE_OF_HEARTS, Card.ACE_OF_SPADES)))
  println(LocalDateTime.now())
}

suspend fun <A, B> Sequence<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
  println("in parallel")
  val result = mutableListOf<B>()
  val asyncs = map { async { f(it) } }.toList()
  asyncs.forEach { result.add(it.await()) }
  result
}
