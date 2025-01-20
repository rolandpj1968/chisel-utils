/*
 * O(logN) n-bit shifter
 */

package org.clanpj.chisel.util.shiftern

import chisel3._
import chisel3.util._

class LogShifterN(n: Int) extends ShifterN(n) {
  val nbits = log2Ceil(n)
  //val shift = io.y(nbits-1, 0)
  val over = io.y(n-1, nbits).orR
  val sign = io.x(n-1)
  val fill = Mux(io.arith, sign, false.B)

  val leftn = (0 to nbits).map(_ => Wire(UInt(n.W)))
  leftn(nbits) := io.x
  for(bn <- nbits-1 to 0 by -1) {
    val vbn = Cat(leftn(bn+1)(n-(1 << bn)-1, 0), Fill(1 << bn, 0.U))
    leftn(bn) := Mux(io.y(bn), vbn, leftn(bn+1))
  }

  val rightn = (0 to nbits).map(_ => Wire(UInt(n.W)))
  rightn(nbits) := io.x
  for(bn <- nbits-1 to 0 by -1) {
    val vbn = Cat(Fill(1 << bn, fill), rightn(bn+1)(n-1, (1 << bn)))
    rightn(bn) := Mux(io.y(bn), vbn, rightn(bn+1))
  }

  when (over) {
    io.v := Mux(io.left || !io.arith, 0.U, Fill(n, sign))
  } otherwise {
    io.v := Mux(io.left, leftn(0), rightn(0))
  }
  printf("log-shiftern: x %d y %d left %d arith %d -> v %d\n", io.x, io.y, io.left, io.arith, io.v)

  def desc = "simple" + n
}
