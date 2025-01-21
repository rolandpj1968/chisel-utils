package org.clanpj.chisel.util.sha256

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import scala.util.Random

class Sha256RoundSpec extends AnyFreeSpec with Matchers {

  def bi(i: Long) = BigInt(i)

  "Sha256Round should be generated" in {
    simulate(new Sha256Round(0, false)) { dut =>

      // Initialise regs
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      dut.clock.step()

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
