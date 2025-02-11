package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.util._

import org.clanpj.chisel.mam.MamSrc;

class AluTestHarness(n: Int) extends Module {
  import AluMamGenUnit._

  def bi(i: Long) = BigInt(i)

  val mask = (bi(1) << n) - 1

  val io = IO(new Bundle {
    val en = Input(Bool())
    val opc = Input(UInt(8.W))

    val nTosV = Output(UInt(n.W))
    val stall = Output(Bool())
    val trap = Output(Bool())

    val lTosV = Output(UInt(n.W))
  })

  val alu = Module(new Alu(n))

  val lTosV = RegInit(0.U(n.W))

  alu.io.en := io.en
  alu.io.opc := io.opc

  io.lTosV := lTosV
  io.nTosV := alu.io.nTosV
  io.stall := alu.io.stall
  io.trap := alu.io.trap

  when (!alu.io.nop) {
    lTosV := alu.io.nTosV
  }

  // emulate AluMamGenUnit
  val conb0 = VecInit(Seq(0.U(n.W), 1.U(n.W), 2.U(n.W), 3.U(n.W)))
  val conb =  VecInit(Seq((bi(-1) & mask).U(n.W), (bi(-2) & mask).U(n.W), (bi(-3) & mask).U(n.W), (bi(-4) & mask).U(n.W)))

  alu.io.mamV := 0.U

  when (alu.io.mamREn) {
    switch(alu.io.mamUnit) {
      is (UnitConb0.id.U) { alu.io.mamV := conb0(alu.io.mamOp) }
      is (UnitConb.id.U) { alu.io.mamV := conb(alu.io.mamOp) }
    }
  }

  //printf("                                          RPJ: alu.io.nTosV is %d, io.nTosV is %d\n", alu.io.nTosV, io.nTosV)

}
