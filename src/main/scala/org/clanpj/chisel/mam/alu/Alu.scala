package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

import org.clanpj.chisel.mam.MamSrc;

class Alu(n: Int) extends Module {

  // Interface with MAM mothership
  val io = IO(new Bundle {
    val enable = Input(Bool())
    val opc = Input(UInt(8.W))

    // val src = Output(MamSrc())
    // val srcI = Output(UInt(3.W))
    // val srcV = Input(UInt(n.W))

    val nTosV = Output(UInt(n.W))

    // val stall = Output(Bool())

    // val trap = Output(Bool())
  })

  val decoder = Module(new Decoder)

  decoder.io.opcRaw := 0.U
  when (io.enable) {
    decoder.io.opcRaw := io.opc
  }

  val regfile = Module(new RegFile(n, 2/*^2*/))
  regfile.io.wEn := false.B // TODO remove
  regfile.io.wVal := 0.U // TODO remove
  regfile.io.i := decoder.io.src1X(1, 0)

  val stack = Module(new Stack(n, 2/*^2*/))
  stack.io.wEn := false.B // TODO remove
  stack.io.newTosV := 0.U // TODO remove
  stack.io.dITos := decoder.io.dITos

  val src0raw = Wire(UInt(n.W))
  val src1raw = Wire(UInt(n.W))

  src0raw := stack.io.tosV
  src1raw := stack.io.nosV

  val src0 = Wire(UInt(n.W))
  src0 := src0raw
  when (decoder.io.src0N) {
    src0 := ~src0raw
  }

  val src1 = Wire(UInt(n.W))
  src1 := src1raw
  when (decoder.io.src1N) {
    src1 := ~src1raw
  }

  val res = Wire(UInt(n.W))
  res := stack.io.tosVNext

  io.nTosV := res
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
