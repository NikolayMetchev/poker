package org.metchev.poker

import org.metchev.poker.Card.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertFails

@ExperimentalUnsignedTypes
class DeckSpec : Spek({
  describe("Deck") {
    val deck by memoized { Deck() }
    context("init") {
      context("Full deck") {
        val pairCount = (52 * 51) / 2 // sum 1 to 51
        val tripleCount = 22100 // 52 c 3 = 52!/(3!*(52-3)!)
        val quadCount = 270725 // 52 c 4 = 52!/(4!*(52-4)!)
        val fiveCount = 2598960 // 52 c 5 = 52!/(5!*(52-5)!)
        it("there should be $pairCount of pairs") {
          assertEquals(pairCount, deck.pairs().count())
          assertEquals(pairCount, deck.nTuples(2u).count())
        }
        it("there should be $tripleCount of triples") {
          assertEquals(tripleCount, deck.triples().count())
          assertEquals(tripleCount, deck.nTuples(3u).count())
        }
        it("there should be $quadCount of quads") {
          assertEquals(quadCount, deck.nTuples(4u).count())
        }
        it("there should be $fiveCount of fives") {
          assertEquals(fiveCount, deck.nTuples(5u).count())
        }
      }

      context("removing 1 card") {
        it("should leave 51") {
          deck.deal()
          assertEquals(51, deck.size())
        }
        val pairCount = (51 * 50) / 2 // sum 1 to 50
        it("Should leave $pairCount pairs") {
          deck.deal()
          assertEquals(pairCount, deck.pairs().count())
        }

      }
      it("should have 52 cards") {
        assertEquals(52, deck.size())
      }
      it("returning a card should throw") {
        assertFails { deck.put(`10_OF_CLUBS`) }
      }
      it("Dealing 53 times should throw") {
        for (i in 1..52) {
          deck.deal()
        }
        assertEquals(0, deck.size())
        assertFails { deck.deal() }
      }
    }
  }
})
