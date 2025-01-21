package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import scala.util.Random

class StackSpec extends AnyFreeSpec with Matchers {

  def bi(i: Long) = BigInt(i)

  def dotest(dutgen: () => Stack, desc: String, n: Int, order: Int): Unit = {
    val mask = (bi(1) << n) - 1
    val size = 1 << order

    desc + " should act like a circular stack" in {
      simulate(dutgen()) { dut =>

        // Initialise regs
        dut.reset.poke(true.B)
        dut.clock.step()
        dut.reset.poke(false.B)
        dut.clock.step()

        // Push a value
        dut.io.en.poke(true.B)
        dut.io.wEn.poke(true.B)
        dut.io.nTosV.poke(7.U)
        dut.io.dITos.poke(1.U)
        dut.clock.step()
        dut.io.tosV.expect(7.U)

        // Push another value
        dut.io.en.poke(true.B)
        dut.io.wEn.poke(true.B)
        dut.io.nTosV.poke(8.U)
        dut.io.dITos.poke(1.U)
        dut.clock.step()
        dut.io.tosV.expect(8.U)
        dut.io.nosV.expect(7.U)

        // Overwrite TOS
        dut.io.en.poke(true.B)
        dut.io.wEn.poke(true.B)
        dut.io.nTosV.poke(9.U)
        dut.io.dITos.poke(0.U)
        dut.clock.step()
        dut.io.tosV.expect(9.U)
        dut.io.nosV.expect(7.U)

        // Push another value
        dut.io.en.poke(true.B)
        dut.io.wEn.poke(true.B)
        dut.io.nTosV.poke(10.U)
        dut.io.dITos.poke(1.U)
        dut.clock.step()
        dut.io.tosV.expect(10.U)
        dut.io.nosV.expect(9.U)

        // Pop a value
        dut.io.en.poke(true.B)
        dut.io.wEn.poke(false.B)
        dut.io.nTosV.poke(0.U)
        dut.io.dITos.poke(((1<<order)-1).U)
        dut.clock.step()
        dut.io.tosV.expect(9.U)
        dut.io.nosV.expect(7.U)
      }
    }
  }

  // 32-bit size 4
  dotest(() => new Stack(32, 2), "stack32x4", 32, 2)
}
