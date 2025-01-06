package org.clanpj.chisel.mam.alu.decode

import chisel3._

object AluOpcode extends ChiselEnum {
  val AluOpcNop =      Value(0x00.U)
  val AluOpcDrop =     Value(0x01.U)
  val AluOpcAdd1 =     Value(0x02.U)
  val AluOpcSub1 =     Value(0x03.U)
  val AluOpcAdd2 =     Value(0x04.U)
  val AluOpcSub2 =     Value(0x05.U)
  val AluOpcAdd4 =     Value(0x06.U)
  val AluOpcSub4 =     Value(0x07.U)
}

object AluSrc0 extends ChiselEnum {
  val Src0None, Src0Tos, Src0Stk, Src0Reg, Src0Fwd, Src0Con = Value
}

object AluSrc1 extends ChiselEnum {
  val Src1None, Src1Nos, Src1Lit = Value
}

object AluOp extends ChiselEnum {
  val OpNone, OpAdd, OpShift, OpBits, OpExt = Value
}


