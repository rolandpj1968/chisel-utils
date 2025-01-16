/*
 * Circular stack of size 2^order; n-bit values
 */

package org.clanpj.chisel.mam.alu

import chisel3._

class Stack(n: Int, order: Int) extends Module {
  assert(n > 0)
  assert(order > 0)
  assert(order < 31)

  val size = 1 << order

  val io = IO(new Bundle {
    val en = Input(Bool())
    val rEn = Input(Bool())
    val i = Input(Bool())
    val v = Output(UInt(n.W))
    val wEn = Input(Bool())
    val nTosV = Input(UInt(n.W))
    val dITos = Input(UInt(order.W))
    val tosV = Output(UInt(n.W))
    val nosV = Output(UInt(n.W))
  })

  // top-of-stack index; full stack
  val iTos = RegInit(0.U(order.W))
  // TODO: we should calculate this from iTos conbinationally (iTos - 1.U) but that results in
  //   verilator linter errors like https://github.com/verilator/verilator/issues/2070
  // next-of-stack index
  val iNos = RegInit(((1<<order)-1).U(order.W))
  // stack entries - circular buffer
  val stack = Reg(Vec(size, UInt(n.W)))

  io.v := 0.U
  io.tosV := 0.U
  io.nosV := 0.U

  when (io.en) {

    when (io.rEn) {
      io.v := stack(iTos + io.i)
    }

    //printf("iTos %d iNos %d\n", iTos, iNos)

    io.tosV := stack(iTos)
    // val iNos = iTos - 1.U - see TODO above
    io.nosV := stack(iNos)

    val iTosNext = iTos + io.dITos
    val iNosNext = iNos + io.dITos

    when (io.wEn) {
      stack(iTosNext) := io.nTosV
    }

    iTos := iTosNext
    iNos := iNosNext
  }
}

