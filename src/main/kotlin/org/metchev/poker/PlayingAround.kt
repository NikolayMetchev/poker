package org.metchev.poker

import java.io.FileOutputStream
import java.io.PrintWriter
import java.util.zip.GZIPOutputStream

//@ExperimentalUnsignedTypes
//fun main() {
//  println(computeOdds(Card.`8_OF_HEARTS`, Card.`6_OF_HEARTS`,   Card.`8_OF_DIAMONDS`, Card.`6_OF_DIAMONDS`))
//  println(computeOdds(Card.`8_OF_HEARTS`, Card.`7_OF_HEARTS`,   Card.`8_OF_DIAMONDS`, Card.`7_OF_DIAMONDS`))
//  println(computeOdds(Card.`8_OF_HEARTS`, Card.`7_OF_DIAMONDS`, Card.`8_OF_DIAMONDS`, Card.`7_OF_HEARTS`))
//  println(computeOdds(Card.`8_OF_HEARTS`, Card.`6_OF_DIAMONDS`, Card.`8_OF_DIAMONDS`, Card.`6_OF_HEARTS`))
//  println(computeOdds(Card.`8_OF_HEARTS`, Card.`5_OF_DIAMONDS`, Card.`8_OF_DIAMONDS`, Card.`5_OF_HEARTS`))
////  println(computeOdds(Card.`8_OF_HEARTS`, Card.`7_OF_DIAMONDS`, Card.`8_OF_DIAMONDS`, Card.`7_OF_HEARTS`))
//}

@ExperimentalUnsignedTypes
fun main() {
  PrintWriter(GZIPOutputStream(FileOutputStream("allodds.out.gz"))).use { pw ->
    val deck = Array(Card.values().size) {Card.values()[it]}
    val nTuples = deck.nTuples(2u)
    var i = 1
    nTuples.forEach {
      val player1Hand = Hand(it[0], it[1])
      printOdds(player1Hand, pw)
      i++
    }
    pw.flush()
  }
}

@ExperimentalUnsignedTypes
fun printOdds(player1Hand : Hand, pw: PrintWriter) {
  var deck: Array<Card> = Array(Card.values().size) {
    Card.values()[it]
  }
  deck -= arrayOf(player1Hand.cards.first, player1Hand.cards.second)
  val player2Tuples = deck.nTuplesList(2u)
  player2Tuples.forEach {
    val player2Hand = Hand(it[0], it[1])
    val odds = computeOdds(player1Hand, player2Hand)
    pw.println("${player1Hand.cards.first},${player1Hand.cards.second},${it[0]},${it[1]} = ${odds.player1Wins},${odds.player2Wins},${odds.split}")
  }
}
