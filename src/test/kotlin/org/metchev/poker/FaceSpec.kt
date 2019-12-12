package org.metchev.poker

import org.metchev.poker.Face.*
import org.metchev.poker.Suit.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertFails

@ExperimentalUnsignedTypes
class FaceSpec : Spek({
  describe("Face") {
    it("Ordering of faces") {
      assertEquals(JACK, `10`.next())
      assertEquals(ACE, KING.next())
      assertEquals(`2`, ACE.next())
    }
  }
})
