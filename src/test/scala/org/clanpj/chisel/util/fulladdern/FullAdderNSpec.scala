package org.clanpj.chisel.util.fulladdern

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import scala.util.Random

class FullAdderNSpec extends AnyFreeSpec with Matchers {

  def bi(i: Long) = BigInt(i)

  def dotest[T <: FullAdderN](dutgen: () => T, desc: String, n: Int): Unit = {
    val mask = (bi(1) << n) - 1

    desc + " should add edge cases" in {
      simulate(dutgen()) { dut =>
        val testValues = for { x <- -3 to 3; y <- -3 to 3; cin <- 0 to 1} yield (bi(x) & mask, bi(y) & mask, cin)
        testValues.map { case (x, y, cin) => {
          dut.io.cin.poke(cin.U)
          dut.io.x.poke(x.U)
          dut.io.y.poke(y.U)
          dut.clock.step()
          val sum = x + y + cin
          dut.io.sum.expect((sum & mask).U)
          dut.io.cout.expect( (if (sum > mask) 1 else 0).U)
        }}
      }
    }

    desc + " should add random numbers" in {
      simulate(dutgen()) { dut =>
        // TODO Random.nextLong only useful up to 64 bit adders
        val testValues = for { i <- 0 to 10; j <- 0 to 10} yield (bi(Random.nextLong()) & mask, bi(Random.nextLong()) & mask, Random.nextInt() & 1)
        testValues.map { case (x, y, cin) => {
          dut.io.cin.poke(cin.U)
          dut.io.x.poke(x.U)
          dut.io.y.poke(y.U)
          dut.clock.step()
          val sum = x + y + cin
          dut.io.sum.expect((sum & mask).U)
          dut.io.cout.expect( (if (sum > mask) 1 else 0).U)
        }}
      }
    }
  }

  val FULL = false

  // simple adders
  dotest(() => FullAdderN.simple(1), "simple1", 1)
  dotest(() => FullAdderN.simple(3), "simple3", 3)
  if (FULL) {
    dotest(() => FullAdderN.simple(4), "simple4", 4)
    dotest(() => FullAdderN.simple(64), "simple64", 64)
  }

  // carry-select adders
  dotest(() => FullAdderN.csel(2), "csel2", 2)
  dotest(() => FullAdderN.csel(5), "csel5", 5)
  if (FULL) {
    dotest(() => FullAdderN.csel(16), "csel16", 16)
    dotest(() => FullAdderN.csel(64), "csel64", 64)
  }

  // 2-layer carry-select adder
  dotest(() => FullAdderN.csel(19, (n: Int) => FullAdderN.csel(n)), "csel19[csel9,csel10]", 19)

  // carry-lookahead adders
  dotest(() => FullAdderN.clkahd(2), "clkahd2", 2)
  dotest(() => FullAdderN.clkahd(5), "clkahd5", 5)
  if (FULL) {
    dotest(() => FullAdderN.clkahd(16), "clkahd16", 16)
    dotest(() => FullAdderN.clkahd(64), "clkahd64", 64)
  }

  // 2-layer carry-lookahead adder
  dotest(() => FullAdderN.clkahd(23, (n: Int) => FullAdderN.clkahd(n)), "clkahd23[clkahd11,clkahd12]", 23)

  // carry-select adder over carry-lookahead adder
  dotest(() => FullAdderN.csel(33, (n: Int) => FullAdderN.clkahd(n)), "csel33[clkahd16,clkahd17]", 33)
}
