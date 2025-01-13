package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class DecoderSpec extends AnyFreeSpec with Matchers {
}

object DecoderSpec {
  import AluOpcode1._

  def N[T] = None
  def S[T](v: T) = Some(v)

  def U(x: UInt) = x

  val spec = Map(
    /////////////////////////////////////////////////////////////////

    // Unary ops dtos == 0
    // src0 == 0, src1 == TOS

    AluOpcNop ->      (),

    AluOpcNeg ->      (),

    AluOpcNot ->      (),

    /* Register write non-popping */

    AluOpcWrR0 ->     (),
    AluOpcWrR1 ->     (),
    AluOpcWrR2 ->     (),
    AluOpcWrR3 ->     (),

    /* Extensions and Truncations */

    AluOpcExtb ->     (),
    AluOpcExtub ->    (),
    AluOpcExth ->     (),
    AluOpcExtuh ->    (),

    /////////////////////////////////////////////////////////////////

    // Binary ops dtos == -1
    // src0/lhs/x == NOS, src1/rhs/y == TOS

    /* Integer Add/Slt XLEN width */

    AluOpcAdd ->      (),
    AluOpcSub ->      (),
    AluOpcSlt ->      (),
    AluOpcSltu ->     (),

    /* Bits - XLEN width */

    AluOpcAnd ->      (),
    AluOpcOr ->       (),
    AluOpcXor ->      (),
    AluOpcSeq ->      (),

    /* Shift - XLEN width */

    AluOpcSll ->      (),
    AluOpcSrl ->      (),
    AluOpcSra ->      (),

    /* Select using remote alu condition (LAST cycle value) */

    AluOpcSelzA0 ->   (),
    AluOpcSelzA1 ->   (),
    AluOpcSelzA2 ->   (),
    AluOpcSelzA3 ->   (),
    AluOpcSelnzA0 ->  (),
    AluOpcSelnzA1 ->  (),
    AluOpcSelnzA2 ->  (),
    AluOpcSelnzA3 ->  (),

    ////////////////////////////////////////////////////////////////////

    // Generating ops - dtos == 1

    /* Stack read */
    AluOpcRdS0 ->     (),
    AluOpcRdS1 ->     (),
    AluOpcRdS2 ->     (),
    AluOpcRdS3 ->     (),

    /* Register read */
    AluOpcRdR0 ->     (),
    AluOpcRdR1 ->     (),
    AluOpcRdR2 ->     (),
    AluOpcRdR3 ->     (),

    /* Remote alu TOS access (LAST cycle result) */
    AluOpcRdA0 ->     (),
    AluOpcRdA1 ->     (),
    AluOpcRdA2 ->     (),
    AluOpcRdA3 ->     (),

    /* (Remote) Mem unit value access (THIS cycle result - if it's ready, otherwise stall) */
    // AluOpcRdM0v0 ->   (),
    // AluOpcRdM0v1 ->   (),
    // AluOpcRdM1v0 ->   (),
    // AluOpcRdM1v1 ->   (),
    // TODO addr's too?

    /* Icache constants */

    // Non-overlapping
    AluOpcConb0 ->    (),
    AluOpcConb1 ->    (),
    AluOpcConb2 ->    (),
    AluOpcConb3 ->    (),

    // Overlapping b/h/w
    AluOpcConb4 ->    (),
    AluOpcConb5 ->    (),
    AluOpcConb6 ->    (),
    AluOpcConb7 ->    (),
    AluOpcConh0 ->    (),
    AluOpcConh1 ->    (),
    AluOpcConh2 ->    (),
    AluOpcConh3 ->    (),
    AluOpcConw0 ->    (),
    AluOpcConw1 ->    (),
    AluOpcConw2 ->    (),
    AluOpcConw3 ->    (),
  );

}
