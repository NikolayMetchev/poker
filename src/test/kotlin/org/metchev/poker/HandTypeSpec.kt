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

        Player1Wins,
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

        Player2Wins,
        STRAIGHT,
        FLUSH
      )
    }

    it("1 Kicker Plays in 4 of a kind", timeout = 0L) {
      assertWinner(
        `5_OF_SPADES`, `6_OF_CLUBS`,

        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        QUEEN_OF_DIAMONDS,
        QUEEN_OF_SPADES,
        QUEEN_OF_CLUBS,
        QUEEN_OF_HEARTS,
        `7_OF_DIAMONDS`,

        Player2Wins,
        FOUR_OF_A_KIND,
        FOUR_OF_A_KIND
      )
    }
    it("2 Kicker Plays in 3 of a kind", timeout= 0L) {
      assertWinner(
        ACE_OF_CLUBS, `6_OF_CLUBS`,

        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        QUEEN_OF_DIAMONDS,
        QUEEN_OF_SPADES,
        QUEEN_OF_CLUBS,
        JACK_OF_CLUBS,
        `7_OF_DIAMONDS`,

        Player2Wins,
        THREE_OF_A_KIND,
        THREE_OF_A_KIND
      )
    }
    it("Same kicker equals split pot", timeout = 0L) {
      assertWinner(
        ACE_OF_CLUBS, `6_OF_CLUBS`,

        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        QUEEN_OF_DIAMONDS,
        QUEEN_OF_SPADES,
        QUEEN_OF_CLUBS,
        QUEEN_OF_HEARTS,
        `7_OF_DIAMONDS`,

        Split,
        FOUR_OF_A_KIND,
        FOUR_OF_A_KIND
      )
    }

    it("Higher pair of the 2 wins", timeout = 0L) {
      assertWinner(
        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        ACE_OF_CLUBS, `6_OF_CLUBS`,

        QUEEN_OF_DIAMONDS,
        ACE_OF_SPADES,
        KING_OF_CLUBS,
        JACK_OF_HEARTS,
        `6_OF_DIAMONDS`,

        Player1Wins,
        TWO_PAIR,
        TWO_PAIR
      )
    }
    it("Higher kicker of the 2 pair", timeout = 0L) {
      assertWinner(
        KING_OF_DIAMONDS, ACE_OF_DIAMONDS,

        ACE_OF_CLUBS, `6_OF_CLUBS`,

        QUEEN_OF_DIAMONDS,
        ACE_OF_SPADES,
        JACK_OF_CLUBS,
        JACK_OF_HEARTS,
        `6_OF_DIAMONDS`,

        Player1Wins,
        TWO_PAIR,
        TWO_PAIR
      )
    }

    it("Full House vs Flush", timeout = 0L) {
      assertWinner(
        KING_OF_DIAMONDS, KING_OF_HEARTS,

        ACE_OF_CLUBS, `6_OF_CLUBS`,

        QUEEN_OF_CLUBS,
        `10_OF_CLUBS`,
        JACK_OF_CLUBS,
        JACK_OF_HEARTS,
        JACK_OF_DIAMONDS,

        Player1Wins,
        FULL_HOUSE,
        FLUSH
      )
    }

    it("Higher Straight Flush!", timeout = 0L) {
      assertWinner(
        `9_OF_DIAMONDS`, KING_OF_HEARTS,

        ACE_OF_DIAMONDS, `6_OF_CLUBS`,

        KING_OF_DIAMONDS,
        QUEEN_OF_DIAMONDS,
        JACK_OF_DIAMONDS,
        `10_OF_DIAMONDS`,
        `6_OF_DIAMONDS`,

        Player2Wins,
        STRAIGHT_FLUSH,
        STRAIGHT_FLUSH
      )
    }

    it("Higher Straight", timeout = 0L) {
      assertWinner(
        `9_OF_DIAMONDS`, KING_OF_HEARTS,

        ACE_OF_DIAMONDS, `6_OF_CLUBS`,

        KING_OF_DIAMONDS,
        QUEEN_OF_SPADES,
        JACK_OF_SPADES,
        `10_OF_SPADES`,
        `6_OF_DIAMONDS`,

        Player2Wins,
        STRAIGHT,
        STRAIGHT
      )
    }


    it("Higher Full House!", timeout = 0L) {
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

        Player2Wins,
        FULL_HOUSE,
        FULL_HOUSE
      )
    }

    it("One Pair vs Flush", timeout = 0L) {
      assertWinner(
        ACE_OF_DIAMONDS, ACE_OF_CLUBS,

        ACE_OF_HEARTS, ACE_OF_SPADES,

        KING_OF_HEARTS,
        QUEEN_OF_HEARTS,
        JACK_OF_HEARTS,
        `9_OF_HEARTS`,
        `8_OF_DIAMONDS`,

        Player2Wins,
        ONE_PAIR,
        FLUSH
      )
    }
    it("Pair split", timeout = 0L) {
      assertWinner(
        ACE_OF_DIAMONDS, ACE_OF_CLUBS,
        ACE_OF_HEARTS, ACE_OF_SPADES,

        KING_OF_HEARTS,
        QUEEN_OF_HEARTS,
        JACK_OF_HEARTS,
        `9_OF_DIAMONDS`,
        `8_OF_DIAMONDS`,
        Split,
        ONE_PAIR,
        ONE_PAIR
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
          STRAIGHT.check(
            ACE_OF_HEARTS,
            `2_OF_HEARTS`,
            `3_OF_HEARTS`,
            `4_OF_HEARTS`,
            `5_OF_HEARTS`
          )
        )
        assertTrue(
          STRAIGHT.check(
            ACE_OF_DIAMONDS,
            `2_OF_HEARTS`,
            `3_OF_HEARTS`,
            `4_OF_CLUBS`,
            `5_OF_HEARTS`
          )
        )
        assertTrue(
          STRAIGHT_FLUSH.check(
            ACE_OF_HEARTS,
            `2_OF_HEARTS`,
            `3_OF_HEARTS`,
            `4_OF_HEARTS`,
            `5_OF_HEARTS`
          )
        )

      }
      it("Find Straight Flush") {
        assertTrue(
          STRAIGHT_FLUSH.check(
            ACE_OF_HEARTS,
            KING_OF_HEARTS,
            QUEEN_OF_HEARTS,
            JACK_OF_HEARTS,
            `10_OF_HEARTS`
          )
        )
        assertFalse(
          STRAIGHT_FLUSH.check(
            ACE_OF_CLUBS,
            KING_OF_HEARTS,
            QUEEN_OF_HEARTS,
            JACK_OF_HEARTS,
            `10_OF_HEARTS`
          )
        )
        assertFalse(
          STRAIGHT_FLUSH.check(
            ACE_OF_HEARTS,
            KING_OF_HEARTS,
            QUEEN_OF_HEARTS,
            JACK_OF_HEARTS,
            `9_OF_HEARTS`
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
