/*
 * Circular stack of size 2^order
 */

package org.clanpj.chisel.mam.stack

import chisel3._

class Stack(n: Int, order: Int) extends Module {
  assert(n > 0)
  assert(order > 0)

  val size = 1 << order

  val io = IO(new Bundle {
    val wEn = Input(UInt(1.W))
    val nextTos = Input(UInt(n.W))
    val dItos = Input(UInt(order.W))
    val tos = Output(UInt(n.W))
    val nos = Output(UInt(n.W))
  })

  // top-of-stack index; full stack
  val iTos = RegInit(0.U(order.W))
  // stack entries - circular buffer
  val stack = Reg(Vec(size, UInt(n.W)))

  io.tos := stack(iTos)
  io.nos := stack(iTos-1.U)

  val iTosNext = iTos + io.dItos
  when (io.wEn(0)) {
    stack(iTosNext) := io.nextTos
  }

  iTos := iTosNext
}

