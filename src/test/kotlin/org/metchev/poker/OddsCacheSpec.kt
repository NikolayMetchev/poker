package org.metchev.poker

import org.metchev.poker.Card.*
import org.metchev.poker.HandResult.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class OddsCacheSpec : Spek({
  describe("Cache") {
    val oddsCache by memoized { OddsCache() }
    it("Ordering of cards not relevant") {
      val odds = mapOf(PLAYER_1_WINS to 1410336, PLAYER_2_WINS to 292660, SPLIT to 9308)
      val flippedOdds = mapOf(PLAYER_2_WINS to 1410336, PLAYER_1_WINS to 292660, SPLIT to 9308)
      oddsCache.put(
        ACE_OF_DIAMONDS, ACE_OF_CLUBS, KING_OF_DIAMONDS, KING_OF_CLUBS,
        odds
      )
      assertEquals(odds, oddsCache.get(ACE_OF_DIAMONDS, ACE_OF_CLUBS, KING_OF_DIAMONDS, KING_OF_CLUBS))
      assertEquals(odds, oddsCache.get(ACE_OF_DIAMONDS, ACE_OF_CLUBS, KING_OF_CLUBS, KING_OF_DIAMONDS))
      assertEquals(odds, oddsCache.get(ACE_OF_CLUBS, ACE_OF_DIAMONDS, KING_OF_DIAMONDS, KING_OF_CLUBS))
      assertEquals(odds, oddsCache.get(ACE_OF_CLUBS, ACE_OF_DIAMONDS, KING_OF_CLUBS, KING_OF_DIAMONDS))
      assertEquals(flippedOdds, oddsCache.get(KING_OF_DIAMONDS, KING_OF_CLUBS, ACE_OF_DIAMONDS, ACE_OF_CLUBS))
      assertEquals(flippedOdds, oddsCache.get(KING_OF_DIAMONDS, KING_OF_CLUBS, ACE_OF_CLUBS, ACE_OF_DIAMONDS))
      assertEquals(flippedOdds, oddsCache.get(KING_OF_CLUBS, KING_OF_DIAMONDS, ACE_OF_DIAMONDS, ACE_OF_CLUBS))
      assertEquals(flippedOdds, oddsCache.get(KING_OF_CLUBS, KING_OF_DIAMONDS, ACE_OF_CLUBS, ACE_OF_DIAMONDS))
    }
    it("Suits of cards not relevant") {
      val odds = mapOf(PLAYER_1_WINS to 1410336, PLAYER_2_WINS to 292660, SPLIT to 9308)
      val flippedOdds = mapOf(PLAYER_2_WINS to 1410336, PLAYER_1_WINS to 292660, SPLIT to 9308)
      oddsCache.put(
        ACE_OF_HEARTS, ACE_OF_SPADES, KING_OF_HEARTS, KING_OF_SPADES,
        odds
      )
      assertEquals(odds, oddsCache.get(ACE_OF_DIAMONDS, ACE_OF_CLUBS, KING_OF_DIAMONDS, KING_OF_CLUBS))
      assertEquals(odds, oddsCache.get(ACE_OF_DIAMONDS, ACE_OF_CLUBS, KING_OF_CLUBS, KING_OF_DIAMONDS))
      assertEquals(odds, oddsCache.get(ACE_OF_CLUBS, ACE_OF_DIAMONDS, KING_OF_DIAMONDS, KING_OF_CLUBS))
      assertEquals(odds, oddsCache.get(ACE_OF_CLUBS, ACE_OF_DIAMONDS, KING_OF_CLUBS, KING_OF_DIAMONDS))
      assertEquals(flippedOdds, oddsCache.get(KING_OF_DIAMONDS, KING_OF_CLUBS, ACE_OF_DIAMONDS, ACE_OF_CLUBS))
      assertEquals(flippedOdds, oddsCache.get(KING_OF_DIAMONDS, KING_OF_CLUBS, ACE_OF_CLUBS, ACE_OF_DIAMONDS))
      assertEquals(flippedOdds, oddsCache.get(KING_OF_CLUBS, KING_OF_DIAMONDS, ACE_OF_DIAMONDS, ACE_OF_CLUBS))
      assertEquals(flippedOdds, oddsCache.get(KING_OF_CLUBS, KING_OF_DIAMONDS, ACE_OF_CLUBS, ACE_OF_DIAMONDS))
    }
  }
  describe("Cache Key") {
    suitTest("AA vs AA", ACE_OF_HEARTS, ACE_OF_SPADES, ACE_OF_DIAMONDS, ACE_OF_CLUBS)
    suitTest("AA vs KK", ACE_OF_HEARTS, ACE_OF_SPADES, KING_OF_DIAMONDS, KING_OF_CLUBS)
    suitTest("AA vs KK 1 overlapping suit", ACE_OF_HEARTS, ACE_OF_SPADES, KING_OF_HEARTS, KING_OF_CLUBS)
    suitTest("AA vs KK 2 overlapping suits", ACE_OF_HEARTS, ACE_OF_SPADES, KING_OF_HEARTS, KING_OF_SPADES)
    suitTest("AK vs KJ no overlapping suits", ACE_OF_HEARTS, KING_OF_SPADES, KING_OF_CLUBS, JACK_OF_CLUBS)
    suitTest("AK vs KJ 1 overlapping suits", ACE_OF_HEARTS, KING_OF_SPADES, KING_OF_HEARTS, JACK_OF_CLUBS)
    suitTest("AK vs KJ 2 overlapping suits", ACE_OF_HEARTS, KING_OF_SPADES, KING_OF_HEARTS, JACK_OF_SPADES)
  }
})

private fun Suite.suitTest(
  description: String,
  player1Card1: Card,
  player1Card2: Card,
  player2Card1: Card,
  player2Card2: Card
) {
  it(description) {
    val (key, _) = getKey(player1Card1, player1Card2, player2Card1, player2Card2)
    suitMappings.forEach {
      val mappedPlayer1Card1 = player1Card1.mapSuit(it)
      val mappedPlayer1Card2 = player1Card2.mapSuit(it)
      val mappedPlayer2Card1 = player2Card1.mapSuit(it)
      val mappedPlayer2Card2 = player2Card2.mapSuit(it)
      assertEquals(
        key, getKey(
          mappedPlayer1Card1,
          mappedPlayer1Card2,
          mappedPlayer2Card1,
          mappedPlayer2Card2
        ).first
      )
      assertEquals(
        key, getKey(
          mappedPlayer2Card1,
          mappedPlayer2Card2,
          mappedPlayer1Card1,
          mappedPlayer1Card2
        ).first
      )
      assertEquals(
        key, getKey(
          mappedPlayer1Card2,
          mappedPlayer1Card1,
          mappedPlayer2Card1,
          mappedPlayer2Card2
        ).first
      )
      assertEquals(
        key, getKey(
          mappedPlayer1Card1,
          mappedPlayer1Card2,
          mappedPlayer2Card2,
          mappedPlayer2Card1
        ).first
      )
    }
  }
}
