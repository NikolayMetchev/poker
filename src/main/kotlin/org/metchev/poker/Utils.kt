package org.metchev.poker

@ExperimentalUnsignedTypes
fun nTuples(n: UInt, cards: List<Card>): Sequence<Array<Card>> {
  val myCards = cards.toTypedArray()
  val vars: Array<Int> = Array(n.toInt()) { it }
  val result: Array<Card> = Array(n.toInt()) { myCards[vars[it]] }
  return generateSequence {
    if (vars[0] > cards.size - n.toInt()) {
      return@generateSequence null
    }
    var i = 0
    while (i < n.toInt()) {
      result[i] = myCards[vars[i]]
      i++
    }
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
