package org.metchev.poker

import org.metchev.poker.Card.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

class ArrayMinusSpec : Spek({
  describe("Array minus") {
    val deck by memoized { arrayOf(ACE_OF_HEARTS, ACE_OF_SPADES, ACE_OF_CLUBS, ACE_OF_DIAMONDS) }
    context("init") {
      it("removing 2 cards") {
        assertEquals(arrayOf(ACE_OF_HEARTS, ACE_OF_DIAMONDS).toSortedSet(), (deck - arrayOf(ACE_OF_CLUBS, ACE_OF_SPADES)).toSortedSet())
      }
    }
  }
})

class DeckBitSetSpec: Spek ({
  describe("To And From BitSet") {
    bitSetTest("To and From 1 Card", `10_OF_DIAMONDS`)
    bitSetTest("To and From many Card", `10_OF_DIAMONDS`, ACE_OF_SPADES, JACK_OF_DIAMONDS, `6_OF_SPADES`)
    bitSetTest("To and From many Card different order", `10_OF_DIAMONDS`, JACK_OF_DIAMONDS, `6_OF_SPADES`, ACE_OF_SPADES)

  }
})

private fun Suite.bitSetTest(name: String, vararg cards: Card) {
  it(name) {
    val toBitSet = cards.toBitSet()
    val fromBitSet = toBitSet.fromBitSet()
    assertEquals(cards.toSortedSet(), fromBitSet.toSortedSet())
  }
}
