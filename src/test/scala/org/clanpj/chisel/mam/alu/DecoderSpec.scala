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
        printf("opc: 0x%2x\n", opc)
      }
    }
  }

}

object DecoderSpec {
  import AluOpcode._
  import AluUnit._
  import AluGenUnit._
  import AluMamGenUnit._

  def N[T] = None
  def S[T](v: T) = Some(v)

  def U1(u: AluUnit) = u.id.U(3.W)
  def U2(u: AluGenUnit) = u.id.U(3.W)
  def U3(u: AluMamGenUnit) = u.id.U(3.W)

  val spec = Map(
    /////////////////////////////////////////////////////////////////

    // Unary ops dtos == 0
    // src0 == 0, src1 == TOS

    AluOpcNop ->      (U1(UnitZero)),

    AluOpcNeg ->      (U1(UnitAdd)),

    AluOpcNot ->      (U1(UnitBits)),

    /* Register write non-popping */

    AluOpcWrR0 ->     (U1(UnitZero)),
    AluOpcWrR1 ->     (U1(UnitZero)),
    AluOpcWrR2 ->     (U1(UnitZero)),
    AluOpcWrR3 ->     (U1(UnitZero)),

    /* Extensions and Truncations */

    AluOpcExtb ->     (U1(UnitExt)),
    AluOpcExtub ->    (U1(UnitExt)),
    AluOpcExth ->     (U1(UnitExt)),
    AluOpcExtuh ->    (U1(UnitExt)),

    /////////////////////////////////////////////////////////////////

    // Binary ops dtos == -1
    // src0/lhs/x == NOS, src1/rhs/y == TOS

    /* Integer Add/Slt XLEN width */

    AluOpcAdd ->      (U1(UnitAdd)),
    AluOpcSub ->      (U1(UnitAdd)),
    AluOpcSlt ->      (U1(UnitAdd)),
    AluOpcSltu ->     (U1(UnitAdd)),

    /* Bits - XLEN width */

    AluOpcAnd ->      (U1(UnitBits)),
    AluOpcOr ->       (U1(UnitBits)),
    AluOpcXor ->      (U1(UnitBits)),
    AluOpcSeq ->      (U1(UnitBits)),

    /* Shift - XLEN width */

    AluOpcSll ->      (U1(UnitShift)),
    AluOpcSrl ->      (U1(UnitShift)),
    AluOpcSra ->      (U1(UnitShift)),

    /* Select using remote alu condition (LAST cycle value) */

    AluOpcSelzA0 ->   (U1(UnitSelz)),
    AluOpcSelzA1 ->   (U1(UnitSelz)),
    AluOpcSelzA2 ->   (U1(UnitSelz)),
    AluOpcSelzA3 ->   (U1(UnitSelz)),
    AluOpcSelnzA0 ->  (U1(UnitSelnz)),
    AluOpcSelnzA1 ->  (U1(UnitSelnz)),
    AluOpcSelnzA2 ->  (U1(UnitSelnz)),
    AluOpcSelnzA3 ->  (U1(UnitSelnz)),

    ////////////////////////////////////////////////////////////////////

    // Generating ops - dtos == 1

    /* Stack read */
    AluOpcRdS0 ->     (U2(UnitStack)),
    AluOpcRdS1 ->     (U2(UnitStack)),
    AluOpcRdS2 ->     (U2(UnitStack)),
    AluOpcRdS3 ->     (U2(UnitStack)),

    /* Register read */
    AluOpcRdR0 ->     (U2(UnitReg)),
    AluOpcRdR1 ->     (U2(UnitReg)),
    AluOpcRdR2 ->     (U2(UnitReg)),
    AluOpcRdR3 ->     (U2(UnitReg)),

    /* Remote alu TOS access (LAST cycle result) */
    AluOpcRdA0 ->     (U3(UnitAlu)),
    AluOpcRdA1 ->     (U3(UnitAlu)),
    AluOpcRdA2 ->     (U3(UnitAlu)),
    AluOpcRdA3 ->     (U3(UnitAlu)),

    /* (Remote) Mem unit value access (THIS cycle result - if it's ready, otherwise stall) */
    // AluOpcRdM0v0 ->   (U3(UnitMem0)),
    // AluOpcRdM0v1 ->   (U3(UnitMem0)),
    // AluOpcRdM1v0 ->   (U3(UnitMem0)),
    // AluOpcRdM1v1 ->   (U3(UnitMem0)),
    // TODO addr's too?

    /* Icache constants */

    // Non-overlapping
    AluOpcConb0 ->    (U3(UnitConb0)),
    AluOpcConb1 ->    (U3(UnitConb0)),
    AluOpcConb2 ->    (U3(UnitConb0)),
    AluOpcConb3 ->    (U3(UnitConb0)),

    // Overlapping b/h/w
    AluOpcConb4 ->    (U3(UnitConb)),
    AluOpcConb5 ->    (U3(UnitConb)),
    AluOpcConb6 ->    (U3(UnitConb)),
    AluOpcConb7 ->    (U3(UnitConb)),
    AluOpcConh0 ->    (U3(UnitConh)),
    AluOpcConh1 ->    (U3(UnitConh)),
    AluOpcConh2 ->    (U3(UnitConh)),
    AluOpcConh3 ->    (U3(UnitConh)),
    AluOpcConw0 ->    (U3(UnitConw)),
    AluOpcConw1 ->    (U3(UnitConw)),
    AluOpcConw2 ->    (U3(UnitConw)),
    AluOpcConw3 ->    (U3(UnitConw)),
  );
}
