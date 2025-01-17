package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class AluSpec extends AnyFreeSpec with Matchers {
  import AluOpcode._

  def bi(i: Long) = BigInt(i)

  val n = 32
  val mask = (bi(1) << n) - 1

  def reset(dut: AluTestHarness): Unit = {
      // println("AluTestHarness: reset");
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      dut.clock.step()
  }

  def exeOp(dut: AluTestHarness, op: AluOpcode): Unit = {
      dut.io.en.poke(true.B)
      // println("AluTestHarness: running " + op);
      dut.io.opc.poke(op.id.U)
      dut.clock.step()
      dut.io.stall.expect(false.B)
      dut.io.trap.expect(false.B)
  }

  val consp = Seq(AluOpcConb0, AluOpcConb1, AluOpcConb2, AluOpcConb3)
  val consm = Seq(AluOpcConb4, AluOpcConb5, AluOpcConb6, AluOpcConb7)

  def conop(v: Int): AluOpcode = {
    if (0 <= v) {
      consp(v)
    } else {
      consm(-v-1)
    }
  }

  def exeConOp(dut: AluTestHarness, v: Int): Unit = {
    exeOp(dut, conop(v))
    dut.io.nTosV.expect(v)
  }

  def exeBinOp(dut: AluTestHarness, lhs: Int, rhs: Int, op: AluOpcode): Unit = {
    exeConOp(dut, lhs)
    exeConOp(dut, rhs)
    exeOp(dut, op)
  }

  def checkNTos(dut: AluTestHarness, res: BigInt): Unit = {
    // TODO - this is weird... (for a NOOB)
    //   println seems to indicate the expected result, and the stack value at the next
    //     op also seems correct.
    //   However, expect() sees a different value
    //   If, however I save it into a register in the AluTestHarness, then the expected
    //     value emerges next cycle from the (lTosV) register.
    // dut.io.nTosV.expect(res & mask)
    val nTosV = dut.io.nTosV.peek().litValue
    if (nTosV != (res & mask)) {
      //println("                                             ooops " + op + " (" + lhs + "," + rhs + ") is " + dut.io.nTosV.peek() + " expecting " + res);
      exeOp(dut, AluOpcNop)
      //println("                                                     after a NOP lTosV is " + dut.io.lTosV.peek())
      dut.io.lTosV.expect(res & mask)
    }
  }

  def testBinOp(dut: AluTestHarness, lhs: Int, rhs: Int, op: AluOpcode, res: BigInt): Unit = {
    exeBinOp(dut, lhs, rhs, op)
    checkNTos(dut, res)
  }

  "Alu should execute opcodes" in {
    simulate(new AluTestHarness(n)) { dut =>

      // Initialise
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      dut.clock.step()

      // Check invalid op trapping
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
        //println(f"opc: 0x$opc%2x spec inv / spec = $inv / $spec")
        assert(inv == spec.isEmpty)

        dut.io.en.poke(true.B)
        dut.io.opc.poke(opc.U)
        dut.clock.step()
        dut.io.stall.expect(false.B)
        dut.io.trap.expect(inv.B)
      }

      // Reset
      reset(dut)

      val testValues = for { x <- 0 to 3; y <- 0 to 3} yield (x, y)

      // Binary ops

      // BitsUnit
      testValues.map { case (lhs, rhs) => {
        testBinOp(dut, lhs, rhs, AluOpcAnd, bi(lhs) & bi(rhs))
      }}
      testValues.map { case (lhs, rhs) => {
        testBinOp(dut, lhs, rhs, AluOpcOr, bi(lhs) | bi(rhs))
      }}
      testValues.map { case (lhs, rhs) => {
        testBinOp(dut, lhs, rhs, AluOpcXor, bi(lhs) ^ bi(rhs))
      }}
      testValues.map { case (lhs, rhs) => {
        testBinOp(dut, lhs, rhs, AluOpcSeq, bi(if (lhs == rhs) 1 else 0))
      }}
      // AddUnit
      testValues.map { case (lhs, rhs) => {
        testBinOp(dut, lhs, rhs, AluOpcAdd, bi(lhs) + bi(rhs))
      }}
      testValues.map { case (lhs, rhs) => {
        testBinOp(dut, lhs, rhs, AluOpcSub, bi(lhs) - bi(rhs))
      }}
      testValues.map { case (lhs, rhs) => {
        testBinOp(dut, lhs, rhs, AluOpcSlt, bi(if (lhs < rhs) 1 else 0))
      }}
      testValues.map { case (lhs, rhs) => {
        testBinOp(dut, lhs, rhs, AluOpcSltu, bi(if ((bi(lhs) & mask) < (bi(rhs) & mask)) 1 else 0))
      }}

      //println("                                              hello RPJ nTosV is " + dut.io.nTosV.peek())
      //println("                                              hello RPJ alu nTosV is " + dut.alu.io.nTosV.peek())
    }
  }
}
