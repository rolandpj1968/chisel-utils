package org.clanpj.chisel.mam.stack

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
        dut.io.wEn.poke(1.U)
        dut.io.nextTos.poke(7.U)
        dut.io.dItos.poke(1.U)
        dut.clock.step()
        dut.io.tos.expect(7.U)

        // Push another value
        dut.io.wEn.poke(1.U)
        dut.io.nextTos.poke(8.U)
        dut.io.dItos.poke(1.U)
        dut.clock.step()
        dut.io.tos.expect(8.U)
        dut.io.nos.expect(7.U)

        // Overwrite TOS
        dut.io.wEn.poke(1.U)
        dut.io.nextTos.poke(9.U)
        dut.io.dItos.poke(0.U)
        dut.clock.step()
        dut.io.tos.expect(9.U)
        dut.io.nos.expect(7.U)

        // Push another value
        dut.io.wEn.poke(1.U)
        dut.io.nextTos.poke(10.U)
        dut.io.dItos.poke(1.U)
        dut.clock.step()
        dut.io.tos.expect(10.U)
        dut.io.nos.expect(9.U)

        // Pop a value
        dut.io.wEn.poke(0.U)
        dut.io.nextTos.poke(0.U)
        dut.io.dItos.poke(((1<<order)-1).U)
        dut.clock.step()
        dut.io.tos.expect(9.U)
        dut.io.nos.expect(7.U)
      }
    }
  }

  // 32-bit size 4
  dotest(() => new Stack(32, 2), "stack32x4", 32, 2)
}
