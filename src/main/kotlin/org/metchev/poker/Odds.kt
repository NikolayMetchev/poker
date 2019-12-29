package org.metchev.poker

import kotlinx.serialization.Serializable
import org.metchev.poker.HandResult.*

@Serializable
data class Odds(val player1Wins: Int = 0, val player2Wins: Int = 0, val split: Int = 0) {
  operator fun plus(handResult: HandResult) =
    when (handResult) {
      PLAYER_1_WINS -> Odds(player1Wins + 1, player2Wins, split)
      PLAYER_2_WINS -> Odds(player1Wins, player2Wins + 1, split)
      SPLIT -> Odds(player1Wins, player2Wins, split + 1)
    }

  operator fun plus(odds: Odds) =
    Odds(player1Wins + odds.player1Wins, player2Wins + odds.player2Wins, split + odds.split)

  fun flipped() = Odds(player2Wins, player1Wins, split)
}
