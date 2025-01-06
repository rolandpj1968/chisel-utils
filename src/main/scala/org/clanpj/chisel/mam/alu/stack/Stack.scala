/*
 * Circular stack of size 2^order, of n-bit values
 */

package org.clanpj.chisel.mam.alu.stack

import chisel3._

class Stack(n: Int, order: Int) extends Module {
  assert(n > 0)
  assert(order > 0)
  assert(order < 31)

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
  // TODO: we should calculate this from iTos conbinationally (iTos - 1.U) but that results in
  //   verilator linter errors like https://github.com/verilator/verilator/issues/2070
  // next-of-stack index
  val iNos = RegInit(((1<<order)-1).U(order.W))
  // stack entries - circular buffer
  val stack = Reg(Vec(size, UInt(n.W)))

  //printf("iTos %d iNos %d\n", iTos, iNos)

  io.tos := stack(iTos)
  // val iNos = iTos - 1.U - see TODO above
  io.nos := stack(iNos)

  val iTosNext = iTos + io.dItos
  val iNosNext = iNos + io.dItos
  when (io.wEn(0)) {
    stack(iTosNext) := io.nextTos
  }

  iTos := iTosNext
  iNos := iNosNext
}

