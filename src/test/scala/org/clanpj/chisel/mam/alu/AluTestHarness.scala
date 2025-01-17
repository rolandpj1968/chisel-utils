package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.util._

import org.clanpj.chisel.mam.MamSrc;

class AluTestHarness(n: Int) extends Module {
  import AluMamGenUnit._

  val io = IO(new Bundle {
    val en = Input(Bool())
    val opc = Input(UInt(8.W))

    val nTosV = Output(UInt(n.W))
    val stall = Output(Bool())
    val trap = Output(Bool())
  })

  val alu = Module(new Alu(n))

  alu.io.en := io.en
  alu.io.opc := io.opc

  io.nTosV := alu.io.nTosV
  io.stall := alu.io.stall
  io.trap := alu.io.trap

  // emulate AluMamGenUnit
  val conb0 = VecInit(Seq(0.U(n.W), 1.U(n.W), 2.U(n.W), 3.U(n.W)))

  alu.io.mamV := 0.U

  when (alu.io.mamREn) {
    switch(alu.io.mamUnit) {
      is (UnitConb0.id.U) { alu.io.mamV := conb0(alu.io.mamOp) }
    }
  }

  //printf("                                          RPJ: alu.io.nTosV is %d, io.nTosV is %d\n", alu.io.nTosV, io.nTosV)

}
