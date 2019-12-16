package org.metchev.poker

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
fun increment(vars: Array<Int>, index: Int, n: UInt, totalNumberOfCards: Int) {
  vars[index] = vars[index] + 1
  if (vars[index] == totalNumberOfCards - n.toInt() + index + 1) {
    if (index > 0) {
      increment(vars, (index - 1), n, totalNumberOfCards)
      vars[index] = vars[index - 1] + 1
    }
  }
}
