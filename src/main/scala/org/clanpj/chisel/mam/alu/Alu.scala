package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.util._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

import org.clanpj.chisel.mam.MamSrc;

class Alu(n: Int) extends Module {
  import AluUnit._
  import AluGenUnit._

  // Interface with MAM mothership
  val io = IO(new Bundle {
    val en = Input(Bool())
    val opc = Input(UInt(8.W))

    val mamREn = Output(Bool())
    val mamUnit = Output(UInt(3.W))
    val mamOp = Output(UInt(2.W))
    val mamV = Input(UInt(n.W))

    val nTosV = Output(UInt(n.W))

    val stall = Output(Bool())

    val trap = Output(Bool())
  })

  val decoder = Module(new Decoder)

  decoder.io.opc := 0.U
  when (io.en) {
    decoder.io.opc := io.opc
  }

  io.trap := decoder.io.inv
  io.stall := false.B

  val en = !(decoder.io.nop || decoder.io.inv)
  val genEn = en && decoder.io.gen
  val aluGenEn = genEn && !decoder.io.mam
  val mamGenEn = genEn && decoder.io.mam

  val unitOH = UIntToOH(decoder.io.unit)

  val regfile = Module(new RegFile(n, 2/*^2*/))
  regfile.io.en := aluGenEn && unitOH(1 << UnitReg.id)
  regfile.io.wEn := en && decoder.io.wr
  regfile.io.i := decoder.io.op

  val stack = Module(new Stack(n, 2/*^2*/))
  stack.io.en := en
  stack.io.rEn := aluGenEn && unitOH(1 << UnitStack.id)
  stack.io.i := decoder.io.op
  stack.io.wEn := true.B // TODO remove
  stack.io.dITos := Mux(decoder.io.gen, 0x1.U, Mux(decoder.io.bin, 0x0.U, ((1<<2)-1).U))

  io.mamREn := mamGenEn
  io.mamUnit := decoder.io.unit
  io.mamOp := decoder.io.op

  val vGen = Wire(UInt(n.W))
  vGen := regfile.io.v | stack.io.v | io.mamV

  val src0 = Wire(UInt(n.W))
  src0 := stack.io.nosV
  val src1 = Wire(UInt(n.W))
  src1 := stack.io.tosV

  val arithGenEn = en && !decoder.io.gen

  val adderUnit = Module(new AdderUnit(n))
  adderUnit.io.en := arithGenEn && unitOH(1 << UnitAdd.id)
  adderUnit.io.op := decoder.io.op
  adderUnit.io.src0 := src0
  adderUnit.io.src1 := src1
  adderUnit.io.bin := decoder.io.bin

  val bitsUnit = Module(new BitsUnit(n))
  bitsUnit.io.en := arithGenEn && unitOH(1 << UnitBits.id)
  bitsUnit.io.op := decoder.io.op
  bitsUnit.io.src0 := src0
  bitsUnit.io.src1 := src1
  bitsUnit.io.bin := decoder.io.bin

  val vArith = Wire(UInt(n.W))
  vArith := adderUnit.io.v | bitsUnit.io.v

  val v = Wire(UInt(n.W))
  v := vGen | vArith

  regfile.io.wV := v
  stack.io.nTosV := v
  io.nTosV := v
}

/**
 * Generate Verilog sources and save it in file Alu.sv
 */
object Alu extends App {
  ChiselStage.emitSystemVerilogFile(
    new Alu(32),
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
  )
}
