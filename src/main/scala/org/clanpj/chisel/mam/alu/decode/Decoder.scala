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

object Src1LitX extends ChiselEnum {
  val LitX0, LitX1, LitX2, LitX4 = Value
}

object AluOp extends ChiselEnum {
  val OpNone, OpAdd, OpShift, OpBits, OpExt, OpInv = Value
}

class Decoder extends Module {
  import AluOpcode._
  import AluSrc0._
  import AluSrc1._
  import Src1LitX._
  import AluOp._

  val io = IO(new Bundle {
    val opc = Input(AluOpcode())
    val src0 = Output(AluSrc0())
    val src0N = Output(Bool())
    val src1 = Output(AluSrc1())
    val src1N = Output(Bool())
    val op = Output(AluOp())
    val wr = Output(Bool())
    val dITos = Output(UInt(2.W))
    val xtra = Output(UInt(3.W)) // Src1LitX, 
  })

  val src0 = Src0None; val src0N = false.B;
  val src1 = Src1None; val src1N = false.B;
  val op = OpInv
  val wr = false.B
  val dITos = 0.U(2.W)
  val xtra = 0.U(3.W)

  switch (io.opc) {
    is (AluOpcNop)  { op := OpNone; }
    is (AluOpcDrop) { op := OpNone; dITos := 3.U; }
    is (AluOpcAdd1) { op := OpAdd; src0 := Src0Tos; src1 := Src1Lit;                  xtra := LitX1.asUInt; wr := true.B; }
    is (AluOpcSub1) { op := OpAdd; src0 := Src0Tos; src1 := Src1Lit; src1N := true.B; xtra := LitX1.asUInt; wr := true.B; }
    is (AluOpcAdd2) { op := OpAdd; src0 := Src0Tos; src1 := Src1Lit;                  xtra := LitX2.asUInt; wr := true.B; }
    is (AluOpcSub2) { op := OpAdd; src0 := Src0Tos; src1 := Src1Lit; src1N := true.B; xtra := LitX2.asUInt; wr := true.B; }
    is (AluOpcAdd4) { op := OpAdd; src0 := Src0Tos; src1 := Src1Lit;                  xtra := LitX4.asUInt; wr := true.B; }
    is (AluOpcSub4) { op := OpAdd; src0 := Src0Tos; src1 := Src1Lit; src1N := true.B; xtra := LitX4.asUInt; wr := true.B; }
  }

  io.src0 := src0; io.src0N := src0N;
  io.src1 := src1; io.src1N := src1N;
  io.op := op
  io.wr := wr
  io.dITos := dITos
  io.xtra := xtra
}
