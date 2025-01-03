/*
 * Carry-select n-bit full-adder
 * 
 *   https://en.wikipedia.org/wiki/Carry-select_adder
 */

package org.clanpj.chisel.fulladdern

import chisel3._
import chisel3.util.Cat

class CSelFullAdderN[HiT <: FullAdderN, LoT <: FullAdderN](n: Int, nhi: Int, higen: (Int) => HiT, logen: (Int) => LoT) extends FullAdderN(n) {
  assert(nhi > 0)
  assert(nhi < n)

  val nlo = n-nhi
  val lo = Module(logen(nlo))
  lo.io.x := io.x(nlo-1, 0)
  lo.io.y := io.y(nlo-1, 0)
  lo.io.cin := io.cin

  val hi0 = Module(higen(nhi))
  hi0.io.x := io.x(n-1, nlo)
  hi0.io.y := io.y(n-1, nlo)
  hi0.io.cin := 0.U

  val hi1 = Module(higen(nhi))
  hi1.io.x := io.x(n-1, nlo)
  hi1.io.y := io.y(n-1, nlo)
  hi1.io.cin := 1.U

  io.cout := Mux(lo.io.cout(0), hi1.io.cout, hi0.io.cout)
  val hisum = Mux(lo.io.cout(0), hi1.io.sum, hi0.io.sum)
  io.sum := Cat(hisum, lo.io.sum)

  //printf(desc + ": x %d y %d cin %d -> sum %d cout %d\n", io.x, io.y, io.cin, io.sum, io.cout)

  def desc = "csel" + n + "[" + hi0.desc + "," + lo.desc + "]"
}
