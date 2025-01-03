package org.clanpj.chisel.fulladdern

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class FullAdderNSpec extends AnyFreeSpec with Matchers {

  def dotest[T <: FullAdderN](dutgen: () => T, desc: String, n: Int): Unit = {
    val mask = (BigInt(1) << n) - 1

    println("Hallo RPJ - BigInt(-1) & mask is " + BigInt(-1) & mask);

    desc + " should add" in {
      simulate(dutgen()) { dut =>
        val cin = 0;
        val op1 = 2;
        val op2 = 3;
        dut.io.cin.poke(cin.U)
        dut.io.op1.poke(op1.U)
        dut.io.op2.poke(op2.U)
        dut.clock.step()
        val sum = op1 + op2 + cin
        dut.io.sum.expect(5.U)
        dut.io.cout.expect(0.U)
      }
    }
  }

  dotest(() => FullAdderN.simple(4), "simple4", 4)

  //behaviour of "SimpleFullAdderN"

  "SimpleFullAdderN should add" in {
    val n = 4
    simulate(FullAdderN.simple(n)) { dut =>
      val cin = 0;
      val op1 = 2;
      val op2 = 3;
      dut.io.cin.poke(cin.U)
      dut.io.op1.poke(op1.U)
      dut.io.op2.poke(op2.U)
      dut.clock.step()
      val sum = op1 + op2 + cin
      dut.io.sum.expect(5.U)
      dut.io.cout.expect(0.U)
    }
  }
}
