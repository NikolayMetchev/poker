package org.metchev.poker

import org.metchev.poker.Face.ACE
import java.util.*

enum class HandResult {
  PLAYER_1_WINS,
  PLAYER_2_WINS,
  SPLIT
}

private fun nOfAKindChecker(n: Int): List<Card>.() -> Boolean = {
  asSequence()
    .groupingBy { it.face }
    .eachCount()
    .values
    .contains(n)
}

private fun nOfAKindFinder(n: Int): List<Card>.() -> List<Card> = {
  val pair = nOfAKindFinder(this, n)
  pair.first + pair.second
}

private fun nOfAKindFinder(cards: List<Card>, n: Int): Pair<List<Card>, List<Card>> {
  val nOfAKind = cards.asSequence()
    .groupByTo(TreeMap(compareByDescending { it })) { it.face }
    .filterValues { it.size == n }
    .iterator()
    .next()
    .value

  val kickers = highestCards(cards - nOfAKind, 5 - n)

  return Pair(nOfAKind, kickers)
}

private fun highestAnyPair(cards: List<Card>): List<Card> =
  highestCards(
    cards
      .asSequence()
      .groupByTo(TreeMap(compareByDescending { it })) { it.face }
      .filter { it.value.size >= 2 }
      .iterator()
      .next()
      .value,
    2)

private fun hasAnyPair(cards: List<Card>): Boolean =
  cards
    .asSequence()
    .groupBy { it.face }
    .filter { it.value.size >= 2 }
    .isNotEmpty()

private fun nOfAKindComparer(n: Int): (List<Card>, List<Card>) -> HandResult =
  { player1Cards: List<Card>, player2Cards: List<Card> ->
    val (nOfAKindPlayer1Cards, kickers1) = nOfAKindFinder(player1Cards, n)
    val (nOfAKindPlayer2Cards, kickers2) = nOfAKindFinder(player2Cards, n)
    highestFaceComparer(nOfAKindPlayer1Cards, nOfAKindPlayer2Cards) {
      highestFaceComparer(kickers1, kickers2)
    }
  }

private fun straightFaceComparer(player1Cards: List<Card>, player2Cards: List<Card>) =
  highestFaceComparer(player1Cards.filter { it.face != ACE }, player2Cards.filter { it.face != ACE })

private fun highestFaceComparer(player1Cards: List<Card>, player2Cards: List<Card>) =
  highestFaceComparer(player1Cards, player2Cards) { HandResult.SPLIT }

private fun highestFaceComparer(
  player1Cards: List<Card>,
  player2Cards: List<Card>,
  equalCase: () -> HandResult
): HandResult {
  val player1Iterator = player1Cards.sortedByDescending { it.face }.iterator()
  val player2Iterator = player2Cards.sortedByDescending { it.face }.iterator()
  while (player1Iterator.hasNext()) {
    val player1Card = player1Iterator.next()
    if (!player2Iterator.hasNext()) {
      return HandResult.PLAYER_2_WINS
    }
    val player2Card = player2Iterator.next()
    val compareTo = player1Card.face.compareTo(player2Card.face)
    when {
      compareTo > 0 -> return HandResult.PLAYER_1_WINS
      compareTo < 0 -> return HandResult.PLAYER_2_WINS
    }
  }
  if (player2Iterator.hasNext()) {
    return HandResult.PLAYER_1_WINS
  }
  return equalCase()
}

private fun highestCards(cards: List<Card>, n: Int = 5): List<Card> =
  cards.asSequence()
    .sortedByDescending { it.face }
    .take(n)
    .toList()

private fun findStraight(cards: List<Card>, next: (Card) -> Card, hasNext: (Card) -> Boolean): List<Card> {
  cards.sortedByDescending {it.face}.asSequence().forEach {
    var previousCard = it
    val result = mutableListOf<Card>()
    while (hasNext(previousCard)) {
      result.add(previousCard)
      if (result.size >= 4) {
        result.add(next(previousCard))
        return result
      }
      previousCard = next(previousCard)
    }
  }
  throw RuntimeException("This shouldn't happen")
}

private fun checkStraight(cards: List<Card>, next: (Card) -> Card, hasNext: (Card) -> Boolean): Boolean {
  cards.asSequence().forEach {
    var i = 0
    var previousCard = it
    while (hasNext(previousCard)) {
      // Make sure we don't loop round e.g. 2, A, K, Q, J
      if (++i >= 4 && previousCard.face.ordinal <= Face.JACK.ordinal) {
        return true
      }
      previousCard = next(previousCard)
    }
  }
  return false
}

fun twoPairFinder(cards: List<Card>): Pair<List<Card>, List<Card>> {
  val iterator = cards.asSequence()
    .groupByTo(TreeMap(compareByDescending { it })) { it.face }
    .filterValues { it.size == 2 }
    .iterator()
  val twoPairs = iterator.next().value + iterator.next().value
  val kickers = highestCards(cards - twoPairs, 1)
  return Pair(twoPairs, kickers)
}

enum class HandType(
  private val checker: List<Card>.() -> Boolean,
  private val getter: List<Card>.() -> List<Card>,
  private val comparer: (List<Card>, List<Card>) -> HandResult
) {

  STRAIGHT_FLUSH(
    {
      checkStraight(
        this,
        { Card.get(it.face.previous(), it.suit) },
        { Card.get(it.face.previous(), it.suit) in this })
    }
    ,
    {
      findStraight(
        this,
        { Card.get(it.face.previous(), it.suit) },
        { Card.get(it.face.previous(), it.suit) in this })
    },
    ::straightFaceComparer
  ),

  FOUR_OF_A_KIND(nOfAKindChecker(4), nOfAKindFinder(4), nOfAKindComparer(4)),

  FULL_HOUSE({
    THREE_OF_A_KIND.check(this) && hasAnyPair(this - nOfAKindFinder(this, 3).first)
  }, {
    nOfAKindFinder(this, 3).first + highestAnyPair(this - nOfAKindFinder(this, 3).first)
  }, { player1Cards, player2Cards ->
    highestFaceComparer(nOfAKindFinder(player1Cards, 3).first, nOfAKindFinder(player2Cards, 3).first) {
      highestFaceComparer(
        nOfAKindFinder(player1Cards, 2).first,
        nOfAKindFinder(player2Cards, 2).first
      )
    }
  }),

  FLUSH({
    asSequence()
      .groupBy { it.suit }
      .map { it.value }
      .map { it.size }
      .any { it >= 5 }
  }, {
    highestCards(asSequence()
      .groupBy { it.suit }
      .filter { it.value.size >= 5 }
      .iterator()
      .next()
      .value,
      5)
  }, ::highestFaceComparer),

  STRAIGHT(
    {
      val byFace = groupBy { it.face }
      checkStraight(
        this,
        { byFace.getValue(it.face.previous())[0] },
        { it.face.previous() in byFace.keys })
    }, {
      val byFace = groupBy { it.face }
      findStraight(
        this,
        { byFace.getValue(it.face.previous())[0] },
        { it.face.previous() in byFace.keys })
    },
    ::straightFaceComparer
  ),
  THREE_OF_A_KIND(nOfAKindChecker(3), nOfAKindFinder(3), nOfAKindComparer(3)),
  TWO_PAIR({
    asSequence()
      .groupBy { it.face }
      .values
      .asSequence()
      .map { it.size }
      .groupBy { it }[2]
      ?.size ?: 0 >= 2
  }, {
    val (twoPairCards, kickers) = twoPairFinder(this)
    twoPairCards + kickers
  }, { player1Cards, player2Cards ->
    val (twoPairPlayer1Cards, player1Kickers) = twoPairFinder(player1Cards)
    val (twoPairPlayer2Cards, player2Kickers) = twoPairFinder(player2Cards)
    highestFaceComparer(twoPairPlayer1Cards, twoPairPlayer2Cards) {
      highestFaceComparer(player1Kickers, player2Kickers)
    }
  }),
  ONE_PAIR(nOfAKindChecker(2), nOfAKindFinder(2), nOfAKindComparer(2)),
  HIGH_CARD({ true }, { highestCards(this, 5) }, ::highestFaceComparer);

  fun check(cards: List<Card>) = checker(cards)
  fun check(vararg cards: Card) = checker(cards.toList())
  fun getCards(cards: List<Card>): List<Card> = getter(cards)
  fun compareCards(player1Cards: List<Card>, player2Cards: List<Card>): HandResult =
    comparer(player1Cards, player2Cards)
}

fun computeHandType(hand: Hand, communityCards: CommunityCards) =
  computeHandType(communityCards.cards + listOf(hand.cards.first, hand.cards.second))

fun computeHandType(cards: List<Card>): Pair<HandType, List<Card>> {
  HandType.values().forEach {
    if (it.check(cards)) {
      return Pair(it, it.getCards(cards))
    }
  }
  throw RuntimeException("This shouldn't Happen. High Card should always return true. Check your code")
}

fun computeWinner(
  player1: Hand,
  player2: Hand,
  communityCards: CommunityCards
): Triple<HandResult, Pair<HandType, List<Card>>, Pair<HandType, List<Card>>> {
  val player1Pair = computeHandType(player1, communityCards)
  val (player1HandType, player1Cards) = player1Pair
  val player2Pair = computeHandType(player2, communityCards)
  val (player2HandType, player2Cards) = player2Pair
  val compareTo = player1HandType.compareTo(player2HandType)
  val resultType = when {
    compareTo > 0 -> HandResult.PLAYER_2_WINS
    compareTo < 0 -> HandResult.PLAYER_1_WINS
    compareTo == 0 -> player1HandType.compareCards(player1Cards, player2Cards)
    else -> throw RuntimeException("This shouldn't happen")
  }
  return Triple(resultType, player1Pair, player2Pair)
}
