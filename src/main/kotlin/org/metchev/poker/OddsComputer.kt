package org.metchev.poker

import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.HashMap


@ExperimentalUnsignedTypes
fun computeOdds(player1Card1: Card, player1Card2: Card, player2Card1: Card, player2Card2: Card) =
  computeOdds(Hand(player1Card1, player1Card2), Hand(player2Card1, player2Card2))

@ExperimentalUnsignedTypes
fun computeOdds(player1Hand: Hand, player2Hand: Hand): Map<HandResult, Int> {
  val result = ODDS_CACHE.get(player1Hand.cards.first, player1Hand.cards.second, player2Hand.cards.first, player2Hand.cards.second)
  if (result != null) {
    return result
  }
  val deck = Deck()
  deck.remove(player1Hand.cards)
  deck.remove(player2Hand.cards)
  val nTuples = deck.nTuples(5u)
  val player1Array = Array(7) {
    when (it % 2) {
      0 -> player1Hand.cards.component1()
      1 -> player1Hand.cards.component2()
      else -> throw RuntimeException("This shouldn't happen")
    }
  }
  val player2Array = Array(7) {
    when (it % 2) {
      0 -> player2Hand.cards.component1()
      1 -> player2Hand.cards.component2()
      else -> throw RuntimeException("This shouldn't happen")
    }
  }

  val odds = nTuples
    .map { communityTuples ->
      var i = 0
      while (i < 5) {
        player1Array[i + 2] = communityTuples[i]
        player2Array[i + 2] = communityTuples[i]
        i++
      }
      player1Array[0] = player1Hand.cards.component1()
      player1Array[1] = player1Hand.cards.component2()
      player2Array[0] = player2Hand.cards.component1()
      player2Array[1] = player2Hand.cards.component2()
      player1Array.sortByDescending { it.face }
      player2Array.sortByDescending { it.face }
      computeWinnerResult(player1Array, player2Array)
    }
    .groupingBy { it }
    .eachCountTo(TreeMap())
  ODDS_CACHE.put(player1Hand.cards.first, player1Hand.cards.second, player2Hand.cards.first, player2Hand.cards.second, odds)
  return odds
}

@ExperimentalUnsignedTypes
suspend fun computeOdds(player1Hand: Hand): Map<HandResult, Int> {
  var deck: Array<Card> = Array(Card.values().size) {
    Card.values()[it]
  }
  deck -= arrayOf(player1Hand.cards.first, player1Hand.cards.second)
  val player2Tuples = deck.nTuplesList(2u)
  val combos = AtomicLong(0L)
  return player2Tuples
    .pmap {
      computeOdds(player1Hand, Hand(it[0], it[1])).also {
        println("${LocalDateTime.now()} ${Thread.currentThread().name} $player1Hand ${combos.addAndGet(1L)} of ${player2Tuples.size} combos")
      }
    }
    .reduce { acc, map ->
       HashMap(acc).also { merged ->
         map.forEach { merged.merge(it.key, it.value) { a, b -> a + b } }
       }
    }
}

//@ExperimentalUnsignedTypes
//fun main() {
//  println(LocalDateTime.now())
//  println(computeOdds(Card.QUEEN_OF_DIAMONDS, Card.JACK_OF_CLUBS, Card.JACK_OF_SPADES, Card.`10_OF_HEARTS`))
//  println(LocalDateTime.now())
//}


@ExperimentalUnsignedTypes
fun main() = runBlocking {
  val deck = Array(Card.values().size) {Card.values()[it]}
  val nTuples = deck.nTuples(2u)
  nTuples.forEach {
    println("${LocalDateTime.now()} ${it[0]}, ${it[1]}")
    println("${LocalDateTime.now()} ${computeOdds(Hand(it[0], it[1]))}")
    println("${LocalDateTime.now()} ${it[0]}, ${it[1]}")
  }
  ODDS_CACHE.save()
}

suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
  map { GlobalScope.async { f(it) } }.awaitAll()
}

suspend fun <A, B> Sequence<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
  println("in parallel")
  val result = mutableListOf<B>()
  val asyncs = map { GlobalScope.async { f(it) } }.toList()
  asyncs.forEach { result.add(it.await()) }
  result
}

suspend fun <A, B> Sequence<A>.flatPMap(transform: suspend (A) -> Sequence<B>): Sequence<B> {
  println("in flat pmap parallel")
  return flatMap { runBlocking { transform(it) } }
}
