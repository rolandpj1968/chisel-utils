package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.util._

import org.clanpj.chisel.util.fulladdern.FullAdderN
import org.clanpj.chisel.util.fulladdern.FullAdderN._

class AdderUnit(n: Int) extends Module {
  import AdderOp._

  val io = IO(new Bundle {
    val en = Input(Bool())
    val op = Input(UInt(2.W))
    val src0 = Input(UInt(n.W))
    val src1 = Input(UInt(n.W))
    val bin = Input(Bool())
    val v = Output(UInt(n.W))
  })

  val adder = Module(FullAdderN.simple(n))
  adder.io.cin := 0.U
  adder.io.x := 0.U
  adder.io.y := 0.U

  io.v := 0.U

  when (io.en) {
    val cin = Wire(UInt(1.W))
    cin := 0.U
    val src0 = Wire(UInt(n.W))
    src0 := Mux(io.bin, io.src0, 0.U)
    val src1 = Wire(UInt(n.W))
    src1 := io.src1
    when (io.op =/= AdderAdd.id.U) {
      cin := 1.U
      src1 := ~io.src1
    }

    adder.io.cin := cin
    adder.io.x := src0
    adder.io.y := src1

    io.v := adder.io.sum
    val sameHibitLt = ~(io.src0(n-1) ^ io.src1(n-1)) & adder.io.sum(n-1)
    when (io.op === AdderSltu.id.U) {
      io.v := (~io.src0(n-1) & io.src1(n-1)) | sameHibitLt
    } .elsewhen(io.op === AdderSlt.id.U) {
      io.v := (io.src0(n-1) & ~io.src1(n-1)) | sameHibitLt
    }
  }
}
