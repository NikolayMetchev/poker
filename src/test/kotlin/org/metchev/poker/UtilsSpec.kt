package org.metchev.poker

import org.metchev.poker.Card.*
import org.spekframework.spek2.Spek
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
