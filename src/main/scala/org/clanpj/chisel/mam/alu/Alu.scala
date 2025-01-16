package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

import org.clanpj.chisel.mam.MamSrc;

class Alu(n: Int) extends Module {
  import AluUnit._
  import AluGenUnit._

  // Interface with MAM mothership
  val io = IO(new Bundle {
    val en = Input(Bool())
    val opc = Input(UInt(8.W))

    val mamREn = Output(Bool())
    val mamUnit = Output(UInt(3.W))
    val mamOp = Output(UInt(2.W))
    val mamV = Input(UInt(n.W))

    val nTosV = Output(UInt(n.W))

    val stall = Output(Bool())

    val trap = Output(Bool())
  })

  val decoder = Module(new Decoder)

  decoder.io.opc := 0.U
  when (io.en) {
    decoder.io.opc := io.opc
  }

  io.trap := decoder.io.inv
  io.stall := false.B

  val en = !(decoder.io.nop || decoder.io.inv)
  val genEn = en && decoder.io.gen
  val aluGenEn = genEn && !decoder.io.mam
  val mamGenEn = genEn && decoder.io.mam

  val unitOH = UIntToOH(decoder.io.unit)

  val regfile = Module(new RegFile(n, 2/*^2*/))
  regfile.io.en := aluGenEn && unitOH(1 << UnitReg.id)
  regfile.io.wEn := en && decoder.io.wr
  regfile.io.wVal := 0.U // TODO remove
  regfile.io.i := decoder.io.op

  val stack = Module(new Stack(n, 2/*^2*/))
  stack.io.en := en
  stack.io.rEn := aluGenEn && unitOH(1 << UnitStack.id)
  stack.io.i := decoder.io.op
  stack.io.wEn := true.B // TODO remove
  stack.io.newTosV := 0.U // TODO remove
  stack.io.dITos := Mux(decoder.io.gen, 0x1.U, Mux(decoder.io.bin, 0x0.U, ((1<<2)-1).U))

  io.mamREn := mamGenEn
  io.mamUnit := decoder.io.unit
  io.mamOp := decoder.io.op
  io.nTosV := 0.U // TODO remove

  val src0 = Wire(UInt(n.W))
  src0 := stack.io.nosV
  val src1 = Wire(UInt(n.W))
  src1 := stack.io.tosV

  val arithGenEn = en && !decoder.io.gen

  val adderUnit = Module(new AdderUnit(n))
  adderUnit.io.en := arithGenEn && unitOH(1 << UnitAdd.id)
  adderUnit.io.op := decoder.io.op
  adderUnit.io.src0 := src0
  adderUnit.io.src1 := src1
  adderUnit.io.bin := decoder.io.bin

  // val src1raw = Wire(UInt(n.W))
  // src1raw := stack.io.nosV
  // io.src := MamSrc.SrcNone
  // io.srcI := decoder.io.src1X
  // switch (decoder.io.src1) {
  //   is (Src1Lit) {}
  //   is (Src1Alu) { io.src := MamSrc.SrcAlu; src1raw := io.srcV; }
  //   is (Src1Stk) {}
  //   is (Src1Reg) { src1raw := regfile.io.v }
  //   is (Src1Con) { io.src := MamSrc.SrcConB; src1raw := io.srcV; } // TODO - fix Decoder
  // }

  // val src0raw = Wire(UInt(n.W))
  // src0raw := stack.io.tosV

  // val src1 = Wire(UInt(n.W))
  // src1 := src1raw
  // when (decoder.io.src1N) {
  //   src1 := ~src1raw
  // }

  // val src0 = Wire(UInt(n.W))
  // src0 := src0raw
  // when (decoder.io.src0N) {
  //   src0 := ~src0raw
  // }

  // val res = Wire(UInt(n.W))
  // res := 0.U

  // switch (decoder.io.unit) {
  //   is (UnitNone)  {}
  //   is (UnitSrc1)  { res := src1 }
  //   is (UnitAdd)   {}
  //   is (UnitShift) {}
  //   is (UnitBits)  {}
  //   is (UnitExt)   {}
  //   is (UnitSel)   {}
  //   is (UnitInv)   { io.trap := true.B }
  // }

  // regfile.io.wVal := res
  
  // val nTosV = Wire(UInt(n.W))
  // nTosV := Mux(decoder.io.res, res, stack.io.tosVNext)

  // io.nTosV := nTosV
}

/**
 * Generate Verilog sources and save it in file Alu.sv
 */
object Alu extends App {
  ChiselStage.emitSystemVerilogFile(
    new Alu(32),
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
  )
}
