package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.util._

class ExtUnit(n: Int) extends Module {
  import ExtOp._

  val io = IO(new Bundle {
    val en = Input(Bool())
    val op = Input(UInt(2.W))
    val src = Input(UInt(n.W)) // TODO only need lo 16 bits
    val v = Output(UInt(n.W))
  })

  io.v := 0.U

  when (io.en) {
    val src = io.src
    switch (io.op) {
      is (ExtExtb.id.U)  { io.v := Cat(Fill(24, src(7)), src(7,0)) }
      is (ExtExtub.id.U) { io.v := src(7,0) }
      is (ExtExtw.id.U)  { io.v := Cat(Fill(16, src(15)), src(15,0)) }
      is (ExtExtuw.id.U) { io.v := src(15,0) }
    }
  }
}
