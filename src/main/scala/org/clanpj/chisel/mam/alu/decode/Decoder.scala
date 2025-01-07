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

  /* Extensions and Truncations */

  val AluOpcExtb =     Value(0x30.U)
  val AluOpcExtub =    Value(0x31.U)
  val AluOpcExth =     Value(0x32.U)
  val AluOpcExtuh =    Value(0x33.U)

  val AluOpcExtw =     Value(0x34.U)
  val AluOpcExtuw =    Value(0x35.U)

  /* Stack read */
  val AluOpcRdS0 =     Value(0x38.U)
  val AluOpcRdS1 =     Value(0x39.U)
  val AluOpcRdS2 =     Value(0x3a.U)
  val AluOpcRdS3 =     Value(0x3b.U)

  /* Register read */
  val AluOpcRdR0 =     Value(0x3c.U)
  val AluOpcRdR1 =     Value(0x3d.U)
  val AluOpcRdR2 =     Value(0x3e.U)
  val AluOpcRdR3 =     Value(0x3f.U)

  /* Register write popping */
  val AluOpcWpR0 =     Value(0x40.U)
  val AluOpcWpR1 =     Value(0x41.U)
  val AluOpcWpR2 =     Value(0x42.U)
  val AluOpcWpR3 =     Value(0x43.U)

  /* Register write non-popping */
  val AluOpcWrR0 =     Value(0x44.U)
  val AluOpcWrR1 =     Value(0x45.U)
  val AluOpcWrR2 =     Value(0x46.U)
  val AluOpcWrR3 =     Value(0x47.U)

  /* Remote alu TOS access (LAST cycle result) */
  val AluOpcRdA0 =     Value(0x48.U)
  val AluOpcRdA1 =     Value(0x49.U)
  val AluOpcRdA2 =     Value(0x4a.U)
  val AluOpcRdA3 =     Value(0x4b.U)

  /* Remote alu TOS access (THIS cycle result forwarded) */
  val AluOpcFwA0 =     Value(0x4c.U)
  val AluOpcFwA1 =     Value(0x4d.U)
  val AluOpcFwA2 =     Value(0x4e.U)
  val AluOpcFwA3 =     Value(0x4f.U)

  /* (Remote) Mem unit value access (THIS cycle result - if it's ready, otherwise stall) */
  val AluOpcRdM0v0 =   Value(0x50.U)
  val AluOpcRdM0v1 =   Value(0x51.U)
  val AluOpcRdM1v0 =   Value(0x52.U)
  val AluOpcRdM1v1 =   Value(0x53.U)

  val AluOpcFF   =     Value(0xff.U) // Enforce 8-bit width
}

object AluUnit extends ChiselEnum {
  val UnitNone, UnitSrc1, UnitAdd, UnitShift, UnitBits, UnitExt, UnitInv = Value
}

object AdderOp extends ChiselEnum {
  val AddOp, SltOp, SltuOp = Value
}

object ShiftOp extends ChiselEnum {
  val SllOp, SrlOp, SraOp = Value
}

// XorZ for seq*
object BitsOp extends ChiselEnum {
  val AndOp, OrOp, XorOp, XorZOp = Value
}

object AluSrc1 extends ChiselEnum {
  val Src1None, /*Src0Tos, needed?*/ Src1Nos, Src1Lit, Src1Alu, Src0Stk, Src0Reg, Src0Fwd, Src0Con = Value
}

object Src1LitX extends ChiselEnum {
  val LitX0, LitX1, LitX2, LitX4 = Value
}

object Src1AluX extends ChiselEnum {
  val AluX0, AluX1, AluX2, AluX3 = Value
}

class Decoder extends Module {
  import AluOpcode._
  import AluUnit._
  import AdderOp._
  import ShiftOp._
  import BitsOp._
  import AluSrc1._
  import Src1LitX._
  import Src1AluX._

  val io = IO(new Bundle {
    val opc = Input(AluOpcode())
    val unit = Output(AluUnit())
    val op = Output(UInt(5.W))
    val src0N = Output(Bool())
    val src1 = Output(AluSrc1())
    val src1N = Output(Bool())
    val src1X = Output(UInt(3.W))
    val fwd = Output(Bool())
    val wr = Output(Bool())
    val dITos = Output(UInt(2.W))
  })

  val unit = UnitInv; val op = 0.U(5.W)
  val src0N = false.B;
  val src1 = Src1None; val src1N = false.B; val src1X = 0.U;
  val fwd = false.B
  val wr = false.B
  val dITos = 0.U(2.W)

  switch (io.opc) {
    is (AluOpcNop)   { unit := UnitNone; }
    is (AluOpcDrop)  { unit := UnitNone; dITos := 3.U; }
    is (AluOpcAdd1)  { unit := UnitAdd; op := AddOp; src1 := Src1Lit;                  src1X := LitX1; wr := true.B; }
    is (AluOpcSub1)  { unit := UnitAdd; op := AddOp; src1 := Src1Lit; src1N := true.B; src1X := LitX1; wr := true.B; }
    is (AluOpcAdd2)  { unit := UnitAdd; op := AddOp; src1 := Src1Lit;                  src1X := LitX2; wr := true.B; }
    is (AluOpcSub2)  { unit := UnitAdd; op := AddOp; src1 := Src1Lit; src1N := true.B; src1X := LitX2; wr := true.B; }
    is (AluOpcAdd4)  { unit := UnitAdd; op := AddOp; src1 := Src1Lit;                  src1X := LitX4; wr := true.B; }
    is (AluOpcSub4)  { unit := UnitAdd; op := AddOp; src1 := Src1Lit; src1N := true.B; src1X := LitX4; wr := true.B; }

    /* Integer Arithmetic XLEN width */

    is (AluOpcAdd)   { unit := UnitAdd; op := AddOp;                  src1 := Src1Nos;                        wr := true.B; dITos := 3.U; }
    is (AluOpcSub)   { unit := UnitAdd; op := AddOp;                ; src1 := Src1Nos; src1N := true.B;       wr := true.B; dITos := 3.U; }
    is (AluOpcRsub)  { unit := UnitAdd; op := AddOp; src0N := true.B; src1 := Src1Nos;                        wr := true.B; dITos := 3.U; }
    is (AluOpcNeg)   { unit := UnitAdd; op := AddOp; src0N := true.B; src1 := Src1Lit; src1X := LitX0; wr := true.B; }

    /* Integer add with remote ALU tos (PREVIOUS cycle value) XLEN width */

    is (AluOpcAddA0) { unit := UnitAdd; op := AddOp;                  src1 := Src1Alu; src1X := AluX0; wr := true.B; }
    is (AluOpcAddA1) { unit := UnitAdd; op := AddOp;                  src1 := Src1Alu; src1X := AluX1; wr := true.B; }
    is (AluOpcAddA2) { unit := UnitAdd; op := AddOp;                  src1 := Src1Alu; src1X := AluX2; wr := true.B; }
    is (AluOpcAddA3) { unit := UnitAdd; op := AddOp;                  src1 := Src1Alu; src1X := AluX3; wr := true.B; }

    /* Shift Binary - XLEN width */

    is (AluOpcSll) { unit := UnitShift; op := SllOp; src1 := Src1Nos; wr := true.B; dITos := 3.U; }
    is (AluOpcSrl) { unit := UnitShift; op := SrlOp; src1 := Src1Nos; wr := true.B; dITos := 3.U; }
    is (AluOpcSra) { unit := UnitShift; op := SraOp; src1 := Src1Nos; wr := true.B; dITos := 3.U; }

    /* Bits - XLEN width */

    is (AluOpcAnd) { unit := UnitBits; op := AndOp; src1 := Src1Nos; wr := true.B; dITos := 3.U; }
    is (AluOpcOr)  { unit := UnitBits; op := OrOp;  src1 := Src1Nos; wr := true.B; dITos := 3.U; }
    is (AluOpcXor) { unit := UnitBits; op := XorOp; src1 := Src1Nos; wr := true.B; dITos := 3.U; }
    is (AluOpcNot) { unit := UnitNone; src0N := true.B; wr := true.B; }

    /* Comparisons - XLEN width */

    is (AluOpcSlt)  { unit := UnitAdd;  op := SltOp;  src1 := Src1Nos; src1N := true.B; wr := true.B; dITos := 3.U; }
    is (AluOpcSltu) { unit := UnitAdd;  op := SltuOp; src1 := Src1Nos; src1N := true.B; wr := true.B; dITos := 3.U; }
    is (AluOpcSeq)  { unit := UnitBits; op := XorZOp; src1 := Src1Nos; wr := true.B; dITos := 3.U; }

    /* Integer comparisons with remote ALU tos (PREVIOUS cycle value) XLEN width */

    is (AluOpcSltA0)  { unit := UnitAdd;  op := SltOp;  src1 := Src1Alu; src1N := true.B; src1X := AluX0; wr := true.B; }
    is (AluOpcSltA1)  { unit := UnitAdd;  op := SltOp;  src1 := Src1Alu; src1N := true.B; src1X := AluX1; wr := true.B; }
    is (AluOpcSltA2)  { unit := UnitAdd;  op := SltOp;  src1 := Src1Alu; src1N := true.B; src1X := AluX2; wr := true.B; }
    is (AluOpcSltA3)  { unit := UnitAdd;  op := SltOp;  src1 := Src1Alu; src1N := true.B; src1X := AluX3; wr := true.B; }
    is (AluOpcSltuA0) { unit := UnitAdd;  op := SltuOp; src1 := Src1Alu; src1N := true.B; src1X := AluX0; wr := true.B; }
    is (AluOpcSltuA1) { unit := UnitAdd;  op := SltuOp; src1 := Src1Alu; src1N := true.B; src1X := AluX1; wr := true.B; }
    is (AluOpcSltuA2) { unit := UnitAdd;  op := SltuOp; src1 := Src1Alu; src1N := true.B; src1X := AluX2; wr := true.B; }
    is (AluOpcSltuA3) { unit := UnitAdd;  op := SltuOp; src1 := Src1Alu; src1N := true.B; src1X := AluX3; wr := true.B; }
    is (AluOpcSeqA0)  { unit := UnitBits; op := XorZOp; src1 := Src1Alu; src1X := AluX0; wr := true.B }
    is (AluOpcSeqA1)  { unit := UnitBits; op := XorZOp; src1 := Src1Alu; src1X := AluX1; wr := true.B }
    is (AluOpcSeqA2)  { unit := UnitBits; op := XorZOp; src1 := Src1Alu; src1X := AluX2; wr := true.B }
    is (AluOpcSeqA3)  { unit := UnitBits; op := XorZOp; src1 := Src1Alu; src1X := AluX3; wr := true.B }
  }

  io.unit := unit; io.op := op;
  io.src0N := src0N;
  io.src1 := src1; io.src1N := src1N; io.src1X := src1X
  io.fwd := fwd
  io.wr := wr
  io.dITos := dITos
}
