package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.util._

class BitsUnit(n: Int) extends Module {
  import BitsOp._

  val io = IO(new Bundle {
    val en = Input(Bool())
    val op = Input(UInt(2.W))
    val src0 = Input(UInt(n.W))
    val src1 = Input(UInt(n.W))
    val bin = Input(Bool())
    val v = Output(UInt(n.W))
  })

  io.v := 0.U

  when (io.en) {
    val cin = Wire(UInt(1.W))
    cin := 0.U
    val src0 = Wire(UInt(n.W))
    src0 := Mux(io.bin, io.src0, 0.U)
    val src1 = Wire(UInt(n.W))
    src1 := Mux(io.bin, io.src1, ~io.src1)
    switch (io.op) {
      is (BitsAnd.id.U) { io.v := src0 & src1 }
      is (BitsOr.id.U)  { io.v := src0 | src1 }
      is (BitsXor.id.U) { io.v := src0 ^ src1 }
      is (BitsSeq.id.U) { io.v := ~((src0 ^ src1).orR) }
    }
  }
}
