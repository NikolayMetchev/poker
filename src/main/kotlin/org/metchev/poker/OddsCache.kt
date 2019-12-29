package org.metchev.poker

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import org.metchev.poker.OverlappingSuitState.*
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

@Serializable
data class OddsCache(val map: MutableMap<Key, Odds> = HashMap()) {
  fun size() = map.size

  @ExperimentalUnsignedTypes
  @Synchronized
  fun get(player1Card1: Card, player1Card2: Card, player2Card1: Card, player2Card2: Card): Odds? {
    val (key, flipped) = getKey(player1Card1, player1Card2, player2Card1, player2Card2)
    val odds = map[key]
    val result = if (flipped && odds != null) {
      odds.flipped()
    } else {
      odds
    }
    return result.also {
      if (it != null) {
        println("cache hit")
      }
    }
  }

  @Synchronized
  fun put(player1Card1: Card, player1Card2: Card, player2Card1: Card, player2Card2: Card, odds: Odds) {
    val (key, flipped) = getKey(player1Card1, player1Card2, player2Card1, player2Card2)
    map[key] = if (flipped) {
      odds.flipped()
    } else {
      odds
    }
    if (map.size % 100 == 0) {
      save()
    }
  }

  @Synchronized
  fun save() {
    println("Saving ${map.size} entries")
    cacheFile.writeBytes(ProtoBuf.dump(serializer(), this))
    println("Done Saving ${map.size} entries")
  }
}

fun getKey(
  player1Card1: Card,
  player1Card2: Card,
  player2Card1: Card,
  player2Card2: Card
): Pair<Key, Boolean> {
  val player1Suits = arrayOf(player1Card1.suit, player1Card2.suit)
  val player2Suits = arrayOf(player2Card1.suit, player2Card2.suit)
  val player1Faces = arrayOf(player1Card1.face, player1Card2.face)
  val player2Faces = arrayOf(player2Card1.face, player2Card2.face)
  val player1HigherFace = maxOf(player1Card1.face, player1Card2.face)
  val player2HigherFace = maxOf(player2Card1.face, player2Card2.face)

  val player1OddsCacheKeyState: OddsCacheKeyState
  val player2OddsCacheKeyState: OddsCacheKeyState
  val player1SuitState = suitState(player1Card1, player1Card2)
  val player2SuitState = suitState(player2Card1, player2Card2)

  val player1FaceState = faceState(player1Card1, player1Card2)
  val player2FaceState = faceState(player2Card1, player2Card2)

  val player1HigherFaceCard = if (player1HigherFace == player1Card1.face) {
    player1Card1
  } else {
    player1Card2
  }
  val player2HigherFaceCard = if (player2HigherFace == player2Card1.face) {
    player2Card1
  } else {
    player2Card2
  }
  val player1OverlappingSuitState =
    overlappingSuitState(player1Suits, player2Suits, player1HigherFaceCard, player2HigherFaceCard, player1FaceState, player2FaceState, player1SuitState)
  val player2OverlappingSuitState =
    overlappingSuitState(player2Suits, player1Suits, player2HigherFaceCard, player1HigherFaceCard, player2FaceState, player2FaceState, player2SuitState)

  val player1OverlappingFaceState = overlappingFaceState(player1Faces, player2Faces, player1HigherFace)
  val player2OverlappingFaceState = overlappingFaceState(player2Faces, player1Faces, player2HigherFace)

  player1OddsCacheKeyState =
    OddsCacheKeyState(player1SuitState, player1FaceState, player1OverlappingSuitState, player1OverlappingFaceState)
  player2OddsCacheKeyState =
    OddsCacheKeyState(player2SuitState, player2FaceState, player2OverlappingSuitState, player2OverlappingFaceState)

  return if (player1OddsCacheKeyState <= player2OddsCacheKeyState) {
    Pair(Key(player1OddsCacheKeyState, player2OddsCacheKeyState), false)
  } else {
    Pair(Key(player2OddsCacheKeyState, player1OddsCacheKeyState), true)
  }
}

private fun overlappingSuitState(
  player1Suits: Array<Suit>,
  player2Suits: Array<Suit>,
  player1HigherFaceCard: Card,
  player2HigherFaceCard: Card,
  player1FaceState: FaceState,
  player2FaceState: FaceState,
  player1SuitState: SuitState
) = when {
  player1Suits.containsAll(player2Suits) -> if (player1FaceState is SameFaceState || player2FaceState is SameFaceState|| player1SuitState == SuitState.Same || player1HigherFaceCard.suit == player2HigherFaceCard.suit) {
    BothHigher
  } else {
    BothHigherVsLower
  }
  player1Suits.containsNone(player2Suits) -> Neither
  player1FaceState is SameFaceState -> Higher
  player1HigherFaceCard.suit in player2Suits -> Higher
  else -> Lower
}

private fun overlappingFaceState(
  player1Faces: Array<Face>,
  player2Faces: Array<Face>,
  player1HigherFace: Face
) = when {
  player1Faces.containsAll(player2Faces) -> OverlappingFaceState.Both
  player1Faces.containsNone(player2Faces) -> OverlappingFaceState.Neither
  player1HigherFace in player2Faces -> OverlappingFaceState.Higher
  else -> OverlappingFaceState.Lower
}


private fun <T> Array<T>.containsAll(otherArray: Array<T>): Boolean {
  forEach { if (it !in otherArray) return false }
  return true
}

private fun <T> Array<T>.containsNone(otherArray: Array<T>): Boolean {
  forEach { if (it in otherArray) return false }
  return true
}


private fun faceState(
  card1: Card,
  card2: Card
) = when {
  card1.face == card2.face -> SameFaceState(card1.face)
  card1.face > card2.face -> DifferentFaceState(card1.face, card2.face)
  else -> DifferentFaceState(card2.face, card1.face)
}

private fun suitState(
  card1: Card,
  card2: Card
) = if (card1.suit == card2.suit) {
  SuitState.Same
} else {
  SuitState.Different
}

enum class SuitState {
  Same,
  Different
}

@Serializable
sealed class FaceState : Comparable<FaceState>

@Serializable
data class SameFaceState(val face: Face) : FaceState() {
  override fun compareTo(other: FaceState) =
    if (other is SameFaceState) { compareValues(face, other.face) } else { 1 }
}

@Serializable
data class DifferentFaceState(val lowerFace: Face, val higherFace: Face) : FaceState() {
  override fun compareTo(other: FaceState) =
    if (other is DifferentFaceState) {
      Comparator.comparing<DifferentFaceState, Face> { it.lowerFace}
        .thenComparing( compareBy {it.higherFace})
        .compare(this, other) }
    else { -1 }
}

enum class OverlappingSuitState {
  Neither,
  Higher,
  Lower,
  BothHigher,
  BothHigherVsLower
}

enum class OverlappingFaceState {
  Neither,
  Higher,
  Lower,
  Both
}

@Serializable
data class OddsCacheKeyState(
  val suitState: SuitState,
  val faceState: FaceState,
  val overlappingSuitState: OverlappingSuitState,
  val overlappingFaceState: OverlappingFaceState
) : Comparable<OddsCacheKeyState> {
  companion object {
    val COMPARATOR = compareBy<OddsCacheKeyState>(
      { it.suitState },
      { it.faceState },
      { it.overlappingSuitState },
      { it.overlappingFaceState })
  }

  override fun compareTo(other: OddsCacheKeyState) = COMPARATOR.compare(this, other)
}

@Serializable
data class Key(val player1OddsCacheKeyState: OddsCacheKeyState, val player2OddsCacheKeyState: OddsCacheKeyState)

private val cacheFile = File("poker_odds.cache")

val ODDS_CACHE = if (cacheFile.exists()) {
  ProtoBuf.load(OddsCache.serializer(), cacheFile.readBytes())
    .also { println("Loading cache from disk with ${it.size()} entries") }
} else {
  OddsCache().also { println("Fresh cache") }
}

fun main() {
  println(ODDS_CACHE.map.entries.groupBy { it.value }.filter { it.value.size > 1 })

}
