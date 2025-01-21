package org.clanpj.chisel.util.sha256

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import scala.util.Random

class Sha256Spec extends AnyFreeSpec with Matchers {
  import Sha256._

  def bi(i: Long) = BigInt(i)

  "Sha256Core should be generated" in {
    simulate(new Sha256Core(false)) { dut =>

      // Initialise
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      dut.clock.step()

      for (i <- 0 to 7) { dut.in.h(i).poke(h(i)) }
      // padded empty string input
      dut.in.msg(0).poke(bi(0x80000000))
      for (i <- 1 to 15) { dut.in.msg(0).poke(bi(0)) }
      dut.clock.step()
      dut.out.valid.expect(true.B)
      for (i <- 0 to 7) {
        System.out.printf("%08x", dut.out.h(i).peek().litValue.toInt)
      }
      println()

      // // Push a value
      // dut.io.en.poke(true.B)
      // dut.io.wEn.poke(true.B)
      // dut.io.nTosV.poke(7.U)
      // dut.io.dITos.poke(1.U)
      // dut.clock.step()
      // dut.io.tosV.expect(7.U)
    }
  }
}
