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
          case None => assert(inv)
          case Some((unit, opOpt, genOpt, binOpt)) => {
            assert(!inv)
            dut.io.unit.expect(unit)
            opOpt.foreach((op) => dut.io.op.expect(op))
            genOpt.foreach((gen) => dut.io.gen.expect(gen))
            binOpt.foreach((bin) => dut.io.bin.expect(bin))
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

  // def N[T] = None
  // def S[T](v: T) = Some(v)

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
  
  val spec = Map(
    /////////////////////////////////////////////////////////////////

    // Unary ops dtos == 0
    // src0 == 0, src1 == TOS

    AluOpcNop ->      (U1(UnitZero), None, None, None),

    AluOpcNeg ->      (U1(UnitAdd), O2(AdderSub), G(false), B(false)),

    AluOpcNot ->      (U1(UnitBits), O3(BitsXor), G(false), B(false)),

    /* Register write non-popping */

    AluOpcWrR0 ->     (U1(UnitTos), O1(Index0), G(false), B(false)),
    AluOpcWrR1 ->     (U1(UnitTos), O1(Index1), G(false), B(false)),
    AluOpcWrR2 ->     (U1(UnitTos), O1(Index2), G(false), B(false)),
    AluOpcWrR3 ->     (U1(UnitTos), O1(Index3), G(false), B(false)),

    /* Extensions and Truncations */

    AluOpcExtb ->     (U1(UnitExt), O4(ExtExtb), G(false), B(false)),
    AluOpcExtub ->    (U1(UnitExt), O4(ExtExtub), G(false), B(false)),
    AluOpcExth ->     (U1(UnitExt), O4(ExtExtw), G(false), B(false)),
    AluOpcExtuh ->    (U1(UnitExt), O4(ExtExtuw), G(false), B(false)),

    /////////////////////////////////////////////////////////////////

    // Binary ops dtos == -1
    // src0/lhs/x == NOS, src1/rhs/y == TOS

    /* Integer Add/Slt XLEN width */

    AluOpcAdd ->      (U1(UnitAdd), O2(AdderAdd), G(false), B(true)),
    AluOpcSub ->      (U1(UnitAdd), O2(AdderSub), G(false), B(true)),
    AluOpcSlt ->      (U1(UnitAdd), O2(AdderSlt), G(false), B(true)),
    AluOpcSltu ->     (U1(UnitAdd), O2(AdderSltu), G(false), B(true)),

    /* Bits - XLEN width */

    AluOpcAnd ->      (U1(UnitBits), O3(BitsAnd), G(false), B(true)),
    AluOpcOr ->       (U1(UnitBits), O3(BitsOr), G(false), B(true)),
    AluOpcXor ->      (U1(UnitBits), O3(BitsXor), G(false), B(true)),
    AluOpcSeq ->      (U1(UnitBits), O3(BitsSeq), G(false), B(true)),

    /* Shift - XLEN width */

    AluOpcSll ->      (U1(UnitShift), O5(ShiftSll), G(false), B(true)),
    AluOpcSrl ->      (U1(UnitShift), O5(ShiftSrl), G(false), B(true)),
    AluOpcSra ->      (U1(UnitShift), O5(ShiftSra), G(false), B(true)),

    /* Select using remote alu condition (LAST cycle value) */

    AluOpcSelzA0 ->   (U1(UnitSelz), O1(Index0), G(false), B(true)),
    AluOpcSelzA1 ->   (U1(UnitSelz), O1(Index1), G(false), B(true)),
    AluOpcSelzA2 ->   (U1(UnitSelz), O1(Index2), G(false), B(true)),
    AluOpcSelzA3 ->   (U1(UnitSelz), O1(Index3), G(false), B(true)),
    AluOpcSelnzA0 ->  (U1(UnitSelnz), O1(Index0), G(false), B(true)),
    AluOpcSelnzA1 ->  (U1(UnitSelnz), O1(Index1), G(false), B(true)),
    AluOpcSelnzA2 ->  (U1(UnitSelnz), O1(Index2), G(false), B(true)),
    AluOpcSelnzA3 ->  (U1(UnitSelnz), O1(Index3), G(false), B(true)),

    ////////////////////////////////////////////////////////////////////

    // Generating ops - dtos == 1

    /* Stack read */
    AluOpcRdS0 ->     (U2(UnitStack), O1(Index0), G(true), None),
    AluOpcRdS1 ->     (U2(UnitStack), O1(Index1), G(true), None),
    AluOpcRdS2 ->     (U2(UnitStack), O1(Index2), G(true), None),
    AluOpcRdS3 ->     (U2(UnitStack), O1(Index3), G(true), None),

    /* Register read */
    AluOpcRdR0 ->     (U2(UnitReg), O1(Index0), G(true), None),
    AluOpcRdR1 ->     (U2(UnitReg), O1(Index1), G(true), None),
    AluOpcRdR2 ->     (U2(UnitReg), O1(Index2), G(true), None),
    AluOpcRdR3 ->     (U2(UnitReg), O1(Index3), G(true), None),

    /* Remote alu TOS access (LAST cycle result) */
    AluOpcRdA0 ->     (U3(UnitAlu), O1(Index0), G(true), None),
    AluOpcRdA1 ->     (U3(UnitAlu), O1(Index1), G(true), None),
    AluOpcRdA2 ->     (U3(UnitAlu), O1(Index2), G(true), None),
    AluOpcRdA3 ->     (U3(UnitAlu), O1(Index3), G(true), None),

    /* (Remote) Mem unit value access (THIS cycle result - if it's ready, otherwise stall) */
    // AluOpcRdM0v0 ->   (U3(UnitMem0), None, G(true), None),
    // AluOpcRdM0v1 ->   (U3(UnitMem0), None, G(true), None),
    // AluOpcRdM1v0 ->   (U3(UnitMem0), None, G(true), None),
    // AluOpcRdM1v1 ->   (U3(UnitMem0), None, G(true), None),
    // TODO addr's too?

    /* Icache constants */

    // Non-overlapping
    AluOpcConb0 ->    (U3(UnitConb0), O1(Index0), G(true), None),
    AluOpcConb1 ->    (U3(UnitConb0), O1(Index1), G(true), None),
    AluOpcConb2 ->    (U3(UnitConb0), O1(Index2), G(true), None),
    AluOpcConb3 ->    (U3(UnitConb0), O1(Index3), G(true), None),

    // Overlapping b/h/w
    AluOpcConb4 ->    (U3(UnitConb), O1(Index0), G(true), None),
    AluOpcConb5 ->    (U3(UnitConb), O1(Index1), G(true), None),
    AluOpcConb6 ->    (U3(UnitConb), O1(Index2), G(true), None),
    AluOpcConb7 ->    (U3(UnitConb), O1(Index3), G(true), None),
    AluOpcConh0 ->    (U3(UnitConh), O1(Index0), G(true), None),
    AluOpcConh1 ->    (U3(UnitConh), O1(Index1), G(true), None),
    AluOpcConh2 ->    (U3(UnitConh), O1(Index2), G(true), None),
    AluOpcConh3 ->    (U3(UnitConh), O1(Index3), G(true), None),
    AluOpcConw0 ->    (U3(UnitConw), O1(Index0), G(true), None),
    AluOpcConw1 ->    (U3(UnitConw), O1(Index1), G(true), None),
    AluOpcConw2 ->    (U3(UnitConw), O1(Index2), G(true), None),
    AluOpcConw3 ->    (U3(UnitConw), O1(Index3), G(true), None),
  );
}
