package org.metchev.poker

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import org.metchev.poker.HandResult.*
import java.io.File

@Serializable
data class OddsCache(private val map: MutableMap<Pair<Long, Long>, Map<HandResult, Int>> = HashMap()) {
  @ExperimentalUnsignedTypes
  @Synchronized
  fun get(player1Card1: Card, player1Card2: Card, player2Card1: Card, player2Card2: Card): Map<HandResult, Int>? {
    val key = getKey(player1Card1, player1Card2, player2Card1, player2Card2)
    val odds = map[key.first]
    return if (key.second && odds != null) {
      mapOf(
        PLAYER_2_WINS to odds[PLAYER_1_WINS]!!,
        PLAYER_1_WINS to odds[PLAYER_2_WINS]!!,
        SPLIT to odds[SPLIT]!!)
    } else {
      odds
    }
  }

  @Synchronized
  fun put(player1Card1: Card, player1Card2: Card, player2Card1: Card, player2Card2: Card, odds: Map<HandResult, Int>) {
    val key = getKey(player1Card1, player1Card2, player2Card1, player2Card2)
    if (key.second) {
      map[key.first] = mapOf(
        PLAYER_2_WINS to odds[PLAYER_1_WINS]!!,
        PLAYER_1_WINS to odds[PLAYER_2_WINS]!!,
        SPLIT to odds[SPLIT]!!)
    } else {
      map[key.first] = odds
    }
    if (map.size % 100 == 0) {
      save()
    }
  }

  fun save() {
    println("Saving ${map.size} entries")
    cacheFile.writeBytes(ProtoBuf.dump(serializer(), this))
    println("Done Saving ${map.size} entries")
  }

  private fun getKey(
    player1Card1: Card,
    player1Card2: Card,
    player2Card1: Card,
    player2Card2: Card
  ): Pair<Pair<Long, Long>, Boolean> {
    val player1Cards = arrayOf(player1Card1, player1Card2)
    val player2Cards = arrayOf(player2Card1, player2Card2)
    val player1Set = player1Cards.toBitSet()
    val player2Set = player2Cards.toBitSet()
    return if (player1Set < player2Set) {
      Pair(Pair(player1Set, player2Set), false)
    } else {
      Pair(Pair(player2Set, player1Set), true)
    }
  }
}

private val cacheFile = File("poker_odds.cache")

val ODDS_CACHE = if (cacheFile.exists()) { ProtoBuf.load(OddsCache.serializer(), cacheFile.readBytes()) } else {OddsCache()
}
