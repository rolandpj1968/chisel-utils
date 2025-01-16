package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

object AluOpcode extends Enumeration {

  /////////////////////////////////////////////////////////////////

  // Unary ops dtos == 0
  // src0 == 0, src1 == TOS

  val AluOpcNop =      Value(0x00)

  val AluOpcNeg =      Value(0x05)

  val AluOpcNot =      Value(0x0a)

  /* Register write non-popping */

  val AluOpcWrR0 =     Value(0x10)
  val AluOpcWrR1 =     Value(0x11)
  val AluOpcWrR2 =     Value(0x12)
  val AluOpcWrR3 =     Value(0x13)

  /* Extensions and Truncations */

  val AluOpcExtb =     Value(0x14)
  val AluOpcExtub =    Value(0x15)
  val AluOpcExth =     Value(0x16)
  val AluOpcExtuh =    Value(0x17)

  /////////////////////////////////////////////////////////////////

  // Binary ops dtos == -1
  // src0/lhs/x == NOS, src1/rhs/y == TOS

  /* Integer Add/Slt XLEN width */

  val AluOpcAdd =      Value(0x44)
  val AluOpcSub =      Value(0x45)
  val AluOpcSlt =      Value(0x46)
  val AluOpcSltu =     Value(0x47)

  /* Bits - XLEN width */

  val AluOpcAnd =      Value(0x48)
  val AluOpcOr =       Value(0x49)
  val AluOpcXor =      Value(0x4a)
  val AluOpcSeq =      Value(0x4b)

  /* Shift - XLEN width */

  val AluOpcSll =      Value(0x4c)
  val AluOpcSrl =      Value(0x4d)
  val AluOpcSra =      Value(0x4e)

  /* Select using remote alu condition (LAST cycle value) */

  val AluOpcSelzA0 =   Value(0x58)
  val AluOpcSelzA1 =   Value(0x59)
  val AluOpcSelzA2 =   Value(0x5a)
  val AluOpcSelzA3 =   Value(0x5b)
  val AluOpcSelnzA0 =  Value(0x5c)
  val AluOpcSelnzA1 =  Value(0x5d)
  val AluOpcSelnzA2 =  Value(0x5e)
  val AluOpcSelnzA3 =  Value(0x5f)

  ////////////////////////////////////////////////////////////////////

  // Generating ops - dtos == 1

  // Local

  /* Stack read */
  val AluOpcRdS0 =     Value(0x80)
  val AluOpcRdS1 =     Value(0x81)
  val AluOpcRdS2 =     Value(0x82)
  val AluOpcRdS3 =     Value(0x83)

  /* Register read */
  val AluOpcRdR0 =     Value(0x84)
  val AluOpcRdR1 =     Value(0x85)
  val AluOpcRdR2 =     Value(0x86)
  val AluOpcRdR3 =     Value(0x87)

  // Remote

  /* Remote alu TOS access (LAST cycle result) */
  val AluOpcRdA0 =     Value(0xa0)
  val AluOpcRdA1 =     Value(0xa1)
  val AluOpcRdA2 =     Value(0xa2)
  val AluOpcRdA3 =     Value(0xa3)

  /* (Remote) Mem unit value access (THIS cycle result - if it's ready, otherwise stall) */
  // val AluOpcRdM0v0 =   Value(0xa4)
  // val AluOpcRdM0v1 =   Value(0xa5)
  // val AluOpcRdM1v0 =   Value(0xa6)
  // val AluOpcRdM1v1 =   Value(0xa7)
  // TODO addr's too?

  /* Icache constants */

  // Non-overlapping
  val AluOpcConb0 =    Value(0xb0)
  val AluOpcConb1 =    Value(0xb1)
  val AluOpcConb2 =    Value(0xb2)
  val AluOpcConb3 =    Value(0xb3)

  // Overlapping b/h/w
  val AluOpcConb4 =    Value(0xb4)
  val AluOpcConb5 =    Value(0xb5)
  val AluOpcConb6 =    Value(0xb6)
  val AluOpcConb7 =    Value(0xb7)
  val AluOpcConh0 =    Value(0xb8)
  val AluOpcConh1 =    Value(0xb9)
  val AluOpcConh2 =    Value(0xba)
  val AluOpcConh3 =    Value(0xbb)
  val AluOpcConw0 =    Value(0xbc)
  val AluOpcConw1 =    Value(0xbd)
  val AluOpcConw2 =    Value(0xbe)
  val AluOpcConw3 =    Value(0xbf)
}

object AluUnit extends Enumeration {
  type AluUnit = Value
  val UnitZero = Value(0x0)
  val UnitAdd = Value(0x1)
  val UnitBits = Value(0x2)
  val UnitShift = Value(0x3)
  val UnitTos = Value(0x4)
  val UnitExt = Value(0x5)
  val UnitSelz = Value(0x6)
  val UnitSelnz = Value(0x7)
}

object AluGenUnit extends Enumeration {
  type AluGenUnit = Value
  val UnitStack = Value(0x0)
  val UnitReg = Value(0x1)
}

object AluMamGenUnit extends Enumeration {
  type AluMamGenUnit = Value
  val UnitAlu = Value(0x0)
  val UnitMem0 = Value(0x1)
  val UnitMem1 = Value(0x2)
  val UnitConb0 = Value(0x4)
  val UnitConb = Value(0x5)
  val UnitConh = Value(0x6)
  val UnitConw = Value(0x7)
}

object IndexOp extends Enumeration {
  type IndexOp = Value
  val Index0 = Value(0x0)
  val Index1 = Value(0x1)
  val Index2 = Value(0x2)
  val Index3 = Value(0x3)
}

object AdderOp extends Enumeration {
  type AdderOp = Value
  val AdderAdd = Value(0x0)
  val AdderSub = Value(0x1)
  val AdderSlt = Value(0x2)
  val AdderSltu = Value(0x3)
}

object BitsOp extends Enumeration {
  type BitsOp = Value
  val BitsAnd = Value(0x0)
  val BitsOr = Value(0x1)
  val BitsXor = Value(0x2)
  val BitsSeq = Value(0x3)
}

object ExtOp extends Enumeration {
  type ExtOp = Value
  val ExtExtb = Value(0x0)
  val ExtExtub = Value(0x1)
  val ExtExtw = Value(0x2)
  val ExtExtuw = Value(0x3)
}

object ShiftOp extends Enumeration {
  type ShiftOp = Value
  val ShiftSll = Value(0x0)
  val ShiftSrl = Value(0x1)
  val ShiftSra = Value(0x2)
}

class Decoder extends Module {

  val io = IO(new Bundle {
    val opc = Input(UInt(8.W))

    val inv = Output(Bool()) // invalid opc
    val nop = Output(Bool())
    val unit = Output(UInt(3.W))
    val op = Output(UInt(2.W))
    val gen = Output(Bool()) // === generating op, otherwise arithmetic
    val bin = Output(Bool()) // === binary op, otherwise unary (only valid for arithmetic ops)
    val mam = Output(Bool()) // === generated from mother ship (only valid for generating ops)
    val wr = Output(Bool()) // reg write - reg number in "op"
  })

  val inv = Wire(Bool())
  val nop = Wire(Bool())
  val unit = Wire(UInt(3.W));
  val op = Wire(UInt(2.W))
  val gen = Wire(Bool())
  val bin = Wire(Bool())
  val mam = Wire(Bool())
  val wr = Wire(Bool())

  inv := false.B
  nop := false.B
  unit := 0.U
  op := 0.U
  gen := false.B
  bin := false.B
  mam := false.B
  wr := false.B

  nop := (~(io.opc.orR)).asBool
  unit := io.opc(4,2)
  op := io.opc(1,0)
  gen := io.opc(7).asBool
  bin := io.opc(6).asBool
  mam := gen && io.opc(6,5).orR
  wr := !(gen || bin) && (unit === AluUnit.UnitTos.id.U)

  val genValid = Wire(Bool())
  genValid := (io.opc(6,3) === 0.U) || (io.opc(6,2) === 8.U) || (io.opc(6,4) === 3.U)

  inv := !genValid

  io.inv := inv
  io.nop := nop
  io.unit := unit
  io.op := op
  io.gen := gen
  io.bin := bin
  io.mam := mam
  io.wr := wr
}

/**
 * Generate Verilog sources and save it in file Decoder.sv
 */
// object Decoder extends App {
//   ChiselStage.emitSystemVerilogFile(
//     new Decoder,
//     firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
//   )
// }
