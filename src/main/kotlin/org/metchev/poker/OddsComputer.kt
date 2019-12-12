package org.metchev.poker

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.metchev.poker.Card.*
import java.time.LocalDateTime
import java.util.*

@ExperimentalUnsignedTypes
fun computeOdds(player1Card1: Card, player1Card2: Card, player2Card1: Card, player2Card2: Card) =
  computeOdds(Hand(player1Card1, player1Card2), Hand(player2Card1, player2Card2))

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
suspend fun computeOddsAsync(player1Hand: Hand, player2Hand: Hand): Map<HandResult, Int> {
  val deck = Deck()
  deck.remove(player1Hand.cards)
  deck.remove(player2Hand.cards)
  val nTuples = deck.nTuples(5u)
  return nTuples
    .pmap { computeWinner(player1Hand, player2Hand, CommunityCards(it)) }
    .groupingBy { it.first }
    .eachCountTo(TreeMap())
}

@ExperimentalUnsignedTypes
fun main() {
  println(LocalDateTime.now())
  println(computeOdds(QUEEN_OF_DIAMONDS, JACK_OF_CLUBS, JACK_OF_SPADES, `10_OF_HEARTS`))
  println(LocalDateTime.now())
}

//@ExperimentalUnsignedTypes
//fun main() = runBlocking {
//  println(LocalDateTime.now())
//  println(computeOddsAsync(Hand(QUEEN_OF_DIAMONDS, JACK_OF_CLUBS), Hand(JACK_OF_SPADES, `10_OF_HEARTS`)))
//  println(LocalDateTime.now())
//}


suspend fun <A, B> Sequence<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
  println("in parallel")
  val result = mutableListOf<B>()
  val asyncs = map { async { f(it) } }.toList()
  asyncs.forEach { result.add(it.await()) }
  result
}
