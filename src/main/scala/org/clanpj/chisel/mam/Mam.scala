package org.clanpj.chisel.mam

import chisel3._
import chisel3.util._

// TODO make values identical with corresponding AluSrc1 etc.
object MamSrc extends ChiselEnum {
  val SrcAlu, SrcConB, SrcConH, SrcConW = Value
}


