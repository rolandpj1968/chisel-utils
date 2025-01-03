/*
 * Trivial n-bit full-adder
 */

package org.clanpj.chisel.fulladdern

import chisel3._

class SimpleFullAdderN(n: Int) extends FullAdderN(n) {
  val fullsum = (io.op1 +& io.op2) + io.cin
  io.sum := fullsum(n-1, 0)
  io.cout := fullsum(n)
  //printf("op1 %d op2 %d sum %d\n", io.op1, io.op2, io.sum)

  def desc = "simple" + n
}
