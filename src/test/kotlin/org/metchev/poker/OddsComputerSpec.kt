package org.metchev.poker

import org.metchev.poker.Card.*
import org.metchev.poker.HandResult.*
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
    val computeOdds =
      computeOdds(player1Card1, player1Card2, player2Card1, player2Card2)
    assertEquals(player1Wins, computeOdds[PLAYER_1_WINS])
    assertEquals(player2Wins, computeOdds[PLAYER_2_WINS])
    assertEquals(splits, computeOdds[SPLIT])
    ODDS_CACHE.save()
  }
}
