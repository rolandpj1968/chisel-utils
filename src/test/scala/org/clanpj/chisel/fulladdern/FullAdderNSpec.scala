package org.clanpj.chisel.fulladdern

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import scala.util.Random

class FullAdderNSpec extends AnyFreeSpec with Matchers {

  def bi(i: Long) = BigInt(i)

  def dotest[T <: FullAdderN](dutgen: () => T, desc: String, n: Int): Unit = {
    val mask = (BigInt(1) << n) - 1

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
        // Note Random.nextLong only useful up to 64 bit adders
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

  dotest(() => FullAdderN.simple(1), "simple1", 1)
  dotest(() => FullAdderN.simple(3), "simple3", 3)
  dotest(() => FullAdderN.simple(4), "simple4", 4)
  dotest(() => FullAdderN.simple(64), "simple64", 64)
}
