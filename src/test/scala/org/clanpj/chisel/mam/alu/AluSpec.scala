package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class AluSpec extends AnyFreeSpec with Matchers {

  "Alu should execute opcodes" in {
    simulate(new AluTestHarness(32)) { dut =>

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
        dut.io.trap.expect(inv.B)
      }
      
    }
  }
}
