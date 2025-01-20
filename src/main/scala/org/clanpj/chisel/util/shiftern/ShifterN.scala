/*
 * Abstract N-bit shifter
 */

package org.clanpj.chisel.util.shiftern

import chisel3._

abstract class ShifterN(n: Int) extends Module {
  assert(n > 0)

  val io = IO(new Bundle {
    val left = Input(Bool())
    val arith = Input(Bool())
    val x = Input(UInt(n.W))
    val y = Input(UInt(n.W))
    val v = Output(UInt(n.W))
  })

  def width = n

  def desc: String
}

object ShifterN {
  def simple(n: Int) = new SimpleShifterN(n)
  def log(n: Int) = new LogShifterN(n)
}
