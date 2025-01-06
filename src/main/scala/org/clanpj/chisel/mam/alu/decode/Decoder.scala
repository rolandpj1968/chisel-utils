package org.clanpj.chisel.mam.alu.decode

import chisel3._
import chisel3.util._

object AluOpcode extends ChiselEnum {
  val AluOpcNop =      Value(0x00.U)
  val AluOpcDrop =     Value(0x01.U)
  val AluOpcAdd1 =     Value(0x02.U)
  val AluOpcSub1 =     Value(0x03.U)
  val AluOpcAdd2 =     Value(0x04.U)
  val AluOpcSub2 =     Value(0x05.U)
  val AluOpcAdd4 =     Value(0x06.U)
  val AluOpcSub4 =     Value(0x07.U)
  val AluOpcFF   =     Value(0xff.U) // Enforce 8-bit width
}

object AluSrc0 extends ChiselEnum {
  val Src0None, Src0Tos, Src0Stk, Src0Reg, Src0Fwd, Src0Con = Value
}

object AluSrc1 extends ChiselEnum {
  val Src1None, Src1Nos, Src1Lit = Value
}

object AluOp extends ChiselEnum {
  val OpNone, OpAdd, OpShift, OpBits, OpExt, OpInv = Value
}

class Decoder extends Module {
  import AluOpcode._
  import AluSrc0._
  import AluSrc1._
  import AluOp._

  val io = IO(new Bundle {
    val opc = Input(AluOpcode())
    val src0 = Output(AluSrc0())
    val src0X = Output(UInt(2.W))
    val src0M = Output(Bool())
    val src1 = Output(AluSrc1())
    val src1X = Output(UInt(2.W))
    val src1M = Output(Bool())
    val op = Output(AluOp())
    val subop = Output(UInt(3.W))
    val wr = Output(Bool())
    val dItos = Output(UInt(2.W))
  })

  val src0 = Src0None; val src0X = 0.U(2.W); val src0M = false.B;
  val src1 = Src1None; val src1X = 0.U(2.W); val src1M = false.B;
  val op = OpInv; val subop = 0.U(3.W)
  val wr = false.B
  val dITos = 0.U(2.W)

  switch (io.opc) {
    is (AluOpcNop) { op := OpNone; }
  }
}
