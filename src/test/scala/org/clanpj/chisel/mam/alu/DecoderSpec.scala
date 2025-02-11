package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class DecoderSpec extends AnyFreeSpec with Matchers {

  "Alu Decoder should decode opcodes" in {
    simulate(new Decoder) { dut =>
      dut.io.opc.poke(0.U)
      dut.clock.step()
      dut.io.unit.expect(0.U)

      for (opc <- 0x00 to 0xff) {
        val opcEnumOpt = try {
          Some(AluOpcode(opc))
        } catch {
          case _: NoSuchElementException => None
        }
        val (inv, spec) = opcEnumOpt match {
          case None => (true, None)
          case Some(opcEnum) => (false, Some(DecoderSpec.spec(opcEnum)))
        }
        println(f"opc: 0x$opc%2x spec inv / spec = $inv / $spec")
        assert(inv == spec.isEmpty)

        dut.io.opc.poke(opc.U)
        dut.clock.step()
        spec match {
          case None => {
            assert(inv)
            dut.io.inv.expect(true.B)
          }
          case Some((nop, unit, opOpt, genOpt, binOpt, mamOpt, wrOpt)) => {
            assert(!inv)
            dut.io.nop.expect(nop)
            dut.io.unit.expect(unit)
            opOpt.foreach((op) => dut.io.op.expect(op))
            genOpt.foreach((gen) => dut.io.gen.expect(gen))
            binOpt.foreach((bin) => dut.io.bin.expect(bin))
            mamOpt.foreach((mam) => dut.io.mam.expect(mam))
            wrOpt.foreach((wr) => dut.io.wr.expect(wr))
            dut.io.inv.expect(false.B)
          }
        }
      }
    }
  }
}

object DecoderSpec {
  import AluOpcode._
  import AluUnit._
  import AluGenUnit._
  import AluMamGenUnit._
  import IndexOp._
  import AdderOp._
  import BitsOp._
  import ExtOp._
  import ShiftOp._

  def N(b:  Boolean) = b.B

  def U1(u: AluUnit) = u.id.U(3.W)
  def U2(u: AluGenUnit) = u.id.U(3.W)
  def U3(u: AluMamGenUnit) = u.id.U(3.W)

  def O1(u: IndexOp) = Some(u.id.U(2.W))
  def O2(u: AdderOp) = Some(u.id.U(2.W))
  def O3(u: BitsOp) = Some(u.id.U(2.W))
  def O4(u: ExtOp) = Some(u.id.U(2.W))
  def O5(u: ShiftOp) = Some(u.id.U(2.W))

  def G(b: Boolean) = Some(b.B)

  def B(b: Boolean) = Some(b.B)

  def M(b: Boolean) = Some(b.B)
  
  def W(b: Boolean) = Some(b.B)
  
  val spec = Map(
    /////////////////////////////////////////////////////////////////

    // Unary ops dtos == 0
    // src0 == 0, src1 == TOS

    AluOpcNop ->      (N(true), U1(UnitZero), None, None, None, None, None),

    AluOpcNeg ->      (N(false), U1(UnitAdd), O2(AdderSub), G(false), B(false), M(false), W(false)),

    AluOpcNot ->      (N(false), U1(UnitBits), O3(BitsXor), G(false), B(false), M(false), W(false)),

    /* Register write non-popping */

    AluOpcWrR0 ->     (N(false), U1(UnitTos), O1(Index0), G(false), B(false), M(false), W(true)),
    AluOpcWrR1 ->     (N(false), U1(UnitTos), O1(Index1), G(false), B(false), M(false), W(true)),
    AluOpcWrR2 ->     (N(false), U1(UnitTos), O1(Index2), G(false), B(false), M(false), W(true)),
    AluOpcWrR3 ->     (N(false), U1(UnitTos), O1(Index3), G(false), B(false), M(false), W(true)),

    /* Extensions and Truncations */

    AluOpcExtb ->     (N(false), U1(UnitExt), O4(ExtExtb), G(false), B(false), M(false), W(false)),
    AluOpcExtub ->    (N(false), U1(UnitExt), O4(ExtExtub), G(false), B(false), M(false), W(false)),
    AluOpcExth ->     (N(false), U1(UnitExt), O4(ExtExtw), G(false), B(false), M(false), W(false)),
    AluOpcExtuh ->    (N(false), U1(UnitExt), O4(ExtExtuw), G(false), B(false), M(false), W(false)),

    /////////////////////////////////////////////////////////////////

    // Binary ops dtos == -1
    // src0/lhs/x == NOS, src1/rhs/y == TOS

    /* Integer Add/Slt XLEN width */

    AluOpcAdd ->      (N(false), U1(UnitAdd), O2(AdderAdd), G(false), B(true), M(false), W(false)),
    AluOpcSub ->      (N(false), U1(UnitAdd), O2(AdderSub), G(false), B(true), M(false), W(false)),
    AluOpcSlt ->      (N(false), U1(UnitAdd), O2(AdderSlt), G(false), B(true), M(false), W(false)),
    AluOpcSltu ->     (N(false), U1(UnitAdd), O2(AdderSltu), G(false), B(true), M(false), W(false)),

    /* Bits - XLEN width */

    AluOpcAnd ->      (N(false), U1(UnitBits), O3(BitsAnd), G(false), B(true), M(false), W(false)),
    AluOpcOr ->       (N(false), U1(UnitBits), O3(BitsOr), G(false), B(true), M(false), W(false)),
    AluOpcXor ->      (N(false), U1(UnitBits), O3(BitsXor), G(false), B(true), M(false), W(false)),
    AluOpcSeq ->      (N(false), U1(UnitBits), O3(BitsSeq), G(false), B(true), M(false), W(false)),

    /* Shift - XLEN width */

    AluOpcSll ->      (N(false), U1(UnitShift), O5(ShiftSll), G(false), B(true), M(false), W(false)),
    AluOpcSrl ->      (N(false), U1(UnitShift), O5(ShiftSrl), G(false), B(true), M(false), W(false)),
    AluOpcSra ->      (N(false), U1(UnitShift), O5(ShiftSra), G(false), B(true), M(false), W(false)),

    /* Select using remote alu condition (LAST cycle value) */

    AluOpcSelzA0 ->   (N(false), U1(UnitSelz), O1(Index0), G(false), B(true), M(false), W(false)),
    AluOpcSelzA1 ->   (N(false), U1(UnitSelz), O1(Index1), G(false), B(true), M(false), W(false)),
    AluOpcSelzA2 ->   (N(false), U1(UnitSelz), O1(Index2), G(false), B(true), M(false), W(false)),
    AluOpcSelzA3 ->   (N(false), U1(UnitSelz), O1(Index3), G(false), B(true), M(false), W(false)),
    AluOpcSelnzA0 ->  (N(false), U1(UnitSelnz), O1(Index0), G(false), B(true), M(false), W(false)),
    AluOpcSelnzA1 ->  (N(false), U1(UnitSelnz), O1(Index1), G(false), B(true), M(false), W(false)),
    AluOpcSelnzA2 ->  (N(false), U1(UnitSelnz), O1(Index2), G(false), B(true), M(false), W(false)),
    AluOpcSelnzA3 ->  (N(false), U1(UnitSelnz), O1(Index3), G(false), B(true), M(false), W(false)),

    ////////////////////////////////////////////////////////////////////

    // Generating ops - dtos == 1

    /* Stack read */
    AluOpcRdS0 ->     (N(false), U2(UnitStack), O1(Index0), G(true), None, M(false), W(false)),
    AluOpcRdS1 ->     (N(false), U2(UnitStack), O1(Index1), G(true), None, M(false), W(false)),
    AluOpcRdS2 ->     (N(false), U2(UnitStack), O1(Index2), G(true), None, M(false), W(false)),
    AluOpcRdS3 ->     (N(false), U2(UnitStack), O1(Index3), G(true), None, M(false), W(false)),

    /* Register read */
    AluOpcRdR0 ->     (N(false), U2(UnitReg), O1(Index0), G(true), None, M(false), W(false)),
    AluOpcRdR1 ->     (N(false), U2(UnitReg), O1(Index1), G(true), None, M(false), W(false)),
    AluOpcRdR2 ->     (N(false), U2(UnitReg), O1(Index2), G(true), None, M(false), W(false)),
    AluOpcRdR3 ->     (N(false), U2(UnitReg), O1(Index3), G(true), None, M(false), W(false)),

    /* Remote alu TOS access (LAST cycle result) */
    AluOpcRdA0 ->     (N(false), U3(UnitAlu), O1(Index0), G(true), None, M(true), W(false)),
    AluOpcRdA1 ->     (N(false), U3(UnitAlu), O1(Index1), G(true), None, M(true), W(false)),
    AluOpcRdA2 ->     (N(false), U3(UnitAlu), O1(Index2), G(true), None, M(true), W(false)),
    AluOpcRdA3 ->     (N(false), U3(UnitAlu), O1(Index3), G(true), None, M(true), W(false)),

    /* (Remote) Mem unit value access (THIS cycle result - if it's ready, otherwise stall) */
    // AluOpcRdM0v0 ->   (N(false), U3(UnitMem0), None, G(true), None, M(true), W(false)),
    // AluOpcRdM0v1 ->   (N(false), U3(UnitMem0), None, G(true), None, M(true), W(false)),
    // AluOpcRdM1v0 ->   (N(false), U3(UnitMem0), None, G(true), None, M(true), W(false)),
    // AluOpcRdM1v1 ->   (N(false), U3(UnitMem0), None, G(true), None, M(true), W(false)),
    // TODO addr's too?

    /* Icache constants */

    // Non-overlapping
    AluOpcConb0 ->    (N(false), U3(UnitConb0), O1(Index0), G(true), None, M(true), W(false)),
    AluOpcConb1 ->    (N(false), U3(UnitConb0), O1(Index1), G(true), None, M(true), W(false)),
    AluOpcConb2 ->    (N(false), U3(UnitConb0), O1(Index2), G(true), None, M(true), W(false)),
    AluOpcConb3 ->    (N(false), U3(UnitConb0), O1(Index3), G(true), None, M(true), W(false)),

    // Overlapping b/h/w
    AluOpcConb4 ->    (N(false), U3(UnitConb), O1(Index0), G(true), None, M(true), W(false)),
    AluOpcConb5 ->    (N(false), U3(UnitConb), O1(Index1), G(true), None, M(true), W(false)),
    AluOpcConb6 ->    (N(false), U3(UnitConb), O1(Index2), G(true), None, M(true), W(false)),
    AluOpcConb7 ->    (N(false), U3(UnitConb), O1(Index3), G(true), None, M(true), W(false)),
    AluOpcConh0 ->    (N(false), U3(UnitConh), O1(Index0), G(true), None, M(true), W(false)),
    AluOpcConh1 ->    (N(false), U3(UnitConh), O1(Index1), G(true), None, M(true), W(false)),
    AluOpcConh2 ->    (N(false), U3(UnitConh), O1(Index2), G(true), None, M(true), W(false)),
    AluOpcConh3 ->    (N(false), U3(UnitConh), O1(Index3), G(true), None, M(true), W(false)),
    AluOpcConw0 ->    (N(false), U3(UnitConw), O1(Index0), G(true), None, M(true), W(false)),
    AluOpcConw1 ->    (N(false), U3(UnitConw), O1(Index1), G(true), None, M(true), W(false)),
    AluOpcConw2 ->    (N(false), U3(UnitConw), O1(Index2), G(true), None, M(true), W(false)),
    AluOpcConw3 ->    (N(false), U3(UnitConw), O1(Index3), G(true), None, M(true), W(false)),
  );
}
