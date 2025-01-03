/*
 * Abstract N-bit Full Adder
 */

package org.clanpj.chisel.fulladdern

import chisel3._

abstract class FullAdderN(n: Int) extends Module {
  val io = IO(new Bundle {
    val cin = Input(UInt(1.W))
    val op1 = Input(UInt(n.W))
    val op2 = Input(UInt(n.W))
    val sum = Output(UInt(n.W))
    val cout = Output(UInt(1.W))
  })

  assert(n > 0)

  def width = n

  def desc: String
}

object FullAdderN {
  def simple(n: Int) = new SimpleFullAdderN(n)
}
