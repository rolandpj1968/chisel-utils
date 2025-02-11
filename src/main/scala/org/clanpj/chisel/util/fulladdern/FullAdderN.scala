/*
 * Abstract N-bit Full Adder
 */

package org.clanpj.chisel.util.fulladdern

import chisel3._

abstract class FullAdderN(n: Int) extends Module {
  assert(n > 0)

  val io = IO(new Bundle {
    val cin = Input(UInt(1.W))
    val x = Input(UInt(n.W))
    val y = Input(UInt(n.W))
    val sum = Output(UInt(n.W))
    val cout = Output(UInt(1.W))
  })

  def width = n

  def desc: String
}

object FullAdderN {
  def simple(n: Int) = new SimpleFullAdderN(n)

  def csel[HiT <: FullAdderN, LoT <: FullAdderN](n: Int, nhi: Int, higen: (Int) => HiT, logen: (Int) => LoT) =
    new CSelFullAdderN(n, nhi, higen, logen)
  def csel[T <: FullAdderN](n: Int, nhi: Int, gen: (Int) => T): CSelFullAdderN[T,T] = csel(n, n/2, gen, gen)
  def csel[T <: FullAdderN](n: Int, gen: (Int) => T): CSelFullAdderN[T,T] = csel(n, n/2, gen)
  def csel(n: Int, nhi: Int): CSelFullAdderN[SimpleFullAdderN, SimpleFullAdderN] = csel(n, nhi, (n: Int) => simple(n))
  def csel(n: Int): CSelFullAdderN[SimpleFullAdderN, SimpleFullAdderN] = csel(n, n/2, (n: Int) => simple(n))

  def clkahd[HiT <: FullAdderN, LoT <: FullAdderN](n: Int, nhi: Int, higen: (Int) => HiT, logen: (Int) => LoT) =
    new CLookAheadFullAdderN(n, nhi, higen, logen)
  def clkahd[T <: FullAdderN](n: Int, nhi: Int, gen: (Int) => T): CLookAheadFullAdderN[T,T] = clkahd(n, n/2, gen, gen)
  def clkahd[T <: FullAdderN](n: Int, gen: (Int) => T): CLookAheadFullAdderN[T,T] = clkahd(n, n/2, gen)
  def clkahd(n: Int, nhi: Int): CLookAheadFullAdderN[SimpleFullAdderN, SimpleFullAdderN] = clkahd(n, nhi, (n: Int) => simple(n))
  def clkahd(n: Int): CLookAheadFullAdderN[SimpleFullAdderN, SimpleFullAdderN] = clkahd(n, n/2, (n: Int) => simple(n))
}
