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
  val AluOpcConb0 =    Value(0xac)
  val AluOpcConb1 =    Value(0xad)
  val AluOpcConb2 =    Value(0xae)
  val AluOpcConb3 =    Value(0xaf)

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
  val UnitConb0 = Value(0x3)
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

// // negate src0 == op[0]; negate src1 == ~(op[0]) && op != 0
// // slt[u] == op & 4
// // Hrmmm, add1, add2, add4, add8
// object AddOp extends ChiselEnum {
//   val AddOp  = Value(0x0)
//   val RSubOp = Value(0x1)
//   val SubOp  = Value(0x2)
//   val NegOp  = Value(0x3)
//   val SltOp  = Value(0x4)
//   val SltuOp = Value(0x6)
// }

// object ShiftOp extends ChiselEnum {
//   val SllOp, SrlOp, SraOp = Value
// }

// // XorZ for seq*
// object BitsOp extends ChiselEnum {
//   val AndOp, OrOp, XorOp, XorZOp = Value
// }

// object ExtOp extends ChiselEnum {
//   val ExtbOp, ExtubOp, ExthOp, ExtuhOp = Value
// }

// object AluSrc1 extends ChiselEnum {
//   val Src1Nos, Src1Lit, Src1Alu, Src1Stk, Src1Reg, Src1Con = Value
// }

// object Src1LitX extends ChiselEnum {
//   val LitX0, LitX1, LitX2, LitX4 = Value
// }

// object Src1AluX extends ChiselEnum {
//   val AluX0, AluX1, AluX2, AluX3 = Value
// }

// object Src1StkX extends ChiselEnum {
//   val StkX0, StkX1, StkX2, StkX3 = Value
// }

// object Src1RegX extends ChiselEnum {
//   val RegX0, RegX1, RegX2, RegX3 = Value
// }

// object Src1ConX extends ChiselEnum {
//   val ConXb0, ConXb1, ConXb2, ConXb3 = Value
//   val ConXb4, ConXb5, ConXb6, ConXb7 = Value
//   val ConXh0, ConXh1, ConXh2, ConXh3 = Value
//   val ConXh4, ConXh5, ConXh6, ConXh7 = Value
//   val ConXw0, ConXw1, ConXw2, ConXw3 = Value
//   val ConXw4, ConXw5, ConXw6, ConXw7 = Value
// }

class Decoder extends Module {
  import AluOpcode._
  // import AluUnit._
  // import AdderOp._
  // import ShiftOp._
  // import BitsOp._
  // import ExtOp._
  // import AluSrc1._
  // import Src1LitX._
  // import Src1AluX._
  // import Src1StkX._
  // import Src1RegX._
  // import Src1ConX._

  val io = IO(new Bundle {
    val opc = Input(UInt(8.W))

    val inv = Output(Bool()) // invalid opc
    val unit = Output(UInt(3.W))
    val op = Output(UInt(2.W))
    val dtosm1 = Output(Bool())  // === binary op
    val dtos1 = Output(Bool())   // === generating op
    val mam = Output(Bool()) // === generated from mother ship
    val wr = Output(Bool()) // reg write - reg number in "op"
  })

  val inv = Wire(Bool())
  val unit = Wire(UInt(3.W));
  val op = Wire(UInt(2.W))
  val dtosm1 = Wire(Bool())
  val dtos1 = Wire(Bool())
  val mam = Wire(Bool())
  val wr = Wire(Bool())

  inv := false.B
  unit := 0.U
  op := 0.U
  dtosm1 := false.B
  dtos1 := false.B
  mam := false.B
  wr := false.B

  unit := io.opc(4,2)
  op := io.opc(1,0)

  io.inv := inv
  io.unit := unit
  io.op := op
  io.dtosm1 := dtosm1
  io.dtos1 := dtos1
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
