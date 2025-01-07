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

    val src = Output(MamSrc())
    val srcI = Output(UInt(3.W))
    val srcV = Input(UInt(n.W))

    val nTosV = Output(UInt(n.W))

    val stall = Output(Bool())

    val trap = Output(Bool())
  })

  val decoder = Module(new Decoder)

  decoder.io.opc := 0.U
  when (io.enable) {
    decoder.io.opc := io.opc
  }
}
