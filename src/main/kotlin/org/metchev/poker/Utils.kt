package org.metchev.poker

import java.util.*

@ExperimentalUnsignedTypes
fun nTuples(n: UInt, cards: List<Card>) = nTuples(n, cards.toTypedArray())

@ExperimentalUnsignedTypes
fun nTuples(n: UInt, myCards: Array<Card>): Sequence<Array<Card>> {
  val vars: Array<Int> = Array(n.toInt()) { it }
  val result: Array<Card> = Array(n.toInt()) { myCards[vars[it]] }
  return generateSequence {
    if (vars[0] > myCards.size - n.toInt()) {
      return@generateSequence null
    }
    var i = 0
    while (i < n.toInt()) {
      result[i] = myCards[vars[i]]
      i++
    }
    increment(vars, (n - 1u).toInt(), n, myCards.size)
    result
  }
}

@ExperimentalUnsignedTypes
fun Array<Card>.nTuplesPair(n: UInt): Sequence<Pair<Array<Card>, Array<Card>>> {
  val vars: Array<Int> = Array(n.toInt()) { it }
  val result: Array<Card> = Array(n.toInt()) { this[vars[it]] }
  return generateSequence {
    if (vars[0] > this.size - n.toInt()) {
      return@generateSequence null
    }
    var i = 0
    while (i < n.toInt()) {
      result[i] = this[vars[i]]
      i++
    }
    increment(vars, (n - 1u).toInt(), n, this.size)
    Pair(result, this - result)
  }
}

@ExperimentalUnsignedTypes
fun Array<Card>.nTuples(n: UInt): Sequence<Array<Card>> {
  val vars: Array<Int> = Array(n.toInt()) { it }
  val result: Array<Card> = Array(n.toInt()) { this[vars[it]] }
  return generateSequence {
    if (vars[0] > this.size - n.toInt()) {
      return@generateSequence null
    }
    var i = 0
    while (i < n.toInt()) {
      result[i] = this[vars[i]]
      i++
    }
    increment(vars, (n - 1u).toInt(), n, this.size)
    result
  }
}

@ExperimentalUnsignedTypes
fun Array<Card>.nTuplesList(n: UInt) = nTuples(n).map { it.clone() }.toList()

internal inline operator fun <reified T> Array<T>.minus(elements: Array<out T>): Array<T> {
  var j = 0
  return Array(size - elements.size) {
    while (this[j] in elements) {
      j++
    }
    this[j++]
  }
}

@ExperimentalUnsignedTypes
fun increment(vars: Array<Int>, index: Int, n: UInt, totalNumberOfCards: Int) {
  vars[index] = vars[index] + 1
  if (vars[index] == totalNumberOfCards - n.toInt() + index + 1) {
    if (index > 0) {
      increment(vars, (index - 1), n, totalNumberOfCards)
      vars[index] = vars[index - 1] + 1
    }
  }
}

fun Array<out Card>.toBitSet() : Long {
  val result = BitSet(Card.values().size)
  for (card in this) {
    result.set(card.ordinal)
  }
  return result.toLongArray()[0]
}

fun Long.fromBitSet() : Array<Card> {
  val bitSet: BitSet = BitSet.valueOf(longArrayOf(this))
  var nextSetBit = bitSet.nextSetBit(0)
  return Array(bitSet.cardinality()) {
    Card.values()[nextSetBit].also { nextSetBit = bitSet.nextSetBit(nextSetBit + 1) }
  }
}
