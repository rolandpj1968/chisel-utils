/*
 * Trivial n-bit full-adder
 */

package org.clanpj.chisel.util.fulladdern

import chisel3._

class SimpleFullAdderN(n: Int) extends FullAdderN(n) {
  val fullsum = (io.x +& io.y) + io.cin
  io.sum := fullsum(n-1, 0)
  io.cout := fullsum(n)
  //printf(desc + ": x %d y %d cin %d -> sum %d cout %d\n", io.x, io.y, io.cin, io.sum, io.cout)

  def desc = "simple" + n
}
