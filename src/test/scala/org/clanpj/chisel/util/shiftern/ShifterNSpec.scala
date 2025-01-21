package org.clanpj.chisel.util.shiftern

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import scala.util.Random

class ShifterNSpec extends AnyFreeSpec with Matchers {

  def bi(i: Long) = BigInt(i)

  def dotest[T <: ShifterN](dutgen: () => T, desc: String, n: Int): Unit = {
    val mask = (bi(1) << n) - 1

    def V(i: Int): BigInt = bi(i) & mask

    val log2n = log2Ceil(n)
    val shiftMask = (bi(1) << log2n)-1

    desc + " should shift edge cases" in {
      simulate(dutgen()) { dut =>
        val shifts = Seq(V(0), V(1), V(2), V(n-1), V(n), V(n/2))
        val xs = Seq(V(-1), V(-2), V(0), V(1), mask, (shiftMask-1) & mask, (bi(1) << n/2) & mask, ((bi(1) << n/2)-1) & mask, ((bi(1) << n/2)+1) & mask)
        val types = Seq((true, false), (false, false), (false, true)) // TODO - right arithmetic
        val testValues = for { x <- xs; y <- shifts; la <- types} yield (x, y, la)
        //println("Test values: " + testValues);
        testValues.map { case (x, y, (left, arith)) => {
          //println("                           Testing " + n + "-bit: x = " + x + ", y = " + y + ", left = " + left + ", arith = " + arith)
          dut.io.left.poke(left)
          dut.io.arith.poke(arith)
          dut.io.x.poke(x)
          dut.io.y.poke(y)
          dut.clock.step()
          val v = if (left) {
            x << y.toInt
          } else if (arith) {
            val xs = if ((x & (bi(1) << (n-1))) == 0) { x } else { x - mask - 1 }
            xs >> y.toInt
          } else {
            x >> y.toInt
          }
          dut.io.v.expect(v & mask)
        }}
      }
    }

    // desc + " should shift random numbers" in {
    //   simulate(dutgen()) { dut =>
    //     // TODO Random.nextLong only useful up to 64 bit adders
    //     val testValues = for { i <- 0 to 10; j <- 0 to 10} yield (bi(Random.nextLong()) & mask, bi(Random.nextLong()) & mask, Random.nextInt() & 1)
    //     testValues.map { case (x, y, cin) => {
    //       dut.io.cin.poke(cin.U)
    //       dut.io.x.poke(x.U)
    //       dut.io.y.poke(y.U)
    //       dut.clock.step()
    //       val sum = x + y + cin
    //       dut.io.sum.expect((sum & mask).U)
    //       dut.io.cout.expect( (if (sum > mask) 1 else 0).U)
    //     }}
    //   }
    // }
  }

  val FULL = false

  // simple shifters
  dotest(() => ShifterN.simple(1), "simple1", 1)
  dotest(() => ShifterN.simple(8), "simple8", 8)
  if (FULL) {
    dotest(() => ShifterN.simple(32), "simple32", 32)
    dotest(() => ShifterN.simple(64), "simple64", 64)
  }

  // O(logN) shifters
  dotest(() => ShifterN.log(1), "log1", 1)
  dotest(() => ShifterN.log(8), "log8", 8)
  if (FULL) {
    dotest(() => ShifterN.log(32), "log32", 32)
    dotest(() => ShifterN.log(64), "log64", 64)
  }

}
