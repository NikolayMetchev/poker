package org.metchev.poker

import org.metchev.poker.Card.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class CacheSpec : Spek({
  describe("Cache") {
    val cache by memoized { OddsCache() }
    it("Ordering of cards not relevant", timeout = 0L) {
      val odds = Odds(1410336, 292660, 9308)
      val flippedOdds = odds.flipped()
      cache.put(
        ACE_OF_DIAMONDS, ACE_OF_CLUBS, KING_OF_DIAMONDS, KING_OF_CLUBS,
        odds
      )
      assertEquals(odds, cache.get(ACE_OF_DIAMONDS, ACE_OF_CLUBS, KING_OF_DIAMONDS, KING_OF_CLUBS))
      assertEquals(odds, cache.get(ACE_OF_DIAMONDS, ACE_OF_CLUBS, KING_OF_CLUBS, KING_OF_DIAMONDS))
      assertEquals(odds, cache.get(ACE_OF_CLUBS, ACE_OF_DIAMONDS, KING_OF_DIAMONDS, KING_OF_CLUBS))
      assertEquals(odds, cache.get(ACE_OF_CLUBS, ACE_OF_DIAMONDS, KING_OF_CLUBS, KING_OF_DIAMONDS))
      assertEquals(flippedOdds, cache.get(KING_OF_DIAMONDS, KING_OF_CLUBS, ACE_OF_DIAMONDS, ACE_OF_CLUBS))
      assertEquals(flippedOdds, cache.get(KING_OF_DIAMONDS, KING_OF_CLUBS, ACE_OF_CLUBS, ACE_OF_DIAMONDS))
      assertEquals(flippedOdds, cache.get(KING_OF_CLUBS, KING_OF_DIAMONDS, ACE_OF_DIAMONDS, ACE_OF_CLUBS))
      assertEquals(flippedOdds, cache.get(KING_OF_CLUBS, KING_OF_DIAMONDS, ACE_OF_CLUBS, ACE_OF_DIAMONDS))
    }
  }
})
