package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

import org.clanpj.chisel.mam.MamSrc;

class Alu(n: Int) extends Module {
  import AluSrc1._
  import AluUnit._

  // Interface with MAM mothership
  val io = IO(new Bundle {
    val enable = Input(Bool())
    val opc = Input(UInt(8.W))

    val src = Output(MamSrc())
    val srcI = Output(UInt(3.W))
    val srcV = Input(UInt(n.W))

    val nTosV = Output(UInt(n.W))

    // val stall = Output(Bool())

    val trap = Output(Bool())
  })

  val decoder = Module(new Decoder)

  decoder.io.opcRaw := 0.U
  when (io.enable) {
    decoder.io.opcRaw := io.opc
  }

  val regfile = Module(new RegFile(n, 2/*^2*/))
  regfile.io.wEn := decoder.io.wr
  regfile.io.i := decoder.io.src1X(1, 0)

  val stack = Module(new Stack(n, 2/*^2*/))
  stack.io.wEn := false.B // TODO remove
  stack.io.newTosV := 0.U // TODO remove
  stack.io.dITos := decoder.io.dITos

  val src1raw = Wire(UInt(n.W))
  src1raw := stack.io.nosV
  io.src := MamSrc.SrcNone
  io.srcI := decoder.io.src1X
  switch (decoder.io.src1) {
    is (Src1Lit) {}
    is (Src1Alu) { io.src := MamSrc.SrcAlu; src1raw := io.srcV; }
    is (Src1Stk) {}
    is (Src1Reg) { src1raw := regfile.io.v }
    is (Src1Con) { io.src := MamSrc.SrcConB; src1raw := io.srcV; } // TODO - fix Decoder
  }

  val src0raw = Wire(UInt(n.W))
  src0raw := stack.io.tosV

  val src1 = Wire(UInt(n.W))
  src1 := src1raw
  when (decoder.io.src1N) {
    src1 := ~src1raw
  }

  val src0 = Wire(UInt(n.W))
  src0 := src0raw
  when (decoder.io.src0N) {
    src0 := ~src0raw
  }

  val res = Wire(UInt(n.W))
  res := 0.U

  io.trap := false.B
  switch (decoder.io.unit) {
    is (UnitNone)  {}
    is (UnitSrc1)  { res := src1 }
    is (UnitAdd)   {}
    is (UnitShift) {}
    is (UnitBits)  {}
    is (UnitExt)   {}
    is (UnitSel)   {}
    is (UnitInv)   { io.trap := true.B }
  }

  regfile.io.wVal := res
  
  val nTosV = Wire(UInt(n.W))
  nTosV := Mux(decoder.io.res, res, stack.io.tosVNext)

  io.nTosV := nTosV
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
