package org.metchev.poker

@ExperimentalUnsignedTypes
fun nTuples(n: UInt, cards: List<Card>): Sequence<Array<Card>> {
  val myCards = ArrayList<Card>(cards)
  val vars: Array<Int> = Array(n.toInt()) { it }
  return generateSequence {
    if (vars[0] > cards.size - n.toInt()) {
      return@generateSequence null
    }
    val result : Array<Card> = Array(n.toInt()) {myCards[vars[it]]}
    increment(vars, (n - 1u).toInt(), n, cards.size)
    result
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
