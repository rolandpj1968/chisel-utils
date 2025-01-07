package org.clanpj.chisel.mam.alu.decode

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

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
  val AluOpcOr =       Value(0x15.U)
  val AluOpcXor =      Value(0x16.U)
  val AluOpcNot =      Value(0x17.U)

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

  /* Select using remote alu condition (LAST cycle value) */
  val AluOpcSelzA0 =   Value(0x60.U)
  val AluOpcSelzA1 =   Value(0x61.U)
  val AluOpcSelzA2 =   Value(0x62.U)
  val AluOpcSelzA3 =   Value(0x63.U)
  val AluOpcSelnzA0 =  Value(0x64.U)
  val AluOpcSelnzA1 =  Value(0x65.U)
  val AluOpcSelnzA2 =  Value(0x66.U)
  val AluOpcSelnzA3 =  Value(0x67.U)

  /* Icache constants */

  val AluOpcConb0 =    Value(0x68.U)
  val AluOpcConb1 =    Value(0x69.U)
  val AluOpcConb2 =    Value(0x6a.U)
  val AluOpcConb3 =    Value(0x6b.U)
  val AluOpcConb4 =    Value(0x6c.U)
  val AluOpcConb5 =    Value(0x6d.U)
  val AluOpcConb6 =    Value(0x6e.U)
  val AluOpcConb7 =    Value(0x6f.U)
  val AluOpcConh0 =    Value(0x70.U)
  val AluOpcConh1 =    Value(0x71.U)
  val AluOpcConh2 =    Value(0x72.U)
  val AluOpcConh3 =    Value(0x73.U)
  val AluOpcConh4 =    Value(0x74.U)
  val AluOpcConh5 =    Value(0x75.U)
  val AluOpcConh6 =    Value(0x76.U)
  val AluOpcConh7 =    Value(0x77.U)
  val AluOpcConw0 =    Value(0x78.U)
  val AluOpcConw1 =    Value(0x79.U)
  val AluOpcConw2 =    Value(0x7a.U)
  val AluOpcConw3 =    Value(0x7b.U)
  val AluOpcConw4 =    Value(0x7c.U)
  val AluOpcConw5 =    Value(0x7d.U)
  val AluOpcConw6 =    Value(0x7e.U)
  val AluOpcConw7 =    Value(0x7f.U)

  val AluOpcFF   =     Value(0xff.U) // Enforce 8-bit width
}

// Note: sel[n]z is 3-operand; third operand is ALU number in src1X
object AluUnit extends ChiselEnum {
  val UnitNone, UnitSrc1, UnitAdd, UnitShift, UnitBits, UnitExt, UnitSel, UnitInv = Value
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

object ExtOp extends ChiselEnum {
  val ExtbOp, ExtubOp, ExthOp, ExtuhOp = Value
}

object AluSrc1 extends ChiselEnum {
  val Src1Nos, Src1Lit, Src1Alu, Src1Stk, Src1Reg, Src1Con = Value
}

object Src1LitX extends ChiselEnum {
  val LitX0, LitX1, LitX2, LitX4 = Value
}

object Src1AluX extends ChiselEnum {
  val AluX0, AluX1, AluX2, AluX3 = Value
}

object Src1StkX extends ChiselEnum {
  val StkX0, StkX1, StkX2, StkX3 = Value
}

object Src1RegX extends ChiselEnum {
  val RegX0, RegX1, RegX2, RegX3 = Value
}

class Decoder extends Module {
  import AluOpcode._
  import AluUnit._
  import AdderOp._
  import ShiftOp._
  import BitsOp._
  import ExtOp._
  import AluSrc1._
  import Src1LitX._
  import Src1AluX._
  import Src1StkX._
  import Src1RegX._

  val io = IO(new Bundle {
    val opc = Input(AluOpcode())
    val unit = Output(AluUnit())
    val op = Output(UInt(5.W))
    val src0N = Output(Bool())
    val src1 = Output(AluSrc1())
    val src1N = Output(Bool())
    val src1X = Output(UInt(3.W))
    val fw = Output(Bool()) // forward this cycle result - alu number in src1X
    val res = Output(Bool())
    val wr = Output(Bool()) // reg write - reg number in src1X
    val selz = Output(Bool())
    val dITos = Output(UInt(2.W))
  })

  val unit = Wire(AluUnit()); val op = Wire(UInt(5.W))
  val src0N = Wire(Bool())
  val src1 = Wire(AluSrc1()); val src1N = Wire(Bool()); val src1X = Wire(UInt(3.W));
  val fw = Wire(Bool())
  val res = Wire(Bool())
  val wr = Wire(Bool())
  val selz = Wire(Bool())
  val dITos = Wire(UInt(2.W))

  unit := UnitInv; op := 0.U(5.W)
  src0N := false.B;
  src1 := Src1Nos; src1N := false.B; src1X := 0.U;
  fw := false.B
  res := false.B
  wr := false.B
  selz := false.B
  dITos := 0.U(2.W)

  switch (io.opc) {
    is (AluOpcNop)   { unit := UnitNone; }
    is (AluOpcDrop)  { unit := UnitNone; dITos := 3.U; }
    is (AluOpcAdd1)  { unit := UnitAdd; op := AddOp.asUInt; src1 := Src1Lit;                  src1X := LitX1.asUInt; res := true.B; }
    is (AluOpcSub1)  { unit := UnitAdd; op := AddOp.asUInt; src1 := Src1Lit; src1N := true.B; src1X := LitX1.asUInt; res := true.B; }
    is (AluOpcAdd2)  { unit := UnitAdd; op := AddOp.asUInt; src1 := Src1Lit;                  src1X := LitX2.asUInt; res := true.B; }
    is (AluOpcSub2)  { unit := UnitAdd; op := AddOp.asUInt; src1 := Src1Lit; src1N := true.B; src1X := LitX2.asUInt; res := true.B; }
    is (AluOpcAdd4)  { unit := UnitAdd; op := AddOp.asUInt; src1 := Src1Lit;                  src1X := LitX4.asUInt; res := true.B; }
    is (AluOpcSub4)  { unit := UnitAdd; op := AddOp.asUInt; src1 := Src1Lit; src1N := true.B; src1X := LitX4.asUInt; res := true.B; }

    // /* Integer Arithmetic XLEN width */

    // is (AluOpcAdd)   { unit := UnitAdd; op := AddOp;                  src1 := Src1Nos;                  res := true.B; dITos := 3.U; }
    // is (AluOpcSub)   { unit := UnitAdd; op := AddOp;                ; src1 := Src1Nos; src1N := true.B; res := true.B; dITos := 3.U; }
    // is (AluOpcRsub)  { unit := UnitAdd; op := AddOp; src0N := true.B; src1 := Src1Nos;                  res := true.B; dITos := 3.U; }
    // is (AluOpcNeg)   { unit := UnitAdd; op := AddOp; src0N := true.B; src1 := Src1Lit; src1X := LitX0;  res := true.B; }

    // /* Integer add with remote ALU tos (PREVIOUS cycle value) XLEN width */

    // is (AluOpcAddA0) { unit := UnitAdd; op := AddOp;                  src1 := Src1Alu; src1X := AluX0; res := true.B; }
    // is (AluOpcAddA1) { unit := UnitAdd; op := AddOp;                  src1 := Src1Alu; src1X := AluX1; res := true.B; }
    // is (AluOpcAddA2) { unit := UnitAdd; op := AddOp;                  src1 := Src1Alu; src1X := AluX2; res := true.B; }
    // is (AluOpcAddA3) { unit := UnitAdd; op := AddOp;                  src1 := Src1Alu; src1X := AluX3; res := true.B; }

    // /* Shift Binary - XLEN width */

    // is (AluOpcSll) { unit := UnitShift; op := SllOp; src1 := Src1Nos; res := true.B; dITos := 3.U; }
    // is (AluOpcSrl) { unit := UnitShift; op := SrlOp; src1 := Src1Nos; res := true.B; dITos := 3.U; }
    // is (AluOpcSra) { unit := UnitShift; op := SraOp; src1 := Src1Nos; res := true.B; dITos := 3.U; }

    // /* Bits - XLEN width */

    // is (AluOpcAnd) { unit := UnitBits; op := AndOp; src1 := Src1Nos; res := true.B; dITos := 3.U; }
    // is (AluOpcOr)  { unit := UnitBits; op := OrOp;  src1 := Src1Nos; res := true.B; dITos := 3.U; }
    // is (AluOpcXor) { unit := UnitBits; op := XorOp; src1 := Src1Nos; res := true.B; dITos := 3.U; }
    // is (AluOpcNot) { unit := UnitNone; src0N := true.B; res := true.B; }

    // /* Comparisons - XLEN width */

    // is (AluOpcSlt)  { unit := UnitAdd;  op := SltOp;  src1 := Src1Nos; src1N := true.B; res := true.B; dITos := 3.U; }
    // is (AluOpcSltu) { unit := UnitAdd;  op := SltuOp; src1 := Src1Nos; src1N := true.B; res := true.B; dITos := 3.U; }
    // is (AluOpcSeq)  { unit := UnitBits; op := XorZOp; src1 := Src1Nos; res := true.B; dITos := 3.U; }

    // /* Integer comparisons with remote ALU tos (PREVIOUS cycle value) XLEN width */

    // is (AluOpcSltA0)  { unit := UnitAdd;  op := SltOp;  src1 := Src1Alu; src1N := true.B; src1X := AluX0; res := true.B; }
    // is (AluOpcSltA1)  { unit := UnitAdd;  op := SltOp;  src1 := Src1Alu; src1N := true.B; src1X := AluX1; res := true.B; }
    // is (AluOpcSltA2)  { unit := UnitAdd;  op := SltOp;  src1 := Src1Alu; src1N := true.B; src1X := AluX2; res := true.B; }
    // is (AluOpcSltA3)  { unit := UnitAdd;  op := SltOp;  src1 := Src1Alu; src1N := true.B; src1X := AluX3; res := true.B; }
    // is (AluOpcSltuA0) { unit := UnitAdd;  op := SltuOp; src1 := Src1Alu; src1N := true.B; src1X := AluX0; res := true.B; }
    // is (AluOpcSltuA1) { unit := UnitAdd;  op := SltuOp; src1 := Src1Alu; src1N := true.B; src1X := AluX1; res := true.B; }
    // is (AluOpcSltuA2) { unit := UnitAdd;  op := SltuOp; src1 := Src1Alu; src1N := true.B; src1X := AluX2; res := true.B; }
    // is (AluOpcSltuA3) { unit := UnitAdd;  op := SltuOp; src1 := Src1Alu; src1N := true.B; src1X := AluX3; res := true.B; }
    // is (AluOpcSeqA0)  { unit := UnitBits; op := XorZOp; src1 := Src1Alu; src1X := AluX0; res := true.B }
    // is (AluOpcSeqA1)  { unit := UnitBits; op := XorZOp; src1 := Src1Alu; src1X := AluX1; res := true.B }
    // is (AluOpcSeqA2)  { unit := UnitBits; op := XorZOp; src1 := Src1Alu; src1X := AluX2; res := true.B }
    // is (AluOpcSeqA3)  { unit := UnitBits; op := XorZOp; src1 := Src1Alu; src1X := AluX3; res := true.B }

    // /* Extensions and Truncations */

    // is (AluOpcExtb)   { unit := UnitExt; op := ExtbOp;  res := true.B; }
    // is (AluOpcExtub)  { unit := UnitExt; op := ExtubOp; res := true.B; }
    // is (AluOpcExth)   { unit := UnitExt; op := ExthOp;  res := true.B; }
    // is (AluOpcExtuh)  { unit := UnitExt; op := ExtuhOp; res := true.B; }

    // /* Stack read */
    // is (AluOpcRdS0)   { unit := UnitSrc1; src1 := Src1Stk; src1X := StkX0; res := true.B; dITos := 1.U; }
    // is (AluOpcRdS1)   { unit := UnitSrc1; src1 := Src1Stk; src1X := StkX1; res := true.B; dITos := 1.U; }
    // is (AluOpcRdS2)   { unit := UnitSrc1; src1 := Src1Stk; src1X := StkX2; res := true.B; dITos := 1.U; }
    // is (AluOpcRdS3)   { unit := UnitSrc1; src1 := Src1Stk; src1X := StkX3; res := true.B; dITos := 1.U; }

    // /* Register read */
    // is (AluOpcRdR0)   { unit := UnitSrc1; src1 := Src1Reg; src1X := RegX0; res := true.B; dITos := 1.U; }
    // is (AluOpcRdR1)   { unit := UnitSrc1; src1 := Src1Reg; src1X := RegX1; res := true.B; dITos := 1.U; }
    // is (AluOpcRdR2)   { unit := UnitSrc1; src1 := Src1Reg; src1X := RegX2; res := true.B; dITos := 1.U; }
    // is (AluOpcRdR3)   { unit := UnitSrc1; src1 := Src1Reg; src1X := RegX3; res := true.B; dITos := 1.U; }

    // /* Register write popping */
    // is (AluOpcWpR0)   { unit := UnitNone; wr := true.B; src1X := RegX0; dITos := 3.U }
    // is (AluOpcWpR1)   { unit := UnitNone; wr := true.B; src1X := RegX1; dITos := 3.U }
    // is (AluOpcWpR2)   { unit := UnitNone; wr := true.B; src1X := RegX2; dITos := 3.U }
    // is (AluOpcWpR3)   { unit := UnitNone; wr := true.B; src1X := RegX3; dITos := 3.U }

    // /* Register write non-popping */
    // is (AluOpcWrR0)   { unit := UnitNone; wr := true.B; src1X := RegX0; }
    // is (AluOpcWrR1)   { unit := UnitNone; wr := true.B; src1X := RegX1; }
    // is (AluOpcWrR2)   { unit := UnitNone; wr := true.B; src1X := RegX2; }
    // is (AluOpcWrR3)   { unit := UnitNone; wr := true.B; src1X := RegX3; }

    // /* Remote alu TOS access (LAST cycle result) */
    // is (AluOpcRdA0)   { unit := UnitSrc1; src1 := Src1Alu; src1X := AluX0; res := true.B; dITos := 1.U; }
    // is (AluOpcRdA1)   { unit := UnitSrc1; src1 := Src1Alu; src1X := AluX1; res := true.B; dITos := 1.U; }
    // is (AluOpcRdA2)   { unit := UnitSrc1; src1 := Src1Alu; src1X := AluX2; res := true.B; dITos := 1.U; }
    // is (AluOpcRdA3)   { unit := UnitSrc1; src1 := Src1Alu; src1X := AluX3; res := true.B; dITos := 1.U; }

    // /* Remote alu TOS access (THIS cycle result forwarded) */
    // is (AluOpcFwA0)   { unit := UnitNone; fw := true.B; src1X := AluX0; res := true.B; dITos := 1.U; }
    // is (AluOpcFwA1)   { unit := UnitNone; fw := true.B; src1X := AluX0; res := true.B; dITos := 1.U; }
    // is (AluOpcFwA2)   { unit := UnitNone; fw := true.B; src1X := AluX0; res := true.B; dITos := 1.U; }
    // is (AluOpcFwA3)   { unit := UnitNone; fw := true.B; src1X := AluX0; res := true.B; dITos := 1.U; }

    // /* (Remote) Mem unit value access (THIS cycle result - if it's ready, otherwise stall) */
    // is (AluOpcRdM0v0) {} // TODO
    // is (AluOpcRdM0v1) {}
    // is (AluOpcRdM1v0) {}
    // is (AluOpcRdM1v1) {}

    // /* Select using remote alu condition (LAST cycle value) */
    // is (AluOpcSelzA0)  { unit := UnitSel; selz := true.B;  src1X := AluX0; res := true.B; dITos := 3.U; }
    // is (AluOpcSelzA1)  { unit := UnitSel; selz := true.B;  src1X := AluX1; res := true.B; dITos := 3.U; }
    // is (AluOpcSelzA2)  { unit := UnitSel; selz := true.B;  src1X := AluX2; res := true.B; dITos := 3.U; }
    // is (AluOpcSelzA3)  { unit := UnitSel; selz := true.B;  src1X := AluX3; res := true.B; dITos := 3.U; }
    // is (AluOpcSelnzA0) { unit := UnitSel; selz := false.B; src1X := AluX0; res := true.B; dITos := 3.U; }
    // is (AluOpcSelnzA1) { unit := UnitSel; selz := false.B; src1X := AluX1; res := true.B; dITos := 3.U; }
    // is (AluOpcSelnzA2) { unit := UnitSel; selz := false.B; src1X := AluX2; res := true.B; dITos := 3.U; }
    // is (AluOpcSelnzA3) { unit := UnitSel; selz := false.B; src1X := AluX3; res := true.B; dITos := 3.U; }

    // /* Icache constants */

    // is (AluOpcConb0) {}
    // is (AluOpcConb1) {}
    // is (AluOpcConb2) {}
    // is (AluOpcConb3) {}
    // is (AluOpcConb4) {}
    // is (AluOpcConb5) {}
    // is (AluOpcConb6) {}
    // is (AluOpcConb7) {}
    // is (AluOpcConh0) {}
    // is (AluOpcConh1) {}
    // is (AluOpcConh2) {}
    // is (AluOpcConh3) {}
    // is (AluOpcConh4) {}
    // is (AluOpcConh5) {}
    // is (AluOpcConh6) {}
    // is (AluOpcConh7) {}
    // is (AluOpcConw0) {}
    // is (AluOpcConw1) {}
    // is (AluOpcConw2) {}
    // is (AluOpcConw3) {}
    // is (AluOpcConw4) {}
    // is (AluOpcConw5) {}
    // is (AluOpcConw6) {}
    // is (AluOpcConw7) {}
  }

  io.unit := unit; io.op := op;
  io.src0N := src0N;
  io.src1 := src1; io.src1N := src1N; io.src1X := src1X;
  io.fw := fw
  io.res := res
  io.wr := wr
  io.selz := selz;
  io.dITos := dITos
}

/**
 * Generate Verilog sources and save it in file Decoder.sv
 */
object Decoder extends App {
  ChiselStage.emitSystemVerilogFile(
    new Decoder,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
  )
}
