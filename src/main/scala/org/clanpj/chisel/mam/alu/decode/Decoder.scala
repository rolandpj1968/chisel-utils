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

  /* Integer Arithmetic XLEN width */

  val AluOpcAdd =      Value(0x08.U)
  val AluOpcSub =      Value(0x09.U)
  val AluOpcRsub =     Value(0x0a.U)
  val AluOpcNeg =      Value(0x0b.U)

  /* Integer add with remote ALU tos (PREVIOUS cycle value) XLEN width */

  val AluOpcAddA0 =    Value(0x0c.U)
  val AluOpcAddA1 =    Value(0x0d.U)
  val AluOpcAddA2 =    Value(0x0e.U)
  val AluOpcAddA3 =    Value(0x0f.U)

  /* Shift Binary - XLEN width */

  val AluOpcSll =      Value(0x10.U)
  val AluOpcSrl =      Value(0x11.U)
  val AluOpcSra =      Value(0x12.U)

  /* Bits - XLEN width */

  val AluOpcAnd =      Value(0x14.U)
  val AluOpcOr =       Value(0x13.U)
  val AluOpcXor =      Value(0x14.U)
  val AluOpcNot =      Value(0x15.U)

  /* Comparisons - XLEN width */

  val AluOpcSlt =      Value(0x18.U)
  val AluOpcSltu =     Value(0x19.U)
  val AluOpcSeq =      Value(0x1a.U)

  /* Integer comparisons with remote ALU tos (PREVIOUS cycle value) XLEN width */

  val AluOpcSltA0 =    Value(0x1c.U)
  val AluOpcSltA1 =    Value(0x1d.U)
  val AluOpcSltA2 =    Value(0x1e.U)
  val AluOpcSltA3 =    Value(0x1f.U)
  val AluOpcSltuA0 =   Value(0x20.U)
  val AluOpcSltuA1 =   Value(0x21.U)
  val AluOpcSltuA2 =   Value(0x22.U)
  val AluOpcSltuA3 =   Value(0x23.U)
  val AluOpcSeqA0 =    Value(0x24.U)
  val AluOpcSeqA1 =    Value(0x25.U)
  val AluOpcSeqA2 =    Value(0x26.U)
  val AluOpcSeqA3 =    Value(0x27.U)

  val AluOpcFF   =     Value(0xff.U) // Enforce 8-bit width
}

object AluSrc0 extends ChiselEnum {
  val Src0None, Src0Tos, Src0Stk, Src0Reg, Src0Fwd, Src0Con = Value
}

object AluSrc1 extends ChiselEnum {
  val Src1None, Src1Nos, Src1Lit, Src1Alu = Value
}

object Src1LitX extends ChiselEnum {
  val LitX0, LitX1, LitX2, LitX4 = Value
}

object Src1AluX extends ChiselEnum {
  val AluX0, AluX1, AluX2, AluX3 = Value
}

object AluOp extends ChiselEnum {
  val OpNone, OpAdd, OpSlt, OpSltu, OpShift, OpBits, OpExt, OpInv = Value
}

object ShiftX extends ChiselEnum {
  val SllX, SrlX, SraX = Value
}

// XorZ for seq*
object BitsX extends ChiselEnum {
  val AndX, OrX, XorX, XorZX = Value
}


class Decoder extends Module {
  import AluOpcode._
  import AluSrc0._
  import AluSrc1._
  import Src1LitX._
  import Src1AluX._
  import AluOp._
  import ShiftX._
  import BitsX._

  val io = IO(new Bundle {
    val opc = Input(AluOpcode())
    val src0 = Output(AluSrc0())
    val src0N = Output(Bool())
    val src1 = Output(AluSrc1())
    val src1N = Output(Bool())
    val op = Output(AluOp())
    val fwd = Output(Bool())
    val wr = Output(Bool())
    val dITos = Output(UInt(2.W))
    val xtra = Output(UInt(3.W)) // Src1LitX, 
  })

  val src0 = Src0None; val src0N = false.B;
  val src1 = Src1None; val src1N = false.B;
  val op = OpInv
  val fwd = false.B
  val wr = false.B
  val dITos = 0.U(2.W)
  val xtra = 0.U(3.W)

  switch (io.opc) {
    is (AluOpcNop)   { op := OpNone; }
    is (AluOpcDrop)  { op := OpNone; dITos := 3.U; }
    is (AluOpcAdd1)  { op := OpAdd; src0 := Src0Tos; src1 := Src1Lit;                  xtra := LitX1.asUInt; wr := true.B; }
    is (AluOpcSub1)  { op := OpAdd; src0 := Src0Tos; src1 := Src1Lit; src1N := true.B; xtra := LitX1.asUInt; wr := true.B; }
    is (AluOpcAdd2)  { op := OpAdd; src0 := Src0Tos; src1 := Src1Lit;                  xtra := LitX2.asUInt; wr := true.B; }
    is (AluOpcSub2)  { op := OpAdd; src0 := Src0Tos; src1 := Src1Lit; src1N := true.B; xtra := LitX2.asUInt; wr := true.B; }
    is (AluOpcAdd4)  { op := OpAdd; src0 := Src0Tos; src1 := Src1Lit;                  xtra := LitX4.asUInt; wr := true.B; }
    is (AluOpcSub4)  { op := OpAdd; src0 := Src0Tos; src1 := Src1Lit; src1N := true.B; xtra := LitX4.asUInt; wr := true.B; }

    /* Integer Arithmetic XLEN width */

    is (AluOpcAdd)   { op := OpAdd; src0 := Src0Tos;                  src1 := Src1Nos;                       wr := true.B; dITos := 3.U; }
    is (AluOpcSub)   { op := OpAdd; src0 := Src0Tos;                ; src1 := Src1Nos; src1N := true.B;      wr := true.B; dITos := 3.U; }
    is (AluOpcRsub)  { op := OpAdd; src0 := Src0Tos; src0N := true.B; src1 := Src1Nos;                       wr := true.B; dITos := 3.U; }
    is (AluOpcNeg)   { op := OpAdd; src0 := Src0Tos; src0N := true.B; src1 := Src1Lit; xtra := LitX0.asUInt; wr := true.B; }

    /* Integer add with remote ALU tos (PREVIOUS cycle value) XLEN width */

    is (AluOpcAddA0) { op := OpAdd; src0 := Src0Tos;                  src1 := Src1Alu; xtra := AluX0.asUInt; wr := true.B; }
    is (AluOpcAddA1) { op := OpAdd; src0 := Src0Tos;                  src1 := Src1Alu; xtra := AluX1.asUInt; wr := true.B; }
    is (AluOpcAddA2) { op := OpAdd; src0 := Src0Tos;                  src1 := Src1Alu; xtra := AluX2.asUInt; wr := true.B; }
    is (AluOpcAddA3) { op := OpAdd; src0 := Src0Tos;                  src1 := Src1Alu; xtra := AluX3.asUInt; wr := true.B; }

    /* Shift Binary - XLEN width */

    is (AluOpcSll) { op := OpShift; src0 := Src0Tos; src1 := Src1Nos; xtra := SllX; wr := true.B; dITos := 3.U; }
    is (AluOpcSrl) { op := OpShift; src0 := Src0Tos; src1 := Src1Nos; xtra := SrlX; wr := true.B; dITos := 3.U; }
    is (AluOpcSra) { op := OpShift; src0 := Src0Tos; src1 := Src1Nos; xtra := SraX; wr := true.B; dITos := 3.U; }

    /* Bits - XLEN width */

    is (AluOpcAnd) { op := OpBits; src0 := Src0Tos; src1 := Src1Nos; xtra := AndX; wr := true.B; dITos := 3.U; }
    is (AluOpcOr)  { op := OpBits; src0 := Src0Tos; src1 := Src1Nos; xtra := OrX;  wr := true.B; dITos := 3.U; }
    is (AluOpcXor) { op := OpBits; src0 := Src0Tos; src1 := Src1Nos; xtra := XorX; wr := true.B; dITos := 3.U; }
    is (AluOpcNot) { src0 := Src0Tos; src0N := true.B; wr := true.B; }

    /* Comparisons - XLEN width */

    is (AluOpcSlt)  { op := OpSlt;  src0 := Src0Tos; src1 := Src1Nos; src1N := true.B; wr := true.B; dITos := 3.U; }
    is (AluOpcSltu) { op := OpSltu; src0 := Src0Tos; src1 := Src1Nos; src1N := true.B; wr := true.B; dITos := 3.U; }
    is (AluOpcSeq)  { op := OpBits; src0 := Src0Tos; src1 := Src1Nos; xtra := XorZX; wr := true.B; dITos := 3.U; }

    /* Integer comparisons with remote ALU tos (PREVIOUS cycle value) XLEN width */

    is (AluOpcSltA0)  { op := OpSlt;  src0 := Src0Tos; src1 := Src1Alu; src1N := true.B; xtra := AluX0.asUInt; wr := true.B; }
    is (AluOpcSltA1)  { op := OpSlt;  src0 := Src0Tos; src1 := Src1Alu; src1N := true.B; xtra := AluX1.asUInt; wr := true.B; }
    is (AluOpcSltA2)  { op := OpSlt;  src0 := Src0Tos; src1 := Src1Alu; src1N := true.B; xtra := AluX2.asUInt; wr := true.B; }
    is (AluOpcSltA3)  { op := OpSlt;  src0 := Src0Tos; src1 := Src1Alu; src1N := true.B; xtra := AluX3.asUInt; wr := true.B; }
    is (AluOpcSltuA0) { op := OpSltu; src0 := Src0Tos; src1 := Src1Alu; src1N := true.B; xtra := AluX0.asUInt; wr := true.B; }
    is (AluOpcSltuA1) { op := OpSltu; src0 := Src0Tos; src1 := Src1Alu; src1N := true.B; xtra := AluX1.asUInt; wr := true.B; }
    is (AluOpcSltuA2) { op := OpSltu; src0 := Src0Tos; src1 := Src1Alu; src1N := true.B; xtra := AluX2.asUInt; wr := true.B; }
    is (AluOpcSltuA3) { op := OpSltu; src0 := Src0Tos; src1 := Src1Alu; src1N := true.B; xtra := AluX3.asUInt; wr := true.B; }
    is (AluOpcSeqA0)  { op := OpBits; src0 := Src0Tos; src1 := Src1Alu; xtra := XorZX; wr := true.B }
    is (AluOpcSeqA1)  { op := OpBits; src0 := Src0Tos; src1 := Src1Alu; xtra := XorZX; wr := true.B }
    is (AluOpcSeqA2)  { op := OpBits; src0 := Src0Tos; src1 := Src1Alu; xtra := XorZX; wr := true.B }
    is (AluOpcSeqA3)  { op := OpBits; src0 := Src0Tos; src1 := Src1Alu; xtra := XorZX; wr := true.B }
  }

  io.src0 := src0; io.src0N := src0N;
  io.src1 := src1; io.src1N := src1N;
  io.op := op
  io.wr := wr
  io.dITos := dITos
  io.xtra := xtra
}
