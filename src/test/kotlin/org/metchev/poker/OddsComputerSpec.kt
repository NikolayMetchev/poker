package org.metchev.poker

import kotlinx.coroutines.runBlocking
import org.metchev.poker.Card.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class OddsComputerSpec : Spek({
  describe("Odds Computer") {
    oddsTest(
      "AA vs KK",
      ACE_OF_DIAMONDS, ACE_OF_CLUBS, KING_OF_DIAMONDS, KING_OF_CLUBS,
      1410336, 292660, 9308
    )
    oddsTest(
      "AA vs 72",
      ACE_OF_DIAMONDS, ACE_OF_CLUBS, `7_OF_DIAMONDS`, `2_OF_CLUBS`,
      1519445, 184642, 8217
    )
    oddsTest(
      "QJ vs J10",
      QUEEN_OF_DIAMONDS, JACK_OF_CLUBS, JACK_OF_SPADES, `10_OF_HEARTS`,
      1217747, 431345, 63212
    )
    oddsTest(
      "72 offsuit",
      `7_OF_SPADES`, `2_OF_HEARTS`,
      665146081, 1311884399, 120541920
    )
    oddsTest(
      "72 suited", `7_OF_SPADES`, `2_OF_SPADES`,
      743376257, 1240253676, 113942467
    )
    oddsTest(
      "AA",
      ACE_OF_SPADES, ACE_OF_HEARTS,
      1781508418, 304661670, 11402312
    )
  }
})

@ExperimentalUnsignedTypes
private fun Suite.oddsTest(
  description: String,
  player1Card1: Card,
  player1Card2: Card,
  player2Card1: Card,
  player2Card2: Card,
  player1Wins: Int,
  player2Wins: Int,
  splits: Int
) {
  it(description, timeout = 0L) {
    suitMappings.forEach {
      val mappedPlayer1Card1 = player1Card1.mapSuit(it)
      val mappedPlayer1Card2 = player1Card2.mapSuit(it)
      val mappedPlayer2Card1 = player2Card1.mapSuit(it)
      val mappedPlayer2Card2 = player2Card2.mapSuit(it)

      checkOdds(
        mappedPlayer1Card1,
        mappedPlayer1Card2,
        mappedPlayer2Card1,
        mappedPlayer2Card2,
        player1Wins,
        player2Wins,
        splits
      )
      checkOdds(
        mappedPlayer1Card2,
        mappedPlayer1Card1,
        mappedPlayer2Card1,
        mappedPlayer2Card2,
        player1Wins,
        player2Wins,
        splits
      )
      checkOdds(
        mappedPlayer1Card1,
        mappedPlayer1Card2,
        mappedPlayer2Card2,
        mappedPlayer2Card1,
        player1Wins,
        player2Wins,
        splits
      )
      checkOdds(
        mappedPlayer1Card2,
        mappedPlayer1Card1,
        mappedPlayer2Card2,
        mappedPlayer2Card1,
        player1Wins,
        player2Wins,
        splits
      )

      checkOdds(
        mappedPlayer2Card1,
        mappedPlayer2Card2,
        mappedPlayer1Card1,
        mappedPlayer1Card2,
        player2Wins,
        player1Wins,
        splits
      )
      checkOdds(
        mappedPlayer2Card1,
        mappedPlayer2Card2,
        mappedPlayer1Card2,
        mappedPlayer1Card1,
        player2Wins,
        player1Wins,
        splits
      )
      checkOdds(
        mappedPlayer2Card2,
        mappedPlayer2Card1,
        mappedPlayer1Card1,
        mappedPlayer1Card2,
        player2Wins,
        player1Wins,
        splits
      )
      checkOdds(
        mappedPlayer2Card2,
        mappedPlayer2Card1,
        mappedPlayer1Card2,
        mappedPlayer1Card1,
        player2Wins,
        player1Wins,
        splits
      )
    }
    ODDS_CACHE.save()
  }
}

@ExperimentalUnsignedTypes
private fun checkOdds(
  player1Card1: Card,
  player1Card2: Card,
  player2Card1: Card,
  player2Card2: Card,
  player1Wins: Int,
  player2Wins: Int,
  splits: Int
) {
  val odds =
    computeOdds(player1Card1, player1Card2, player2Card1, player2Card2)
  assertEquals(player1Wins, odds.player1Wins)
  assertEquals(player2Wins, odds.player2Wins)
  assertEquals(splits, odds.split)
}

@ExperimentalUnsignedTypes
private fun checkOdds(
  player1Card1: Card,
  player1Card2: Card,
  player1Wins: Int,
  player2Wins: Int,
  splits: Int
) = runBlocking {
  val odds = computeOdds(Hand(player1Card1, player1Card2))
  assertEquals(player1Wins, odds.player1Wins)
  assertEquals(player2Wins, odds.player2Wins)
  assertEquals(splits, odds.split)
}

@ExperimentalUnsignedTypes
private fun Suite.oddsTest(
  description: String,
  player1Card1: Card,
  player1Card2: Card,
  player1Wins: Int,
  player2Wins: Int,
  splits: Int
) {
  it(description, timeout = 0L) {
    suitMappings.forEach {
      val mappedPlayer1Card1 = player1Card1.mapSuit(it)
      val mappedPlayer1Card2 = player1Card2.mapSuit(it)

      checkOdds(mappedPlayer1Card1, mappedPlayer1Card2, player1Wins, player2Wins, splits)
      checkOdds(mappedPlayer1Card2, mappedPlayer1Card1, player1Wins, player2Wins, splits)
    }
    ODDS_CACHE.save()
  }
}
