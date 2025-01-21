/*
 * Single r/w port register file of size 2^order; n-bit values
 */

package org.clanpj.chisel.mam.alu

import chisel3._

class RegFile(n: Int, order: Int) extends Module {
  assert(n > 0)
  assert(order > 0)
  assert(order < 31)

  val size = 1 << order

  val io = IO(new Bundle {
    val en = Input(Bool())
    val wEn = Input(Bool())
    val wV = Input(UInt(n.W))
    val i = Input(UInt(order.W))
    val v = Output(UInt(n.W))
  })

  // registers
  val regs = Reg(Vec(size, UInt(n.W)))

  io.v := Mux(io.en, 0x0.U, regs(io.i))

  when (io.en && io.wEn) {
    regs(io.i) := io.wV
  }
}


