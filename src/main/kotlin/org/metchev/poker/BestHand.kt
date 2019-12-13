package org.metchev.poker

import org.metchev.poker.Face.ACE
import java.util.*

enum class HandResult {
  PLAYER_1_WINS,
  PLAYER_2_WINS,
  SPLIT
}

private fun nOfAKindChecker(n: Int): Array<out Card>.() -> Boolean = checker@{
  for (card in this) {
    if (this.count { it.face == card.face } == n) {
      return@checker true
    }
  }
  false
}

private fun nOfAKindFinder(n: Int): Array<out Card>.() -> Array<Card> = {
  val pair = nOfAKindFinder(this, n)
  pair.first + pair.second
}

private fun nOfAKindFinder(cards: Array<out Card>, n: Int): Pair<Array<Card>, Array<Card>> {
  val bestResult: Array<Card> = Array(n) { Card.`2_OF_HEARTS` }
  val bestOthers: Array<Card> = Array(cards.size - n) { Card.`2_OF_HEARTS` }
  var bestFace: Face? = null
  val currentResult = Array(n) { Card.`2_OF_HEARTS` }
  val currentOthers = Array(cards.size - n) { Card.`2_OF_HEARTS` }
  var index = 0
  while (index < cards.size - n + 1) {
    val card = cards[index]
    if (bestFace != null && bestFace > card.face) {
      index++
      continue
    }
    var i = -1
    var j = -1
    var innerIndex = 0
    while (innerIndex < cards.size) {
      val innerCard = cards[innerIndex]
      if (innerCard.face == card.face) {
        if (++i >= n) {
          break
        } else {
          currentResult[i] = innerCard
        }
      } else {
        if (++j >= cards.size - n) {
          break
        } else {
          currentOthers[j] = innerCard
        }
      }
      innerIndex++
    }
    if (i == n - 1 && j == cards.size - n - 1) {
      System.arraycopy(currentResult, 0, bestResult, 0, currentResult.size)
      System.arraycopy(currentOthers, 0, bestOthers, 0, currentOthers.size)
      bestFace = card.face
    }
    index++
  }

  val kickers = highestCards(bestOthers, 5 - n)

  return Pair(bestResult, kickers)
}

private fun highestAnyPair(cards: List<Card>): Array<Card> =
  highestCards(
    cards
      .asSequence()
      .groupByTo(TreeMap(compareByDescending { it })) { it.face }
      .filter { it.value.size >= 2 }
      .iterator()
      .next()
      .value
      .toTypedArray(),
    2)

private fun hasAnyPair(cards: List<Card>): Boolean =
  cards
    .asSequence()
    .groupByTo(EnumMap(Face::class.java)) { it.face }
    .filter { it.value.size >= 2 }
    .isNotEmpty()

private fun nOfAKindComparer(n: Int): (Array<Card>, Array<Card>) -> HandResult =
  { player1Cards: Array<Card>, player2Cards: Array<Card> ->
    val (nOfAKindPlayer1Cards, kickers1) = nOfAKindFinder(player1Cards, n)
    val (nOfAKindPlayer2Cards, kickers2) = nOfAKindFinder(player2Cards, n)
    highestFaceComparer(nOfAKindPlayer1Cards, nOfAKindPlayer2Cards) {
      highestFaceComparer(kickers1, kickers2)
    }
  }

private fun straightFaceComparer(player1Cards: Array<Card>, player2Cards: Array<Card>) =
  highestFaceComparer(
    player1Cards.filter { it.face != ACE }.toTypedArray(),
    player2Cards.filter { it.face != ACE }.toTypedArray()
  )

private fun highestFaceComparer(player1Cards: Array<Card>, player2Cards: Array<Card>) =
  highestFaceComparer(player1Cards, player2Cards) { HandResult.SPLIT }

private fun highestFaceComparer(
  player1Cards: Array<Card>,
  player2Cards: Array<Card>,
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

private fun highestCards(cards: Array<out Card>, n: Int = 5): Array<Card> {
  cards.sortByDescending { it.face }
  return Array(n) { cards[it] }
}

private fun findStraight(cards: Array<out Card>, next: (Card) -> Card?): Array<Card> {
  cards.sortByDescending { it.face }
  val result = Array(5) { cards[it] }
  for (card in cards) {
    var previousCard = card
    var i = 0
    var nextCard: Card? = next(previousCard)
    while (nextCard != null) {
      result[i] = previousCard
      if (i >= 3) {
        result[i + 1] = nextCard
        return result
      }
      previousCard = nextCard
      nextCard = next(previousCard)
      i++
    }
  }
  throw RuntimeException("This shouldn't happen")
}

private fun checkStraight(cards: Array<out Card>, next: (Card) -> Card?): Boolean {
  cards.forEach {
    var i = 0
    var previousCard = it
    var nextCard: Card? = next(previousCard)
    while (nextCard != null) {
      // Make sure we don't loop round e.g. 2, A, K, Q, J
      if (++i >= 4 && previousCard.face.ordinal <= Face.JACK.ordinal) {
        return true
      }
      previousCard = nextCard
      nextCard = next(previousCard)
    }
  }
  return false
}

fun twoPairFinder(cards: Array<out Card>): Pair<Array<Card>, Array<Card>> {
  val iterator = cards.asSequence()
    .groupByTo(TreeMap(compareByDescending { it })) { it.face }
    .filterValues { it.size == 2 }
    .iterator()
  val twoPairs = iterator.next().value.toTypedArray() + iterator.next().value.toTypedArray()
  val kickers = highestCards((cards.toList() - twoPairs).toTypedArray(), 1)
  return Pair(twoPairs, kickers)
}

enum class HandType(
  private val checker: Array<out Card>.() -> Boolean,
  private val getter: Array<out Card>.() -> Array<Card>,
  private val comparer: (Array<Card>, Array<Card>) -> HandResult
) {

  STRAIGHT_FLUSH(
    {
      checkStraight(this) {
        val previous = it.previousByFace()
        if (previous in this) previous else null
      }
    }
    ,
    {
      findStraight(this) {
        val previous = it.previousByFace()
        if (previous in this) previous else null
      }
    },
    ::straightFaceComparer
  ),

  FOUR_OF_A_KIND(nOfAKindChecker(4), nOfAKindFinder(4), nOfAKindComparer(4)),

  FULL_HOUSE({
    THREE_OF_A_KIND.check(this) && hasAnyPair(this.toList() - nOfAKindFinder(this, 3).first)
  }, {
    nOfAKindFinder(this, 3).first + highestAnyPair(this.toList() - nOfAKindFinder(this, 3).first)
  }, { player1Cards, player2Cards ->
    highestFaceComparer(nOfAKindFinder(player1Cards, 3).first, nOfAKindFinder(player2Cards, 3).first) {
      highestFaceComparer(
        nOfAKindFinder(player1Cards, 2).first,
        nOfAKindFinder(player2Cards, 2).first
      )
    }
  }),

  FLUSH(checker@{
    for (card in this) {
      if (this.count { it.suit == card.suit } >= 5) {
        return@checker true
      }
    }
    false
  }, getter@{
    this.sortByDescending { it.face }
    val result = Array(5) { this[it] }
    for (card in this) {
      var i = 0
      for (innerCard in this) {
        if (innerCard.suit == card.suit) {
          result[i++] = innerCard
          if (i == 5) {
            return@getter result
          }
        }
      }
    }
    throw RuntimeException("This shouldn't happen")
  }, ::highestFaceComparer),

  STRAIGHT(
    {
      checkStraight(this) next@{
        for (card in this) {
          if (card.face == it.face.previous()) {
            return@next card
          }
        }
        null
      }
    }, {
      findStraight(this) next@{
        for (card in this) {
          if (card.face == it.face.previous()) {
            return@next card
          }
        }
        null
      }
    },
    ::straightFaceComparer
  ),
  THREE_OF_A_KIND(nOfAKindChecker(3), nOfAKindFinder(3), nOfAKindComparer(3)),
  TWO_PAIR({
    asSequence()
      .groupByTo(EnumMap(Face::class.java)) { it.face }
      .values
      .asSequence()
      .map { it.size }
      .groupByTo(HashMap()) { it }[2]
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

  //fun check(cards: Array<Card>) = checker(cards)
  fun check(cards: Array<out Card>) = checker(cards)

  fun getCards(cards: Array<Card>): Array<Card> = getter(cards)
  fun compareCards(player1Cards: Array<Card>, player2Cards: Array<Card>): HandResult =
    comparer(player1Cards, player2Cards)
}

fun computeHandType(hand: Hand, communityCards: CommunityCards) =
  computeHandType(communityCards.cards + arrayOf(hand.cards.first, hand.cards.second))

fun computeHandType(cards: Array<Card>): Pair<HandType, Array<Card>> {
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
): Triple<HandResult, Pair<HandType, Array<Card>>, Pair<HandType, Array<Card>>> {
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
