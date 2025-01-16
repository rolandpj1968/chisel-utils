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
        //println(f"opc: 0x$opc%2x")
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
          case Some((unit, opOpt)) => {
            assert(!inv)
            dut.io.unit.expect(unit)
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

  def N[T] = None
  def S[T](v: T) = Some(v)

  def U1(u: AluUnit) = u.id.U(3.W)
  def U2(u: AluGenUnit) = u.id.U(3.W)
  def U3(u: AluMamGenUnit) = u.id.U(3.W)

  def O1(u: IndexOp) = Some(u.id.U(2.W))
  def O2(u: AdderOp) = Some(u.id.U(2.W))
  def O3(u: BitsOp) = Some(u.id.U(2.W))

  val spec = Map(
    /////////////////////////////////////////////////////////////////

    // Unary ops dtos == 0
    // src0 == 0, src1 == TOS

    AluOpcNop ->      (U1(UnitZero), None),

    AluOpcNeg ->      (U1(UnitAdd), O2(AdderSub)),

    AluOpcNot ->      (U1(UnitBits), O3(BitsXor)),

    /* Register write non-popping */

    AluOpcWrR0 ->     (U1(UnitTos), O1(Index0)),
    AluOpcWrR1 ->     (U1(UnitTos), O1(Index1)),
    AluOpcWrR2 ->     (U1(UnitTos), O1(Index2)),
    AluOpcWrR3 ->     (U1(UnitTos), O1(Index3)),

    /* Extensions and Truncations */

    AluOpcExtb ->     (U1(UnitExt), None),
    AluOpcExtub ->    (U1(UnitExt), None),
    AluOpcExth ->     (U1(UnitExt), None),
    AluOpcExtuh ->    (U1(UnitExt), None),

    /////////////////////////////////////////////////////////////////

    // Binary ops dtos == -1
    // src0/lhs/x == NOS, src1/rhs/y == TOS

    /* Integer Add/Slt XLEN width */

    AluOpcAdd ->      (U1(UnitAdd), O2(AdderAdd)),
    AluOpcSub ->      (U1(UnitAdd), O2(AdderSub)),
    AluOpcSlt ->      (U1(UnitAdd), O2(AdderSlt)),
    AluOpcSltu ->     (U1(UnitAdd), O2(AdderSltu)),

    /* Bits - XLEN width */

    AluOpcAnd ->      (U1(UnitBits), O3(BitsAnd)),
    AluOpcOr ->       (U1(UnitBits), O3(BitsOr)),
    AluOpcXor ->      (U1(UnitBits), O3(BitsXor)),
    AluOpcSeq ->      (U1(UnitBits), O3(BitsSeq)),

    /* Shift - XLEN width */

    AluOpcSll ->      (U1(UnitShift), None),
    AluOpcSrl ->      (U1(UnitShift), None),
    AluOpcSra ->      (U1(UnitShift), None),

    /* Select using remote alu condition (LAST cycle value) */

    AluOpcSelzA0 ->   (U1(UnitSelz), O1(Index0)),
    AluOpcSelzA1 ->   (U1(UnitSelz), O1(Index1)),
    AluOpcSelzA2 ->   (U1(UnitSelz), O1(Index2)),
    AluOpcSelzA3 ->   (U1(UnitSelz), O1(Index3)),
    AluOpcSelnzA0 ->  (U1(UnitSelnz), O1(Index0)),
    AluOpcSelnzA1 ->  (U1(UnitSelnz), O1(Index1)),
    AluOpcSelnzA2 ->  (U1(UnitSelnz), O1(Index2)),
    AluOpcSelnzA3 ->  (U1(UnitSelnz), O1(Index3)),

    ////////////////////////////////////////////////////////////////////

    // Generating ops - dtos == 1

    /* Stack read */
    AluOpcRdS0 ->     (U2(UnitStack), O1(Index0)),
    AluOpcRdS1 ->     (U2(UnitStack), O1(Index1)),
    AluOpcRdS2 ->     (U2(UnitStack), O1(Index2)),
    AluOpcRdS3 ->     (U2(UnitStack), O1(Index3)),

    /* Register read */
    AluOpcRdR0 ->     (U2(UnitReg), O1(Index0)),
    AluOpcRdR1 ->     (U2(UnitReg), O1(Index1)),
    AluOpcRdR2 ->     (U2(UnitReg), O1(Index2)),
    AluOpcRdR3 ->     (U2(UnitReg), O1(Index3)),

    /* Remote alu TOS access (LAST cycle result) */
    AluOpcRdA0 ->     (U3(UnitAlu), O1(Index0)),
    AluOpcRdA1 ->     (U3(UnitAlu), O1(Index1)),
    AluOpcRdA2 ->     (U3(UnitAlu), O1(Index2)),
    AluOpcRdA3 ->     (U3(UnitAlu), O1(Index3)),

    /* (Remote) Mem unit value access (THIS cycle result - if it's ready, otherwise stall) */
    // AluOpcRdM0v0 ->   (U3(UnitMem0), None),
    // AluOpcRdM0v1 ->   (U3(UnitMem0), None),
    // AluOpcRdM1v0 ->   (U3(UnitMem0), None),
    // AluOpcRdM1v1 ->   (U3(UnitMem0), None),
    // TODO addr's too?

    /* Icache constants */

    // Non-overlapping
    AluOpcConb0 ->    (U3(UnitConb0), O1(Index0)),
    AluOpcConb1 ->    (U3(UnitConb0), O1(Index1)),
    AluOpcConb2 ->    (U3(UnitConb0), O1(Index2)),
    AluOpcConb3 ->    (U3(UnitConb0), O1(Index3)),

    // Overlapping b/h/w
    AluOpcConb4 ->    (U3(UnitConb), O1(Index0)),
    AluOpcConb5 ->    (U3(UnitConb), O1(Index1)),
    AluOpcConb6 ->    (U3(UnitConb), O1(Index2)),
    AluOpcConb7 ->    (U3(UnitConb), O1(Index3)),
    AluOpcConh0 ->    (U3(UnitConh), O1(Index0)),
    AluOpcConh1 ->    (U3(UnitConh), O1(Index1)),
    AluOpcConh2 ->    (U3(UnitConh), O1(Index2)),
    AluOpcConh3 ->    (U3(UnitConh), O1(Index3)),
    AluOpcConw0 ->    (U3(UnitConw), O1(Index0)),
    AluOpcConw1 ->    (U3(UnitConw), O1(Index1)),
    AluOpcConw2 ->    (U3(UnitConw), O1(Index2)),
    AluOpcConw3 ->    (U3(UnitConw), O1(Index3)),
  );
}
