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

val permutations: List<IntArray> = listOf(
  intArrayOf(3, 2, 1, 0),
  intArrayOf(3, 2, 0, 1),
  intArrayOf(3, 1, 2, 0),
  intArrayOf(3, 1, 0, 2),
  intArrayOf(3, 0, 2, 1),
  intArrayOf(3, 0, 1, 2),

  intArrayOf(2, 3, 1, 0),
  intArrayOf(2, 3, 0, 1),
  intArrayOf(2, 1, 3, 0),
  intArrayOf(2, 1, 0, 3),
  intArrayOf(2, 0, 3, 1),
  intArrayOf(2, 0, 1, 3),

  intArrayOf(1, 3, 2, 0),
  intArrayOf(1, 3, 0, 2),
  intArrayOf(1, 2, 3, 0),
  intArrayOf(1, 2, 0, 3),
  intArrayOf(1, 0, 3, 2),
  intArrayOf(1, 0, 2, 3),

  intArrayOf(0, 3, 2, 1),
  intArrayOf(0, 3, 1, 2),
  intArrayOf(0, 2, 3, 1),
  intArrayOf(0, 2, 1, 3),
  intArrayOf(0, 1, 3, 2),
  intArrayOf(0, 1, 2, 3)
)
val suitMappings: List<Map<Suit, Suit>> = permutations.map {
  it.withIndex().map { (index, value) -> Suit.values()[index] to Suit.values()[value] }.toMap()
}

fun Pair<Long, Long>.toBitSet() = Pair(first.fromBitSet().toSortedSet(), second.fromBitSet().toSortedSet())

