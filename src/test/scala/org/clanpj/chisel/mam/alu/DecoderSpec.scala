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

  def N[T] = None
  def S[T](v: T) = Some(v)

  def U1(u: AluUnit) = u.id.U(3.W)
  def U2(u: AluGenUnit) = u.id.U(3.W)
  def U3(u: AluMamGenUnit) = u.id.U(3.W)

  def O1(u: IndexOp) = Some(u.id.U(2.W))

  val spec = Map(
    /////////////////////////////////////////////////////////////////

    // Unary ops dtos == 0
    // src0 == 0, src1 == TOS

    AluOpcNop ->      (U1(UnitZero), None),

    AluOpcNeg ->      (U1(UnitAdd), None),

    AluOpcNot ->      (U1(UnitBits), None),

    /* Register write non-popping */

    AluOpcWrR0 ->     (U1(UnitTos), None),
    AluOpcWrR1 ->     (U1(UnitTos), None),
    AluOpcWrR2 ->     (U1(UnitTos), None),
    AluOpcWrR3 ->     (U1(UnitTos), None),

    /* Extensions and Truncations */

    AluOpcExtb ->     (U1(UnitExt), None),
    AluOpcExtub ->    (U1(UnitExt), None),
    AluOpcExth ->     (U1(UnitExt), None),
    AluOpcExtuh ->    (U1(UnitExt), None),

    /////////////////////////////////////////////////////////////////

    // Binary ops dtos == -1
    // src0/lhs/x == NOS, src1/rhs/y == TOS

    /* Integer Add/Slt XLEN width */

    AluOpcAdd ->      (U1(UnitAdd), None),
    AluOpcSub ->      (U1(UnitAdd), None),
    AluOpcSlt ->      (U1(UnitAdd), None),
    AluOpcSltu ->     (U1(UnitAdd), None),

    /* Bits - XLEN width */

    AluOpcAnd ->      (U1(UnitBits), None),
    AluOpcOr ->       (U1(UnitBits), None),
    AluOpcXor ->      (U1(UnitBits), None),
    AluOpcSeq ->      (U1(UnitBits), None),

    /* Shift - XLEN width */

    AluOpcSll ->      (U1(UnitShift), None),
    AluOpcSrl ->      (U1(UnitShift), None),
    AluOpcSra ->      (U1(UnitShift), None),

    /* Select using remote alu condition (LAST cycle value) */

    AluOpcSelzA0 ->   (U1(UnitSelz), None),
    AluOpcSelzA1 ->   (U1(UnitSelz), None),
    AluOpcSelzA2 ->   (U1(UnitSelz), None),
    AluOpcSelzA3 ->   (U1(UnitSelz), None),
    AluOpcSelnzA0 ->  (U1(UnitSelnz), None),
    AluOpcSelnzA1 ->  (U1(UnitSelnz), None),
    AluOpcSelnzA2 ->  (U1(UnitSelnz), None),
    AluOpcSelnzA3 ->  (U1(UnitSelnz), None),

    ////////////////////////////////////////////////////////////////////

    // Generating ops - dtos == 1

    /* Stack read */
    AluOpcRdS0 ->     (U2(UnitStack), None),
    AluOpcRdS1 ->     (U2(UnitStack), None),
    AluOpcRdS2 ->     (U2(UnitStack), None),
    AluOpcRdS3 ->     (U2(UnitStack), None),

    /* Register read */
    AluOpcRdR0 ->     (U2(UnitReg), None),
    AluOpcRdR1 ->     (U2(UnitReg), None),
    AluOpcRdR2 ->     (U2(UnitReg), None),
    AluOpcRdR3 ->     (U2(UnitReg), None),

    /* Remote alu TOS access (LAST cycle result) */
    AluOpcRdA0 ->     (U3(UnitAlu), None),
    AluOpcRdA1 ->     (U3(UnitAlu), None),
    AluOpcRdA2 ->     (U3(UnitAlu), None),
    AluOpcRdA3 ->     (U3(UnitAlu), None),

    /* (Remote) Mem unit value access (THIS cycle result - if it's ready, otherwise stall) */
    // AluOpcRdM0v0 ->   (U3(UnitMem0), None),
    // AluOpcRdM0v1 ->   (U3(UnitMem0), None),
    // AluOpcRdM1v0 ->   (U3(UnitMem0), None),
    // AluOpcRdM1v1 ->   (U3(UnitMem0), None),
    // TODO addr's too?

    /* Icache constants */

    // Non-overlapping
    AluOpcConb0 ->    (U3(UnitConb0), None),
    AluOpcConb1 ->    (U3(UnitConb0), None),
    AluOpcConb2 ->    (U3(UnitConb0), None),
    AluOpcConb3 ->    (U3(UnitConb0), None),

    // Overlapping b/h/w
    AluOpcConb4 ->    (U3(UnitConb), None),
    AluOpcConb5 ->    (U3(UnitConb), None),
    AluOpcConb6 ->    (U3(UnitConb), None),
    AluOpcConb7 ->    (U3(UnitConb), None),
    AluOpcConh0 ->    (U3(UnitConh), None),
    AluOpcConh1 ->    (U3(UnitConh), None),
    AluOpcConh2 ->    (U3(UnitConh), None),
    AluOpcConh3 ->    (U3(UnitConh), None),
    AluOpcConw0 ->    (U3(UnitConw), None),
    AluOpcConw1 ->    (U3(UnitConw), None),
    AluOpcConw2 ->    (U3(UnitConw), None),
    AluOpcConw3 ->    (U3(UnitConw), None),
  );
}
