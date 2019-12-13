package org.metchev.poker

import org.metchev.poker.Card.*
import org.metchev.poker.HandResult.*
import org.metchev.poker.HandType.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalUnsignedTypes
class HandTypeSpec : Spek({
  describe("Compute Winner") {
    it("Highest High Card Should Win") {
      assertWinner(
        ACE_OF_DIAMONDS, `6_OF_CLUBS`,

        KING_OF_DIAMONDS, `7_OF_CLUBS`,

        QUEEN_OF_HEARTS,
        `10_OF_DIAMONDS`,
        `3_OF_CLUBS`,
        `4_OF_HEARTS`,
        `9_OF_SPADES`,

        PLAYER_1_WINS,
        HIGH_CARD,
        HIGH_CARD
      )
    }

    it("Flush beats a straight every time!") {
      assertWinner(
        `5_OF_SPADES`, `6_OF_CLUBS`,

        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        QUEEN_OF_DIAMONDS,
        `10_OF_DIAMONDS`,
        `3_OF_CLUBS`,
        `4_OF_HEARTS`,
        `7_OF_DIAMONDS`,

        PLAYER_2_WINS,
        STRAIGHT,
        FLUSH
      )
    }

    it("1 Kicker Plays in 4 of a kind", timeout= 0L) {
      assertWinner(
        `5_OF_SPADES`, `6_OF_CLUBS`,

        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        QUEEN_OF_DIAMONDS,
        QUEEN_OF_SPADES,
        QUEEN_OF_CLUBS,
        QUEEN_OF_HEARTS,
        `7_OF_DIAMONDS`,

        PLAYER_2_WINS,
        FOUR_OF_A_KIND,
        FOUR_OF_A_KIND
      )
    }
    it("2 Kickers Play in 3 of a kind") {
      assertWinner(
        ACE_OF_CLUBS, `6_OF_CLUBS`,

        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        QUEEN_OF_DIAMONDS,
        QUEEN_OF_SPADES,
        QUEEN_OF_CLUBS,
        JACK_OF_CLUBS,
        `7_OF_DIAMONDS`,

        PLAYER_2_WINS,
        THREE_OF_A_KIND,
        THREE_OF_A_KIND
      )
    }

    it("2 Kickers Play in one pair") {
      assertWinner(
        ACE_OF_CLUBS, `10_OF_CLUBS`,

        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        `2_OF_DIAMONDS`,
        QUEEN_OF_SPADES,
        QUEEN_OF_CLUBS,
        JACK_OF_CLUBS,
        `7_OF_DIAMONDS`,

        PLAYER_2_WINS,
        ONE_PAIR,
        ONE_PAIR
      )
    }

    it("Higher one pair wins") {
      assertWinner(
        QUEEN_OF_HEARTS, `10_OF_CLUBS`,

        KING_OF_DIAMONDS, `7_OF_HEARTS`,

        `2_OF_DIAMONDS`,
        ACE_OF_SPADES,
        QUEEN_OF_CLUBS,
        JACK_OF_CLUBS,
        `7_OF_DIAMONDS`,

        PLAYER_1_WINS,
        ONE_PAIR,
        ONE_PAIR
      )
    }

    it("Same kicker equals split pot") {
      assertWinner(
        ACE_OF_CLUBS, `6_OF_CLUBS`,

        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        QUEEN_OF_DIAMONDS,
        QUEEN_OF_SPADES,
        QUEEN_OF_CLUBS,
        QUEEN_OF_HEARTS,
        `7_OF_DIAMONDS`,

        SPLIT,
        FOUR_OF_A_KIND,
        FOUR_OF_A_KIND
      )
    }

    it("Higher pair of the 2 wins") {
      assertWinner(
        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        ACE_OF_CLUBS, `6_OF_CLUBS`,

        QUEEN_OF_DIAMONDS,
        ACE_OF_SPADES,
        KING_OF_CLUBS,
        JACK_OF_HEARTS,
        `6_OF_DIAMONDS`,

        PLAYER_1_WINS,
        TWO_PAIR,
        TWO_PAIR
      )
    }
    it("Higher 3 of a kind wins") {
      assertWinner(
        KING_OF_DIAMONDS, KING_OF_HEARTS,

        ACE_OF_CLUBS, ACE_OF_DIAMONDS,

        QUEEN_OF_DIAMONDS,
        ACE_OF_SPADES,
        KING_OF_CLUBS,
        JACK_OF_HEARTS,
        `6_OF_DIAMONDS`,

        PLAYER_2_WINS,
        THREE_OF_A_KIND,
        THREE_OF_A_KIND
      )
    }
    it("Higher kicker of the 2 pair") {
      assertWinner(
        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        ACE_OF_CLUBS, `6_OF_CLUBS`,

        QUEEN_OF_DIAMONDS,
        ACE_OF_SPADES,
        JACK_OF_CLUBS,
        JACK_OF_HEARTS,
        `6_OF_DIAMONDS`,

        PLAYER_1_WINS,
        TWO_PAIR,
        TWO_PAIR
      )
    }

    it("Same kicker of the 2 pair") {
      assertWinner(
        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        ACE_OF_CLUBS, KING_OF_HEARTS,

        QUEEN_OF_DIAMONDS,
        ACE_OF_SPADES,
        JACK_OF_CLUBS,
        JACK_OF_HEARTS,
        `6_OF_DIAMONDS`,

        SPLIT,
        TWO_PAIR,
        TWO_PAIR
      )
    }

    it("Longer Straight") {
      assertWinner(
        `6_OF_HEARTS`, `5_OF_HEARTS`,

        QUEEN_OF_CLUBS, KING_OF_HEARTS,

        `9_OF_DIAMONDS`,
        `8_OF_HEARTS`,
        `7_OF_CLUBS`,
        JACK_OF_HEARTS,
        `10_OF_DIAMONDS`,

        PLAYER_2_WINS,
        STRAIGHT,
        STRAIGHT
      )
    }



    it("2 pair split") {
      assertWinner(
        `5_OF_CLUBS`, ACE_OF_DIAMONDS,

        ACE_OF_CLUBS, `6_OF_CLUBS`,

        QUEEN_OF_DIAMONDS,
        ACE_OF_SPADES,
        JACK_OF_CLUBS,
        JACK_OF_HEARTS,
        `6_OF_DIAMONDS`,

        SPLIT,
        TWO_PAIR,
        TWO_PAIR
      )
    }

    it("Full House vs Flush") {
      assertWinner(
        KING_OF_DIAMONDS, KING_OF_HEARTS,

        ACE_OF_CLUBS, `6_OF_CLUBS`,

        QUEEN_OF_CLUBS,
        `10_OF_CLUBS`,
        JACK_OF_CLUBS,
        JACK_OF_HEARTS,
        JACK_OF_DIAMONDS,

        PLAYER_1_WINS,
        FULL_HOUSE,
        FLUSH
      )
    }

    it("Higher Straight Flush!") {
      assertWinner(
        `9_OF_DIAMONDS`, KING_OF_HEARTS,

        ACE_OF_DIAMONDS, `6_OF_CLUBS`,

        KING_OF_DIAMONDS,
        QUEEN_OF_DIAMONDS,
        JACK_OF_DIAMONDS,
        `10_OF_DIAMONDS`,
        `6_OF_DIAMONDS`,

        PLAYER_2_WINS,
        STRAIGHT_FLUSH,
        STRAIGHT_FLUSH
      )
    }
    it("Higher Straight Flush!") {
      assertWinner(
        ACE_OF_DIAMONDS, `6_OF_CLUBS`,
        `9_OF_DIAMONDS`, KING_OF_HEARTS,


        KING_OF_DIAMONDS,
        QUEEN_OF_DIAMONDS,
        JACK_OF_DIAMONDS,
        `10_OF_DIAMONDS`,
        `6_OF_DIAMONDS`,

        PLAYER_1_WINS,
        STRAIGHT_FLUSH,
        STRAIGHT_FLUSH
      )
    }

    it("Higher Straight") {
      assertWinner(
        `9_OF_DIAMONDS`, KING_OF_HEARTS,

        ACE_OF_DIAMONDS, `6_OF_CLUBS`,

        KING_OF_DIAMONDS,
        QUEEN_OF_SPADES,
        JACK_OF_SPADES,
        `10_OF_SPADES`,
        `6_OF_DIAMONDS`,

        PLAYER_2_WINS,
        STRAIGHT,
        STRAIGHT
      )
    }

    it("Wheel barrow is the lowest straight Higher Straight") {
      assertWinner(
        `6_OF_DIAMONDS`, KING_OF_HEARTS,

        ACE_OF_DIAMONDS, `7_OF_CLUBS`,

        KING_OF_DIAMONDS,
        `5_OF_SPADES`,
        `4_OF_SPADES`,
        `3_OF_SPADES`,
        `2_OF_DIAMONDS`,

        PLAYER_1_WINS,
        STRAIGHT,
        STRAIGHT
      )
    }

    it("Wheel barrow is the lowest straight Higher Straight") {
      assertWinner(
        ACE_OF_DIAMONDS, `7_OF_CLUBS`,
        `6_OF_DIAMONDS`, KING_OF_HEARTS,


        KING_OF_DIAMONDS,
        `5_OF_SPADES`,
        `4_OF_SPADES`,
        `3_OF_SPADES`,
        `2_OF_DIAMONDS`,

        PLAYER_2_WINS,
        STRAIGHT,
        STRAIGHT
      )
    }

    it("Straight Split") {
      assertWinner(
        `9_OF_DIAMONDS`, `10_OF_HEARTS`,

        `10_OF_DIAMONDS`, `6_OF_CLUBS`,

        KING_OF_DIAMONDS,
        QUEEN_OF_SPADES,
        JACK_OF_SPADES,
        `9_OF_SPADES`,
        `6_OF_DIAMONDS`,

        SPLIT,
        STRAIGHT,
        STRAIGHT
      )
    }

    it("Higher Full House!") {
      assertWinner(
        KING_OF_DIAMONDS,
        KING_OF_HEARTS,

        ACE_OF_CLUBS,
        `6_OF_CLUBS`,

        ACE_OF_DIAMONDS,
        `10_OF_CLUBS`,
        JACK_OF_CLUBS,
        JACK_OF_HEARTS,
        JACK_OF_DIAMONDS,

        PLAYER_2_WINS,
        FULL_HOUSE,
        FULL_HOUSE
      )
    }

    it("One Pair vs Flush") {
      assertWinner(
        ACE_OF_DIAMONDS, ACE_OF_CLUBS,

        ACE_OF_HEARTS, ACE_OF_SPADES,

        KING_OF_HEARTS,
        QUEEN_OF_HEARTS,
        JACK_OF_HEARTS,
        `9_OF_HEARTS`,
        `8_OF_DIAMONDS`,

        PLAYER_2_WINS,
        ONE_PAIR,
        FLUSH
      )
    }
    it("Pair split") {
      assertWinner(
        ACE_OF_DIAMONDS, ACE_OF_CLUBS,
        ACE_OF_HEARTS, ACE_OF_SPADES,

        KING_OF_HEARTS,
        QUEEN_OF_HEARTS,
        JACK_OF_HEARTS,
        `9_OF_DIAMONDS`,
        `8_OF_DIAMONDS`,
        SPLIT,
        ONE_PAIR,
        ONE_PAIR
      )
    }

    it("Full house vs 3 of a kind") {
      assertWinner(
        ACE_OF_DIAMONDS, ACE_OF_CLUBS,
        `7_OF_DIAMONDS`, `2_OF_CLUBS`,

        ACE_OF_HEARTS,
        KING_OF_HEARTS,
        QUEEN_OF_HEARTS,
        KING_OF_DIAMONDS,
        KING_OF_SPADES,

        PLAYER_1_WINS,
        FULL_HOUSE,
        THREE_OF_A_KIND
      )
    }
    it("Full house vs Full house") {
      assertWinner(
        JACK_OF_DIAMONDS, JACK_OF_CLUBS,
        QUEEN_OF_DIAMONDS, QUEEN_OF_CLUBS,

        JACK_OF_HEARTS,
        KING_OF_HEARTS,
        QUEEN_OF_HEARTS,
        KING_OF_DIAMONDS,
        KING_OF_SPADES,

        PLAYER_2_WINS,
        FULL_HOUSE,
        FULL_HOUSE
      )
    }
    it("Full house vs Full house") {
      assertWinner(
        `10_OF_HEARTS`, `9_OF_SPADES`,
        `9_OF_DIAMONDS`, `9_OF_CLUBS`,

        ACE_OF_HEARTS,
        JACK_OF_DIAMONDS,
        `9_OF_HEARTS`,
        `10_OF_DIAMONDS`,
        `10_OF_SPADES`,

        PLAYER_1_WINS,
        FULL_HOUSE,
        FULL_HOUSE
      )
    }

  }
  describe("Best HandType") {
    it("Should be High Card") {
      val (handType, _) = computeHandType(
        Hand(ACE_OF_CLUBS, `10_OF_CLUBS`),
        CommunityCards(
          `7_OF_SPADES`,
          `6_OF_DIAMONDS`,
          `2_OF_HEARTS`,
          JACK_OF_CLUBS,
          `8_OF_HEARTS`
        )
      )
      assertEquals(HIGH_CARD, handType)
    }
  }
  describe("HandType") {
    context("init") {
      it("Should be a Wheel straight") {
        assertTrue(
          STRAIGHT.check(arrayOf(
            ACE_OF_HEARTS,
            `2_OF_HEARTS`,
            `3_OF_HEARTS`,
            `4_OF_HEARTS`,
            `5_OF_HEARTS`)
          )
        )
        assertTrue(
          STRAIGHT.check(arrayOf(
            ACE_OF_DIAMONDS,
            `2_OF_HEARTS`,
            `3_OF_HEARTS`,
            `4_OF_CLUBS`,
            `5_OF_HEARTS`)
          )
        )
        assertTrue(
          STRAIGHT_FLUSH.check(arrayOf(
            ACE_OF_HEARTS,
            `2_OF_HEARTS`,
            `3_OF_HEARTS`,
            `4_OF_HEARTS`,
            `5_OF_HEARTS`)
          )
        )

      }

      it("Not Straights") {
        assertFalse(
          STRAIGHT.check(arrayOf(
            ACE_OF_HEARTS,
            KING_OF_HEARTS,
            QUEEN_OF_HEARTS,
            JACK_OF_HEARTS,
            `2_OF_DIAMONDS`)
          )
        )
        assertFalse(
          STRAIGHT.check(arrayOf(
            ACE_OF_HEARTS,
            KING_OF_HEARTS,
            QUEEN_OF_HEARTS,
            `3_OF_HEARTS`,
            `2_OF_DIAMONDS`)
          )
        )
        assertFalse(
          STRAIGHT.check(arrayOf(
            ACE_OF_HEARTS,
            KING_OF_HEARTS,
            `4_OF_HEARTS`,
            `3_OF_HEARTS`,
            `2_OF_DIAMONDS`)
          )
        )
        assertFalse(
          STRAIGHT_FLUSH.check(arrayOf(
            ACE_OF_HEARTS,
            KING_OF_HEARTS,
            `4_OF_HEARTS`,
            `3_OF_HEARTS`,
            `2_OF_HEARTS`)
          )
        )
        assertFalse(
          STRAIGHT_FLUSH.check(arrayOf(
            ACE_OF_HEARTS,
            KING_OF_HEARTS,
            QUEEN_OF_HEARTS,
            JACK_OF_HEARTS,
            `2_OF_HEARTS`)
          )
        )
        assertFalse(
          STRAIGHT_FLUSH.check(arrayOf(
            ACE_OF_HEARTS,
            KING_OF_HEARTS,
            QUEEN_OF_HEARTS,
            `3_OF_HEARTS`,
            `2_OF_HEARTS`)
          )
        )
      }

      it("Straight Finder") {
        assertEquals(
          listOf(`10_OF_HEARTS`, `8_OF_HEARTS`, `9_OF_HEARTS`, JACK_OF_CLUBS, QUEEN_OF_CLUBS).sortedBy { it.face },
          STRAIGHT.getCards(
            arrayOf(
              `7_OF_HEARTS`,
              `8_OF_HEARTS`,
              `9_OF_HEARTS`,
              JACK_OF_CLUBS,
              QUEEN_OF_CLUBS,
              `6_OF_SPADES`,
              `10_OF_HEARTS`
            )
          ).sortedBy { it.face }
        )
      }
      it("Find Straight Flush") {
        assertTrue(
          STRAIGHT_FLUSH.check(arrayOf(
            ACE_OF_HEARTS,
            KING_OF_HEARTS,
            QUEEN_OF_HEARTS,
            JACK_OF_HEARTS,
            `10_OF_HEARTS`)
          )
        )
        assertFalse(
          STRAIGHT_FLUSH.check(arrayOf(
            ACE_OF_CLUBS,
            KING_OF_HEARTS,
            QUEEN_OF_HEARTS,
            JACK_OF_HEARTS,
            `10_OF_HEARTS`)
          )
        )
        assertFalse(
          STRAIGHT_FLUSH.check(arrayOf(
            ACE_OF_HEARTS,
            KING_OF_HEARTS,
            QUEEN_OF_HEARTS,
            JACK_OF_HEARTS,
            `9_OF_HEARTS`)
          )
        )
      }
    }
  }
})

private fun assertWinner(
  player1Card1: Card,
  player1Card2: Card,
  player2Card1: Card,
  player2Card2: Card,
  communityCard1: Card,
  communityCard2: Card,
  communityCard3: Card,
  communityCard4: Card,
  communityCard5: Card,
  handResult: HandResult,
  player1Result: HandType,
  player2Result: HandType
) {
  val result = computeWinner(
    Hand(player1Card1, player1Card2),
    Hand(player2Card1, player2Card2),
    CommunityCards(
      communityCard1,
      communityCard2,
      communityCard3,
      communityCard4,
      communityCard5
    )
  )
  assertEquals(handResult, result.first)
  assertEquals(player1Result, result.second.first)
  assertEquals(player2Result, result.third.first)
}
