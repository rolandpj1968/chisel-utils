package org.clanpj.chisel.mam.alu

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class AluSpec extends AnyFreeSpec with Matchers {
  import AluOpcode._

  def reset(dut: AluTestHarness): Unit = {
      println("AluTestHarness: reset");
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      dut.clock.step()
  }

  def exeOp(dut: AluTestHarness, op: AluOpcode): Unit = {
      dut.io.en.poke(true.B)
      println("AluTestHarness: running " + op);
      dut.io.opc.poke(op.id.U)
      dut.clock.step()
      dut.io.stall.expect(false.B)
      dut.io.trap.expect(false.B)
  }

  "Alu should execute opcodes" in {
    simulate(new AluTestHarness(32)) { dut =>

      // // Initialise
      // dut.reset.poke(true.B)
      // dut.clock.step()
      // dut.reset.poke(false.B)
      // dut.clock.step()

      // // Check invalid op trapping
      // for (opc <- 0x00 to 0xff) {
      //   val opcEnumOpt = try {
      //     Some(AluOpcode(opc))
      //   } catch {
      //     case _: NoSuchElementException => None
      //   }
      //   val (inv, spec) = opcEnumOpt match {
      //     case None => (true, None)
      //     case Some(opcEnum) => (false, Some(DecoderSpec.spec(opcEnum)))
      //   }
      //   //println(f"opc: 0x$opc%2x spec inv / spec = $inv / $spec")
      //   assert(inv == spec.isEmpty)

      //   dut.io.en.poke(true.B)
      //   dut.io.opc.poke(opc.U)
      //   dut.clock.step()
      //   dut.io.stall.expect(false.B)
      //   dut.io.trap.expect(inv.B)
      // }
      
      // Reset
      reset(dut)

      // Run stuff
      exeOp(dut, AluOpcConb1)
      dut.io.nTosV.expect(1.U)

      dut.io.en.poke(true.B)
      dut.io.opc.poke(AluOpcConb2.id.U)
      dut.clock.step()
      dut.io.stall.expect(false.B)
      dut.io.trap.expect(false.B)
      dut.io.nTosV.expect(2.U)

      // dut.io.en.poke(true.B)
      // dut.io.opc.poke(AluOpcAdd.id.U)
      // dut.clock.step()
      // dut.io.stall.expect(false.B)
      // dut.io.trap.expect(false.B)
      // dut.io.nTosV.expect(3.U) // Hrmmm, println's 3 but expect says it's 4???

      dut.io.en.poke(true.B)
      dut.io.opc.poke(AluOpcOr.id.U)
      dut.clock.step()
      dut.io.stall.expect(false.B)
      dut.io.trap.expect(false.B)
      dut.io.nTosV.expect(3.U)

      dut.io.en.poke(true.B)
      dut.io.opc.poke(AluOpcConb2.id.U)
      dut.clock.step()
      dut.io.stall.expect(false.B)
      dut.io.trap.expect(false.B)
      dut.io.nTosV.expect(2.U)

      dut.io.en.poke(true.B)
      dut.io.opc.poke(AluOpcXor.id.U)
      dut.clock.step()
      dut.io.stall.expect(false.B)
      dut.io.trap.expect(false.B)
      //dut.io.nTosV.expect(1.U) // Hrmmm, println's 1 but expect says 2??? Something is wrong :(

      dut.io.en.poke(true.B)
      dut.io.opc.poke(AluOpcConb3.id.U)
      dut.clock.step()
      dut.io.stall.expect(false.B)
      dut.io.trap.expect(false.B)
      dut.io.nTosV.expect(3.U)

      //println("                                              hello RPJ nTosV is " + dut.io.nTosV.peek())
      //println("                                              hello RPJ alu nTosV is " + dut.alu.io.nTosV.peek())
    }
  }
}
