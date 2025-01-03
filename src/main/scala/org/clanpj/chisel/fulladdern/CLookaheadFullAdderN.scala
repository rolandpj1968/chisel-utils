/*
 * Carry-lookahead n-bit full-adder
 * 
 *   https://en.wikipedia.org/wiki/Carry-lookahead_adder
 */

package org.clanpj.chisel.fulladdern

import chisel3._
import chisel3.util.{Cat, scanLeftOr}

class CLookAheadFullAdderN[HiT <: FullAdderN, LoT <: FullAdderN](n: Int, nhi: Int, higen: (Int) => HiT, logen: (Int) => LoT) extends FullAdderN(n) {
  // TODO - make Option'al on ctor param
  val iogp = IO(new Bundle {
    val prop = Output(UInt(1.W))
    val genr = Output(UInt(1.W))
  })

  assert(nhi > 0)
  assert(nhi < n)

  // Chain prop/genr from nested carry-lookahead adders; otherwise compute them
  def propgenr[T <: FullAdderN](child: T, x: UInt, y: UInt) =
    // Ugh, but meh
    if (child.isInstanceOf[CLookAheadFullAdderN[_,_]]) {
      val lkahd = child.asInstanceOf[CLookAheadFullAdderN[_,_]]
      (lkahd.iogp.prop, lkahd.iogp.genr)
    } else {
      val xory = x | y
      val prop = xory.andR
      val genr = (scanLeftOr(x & y) | xory).andR
      (prop, genr)
    }

  val nlo = n-nhi
  val lo = Module(logen(nlo))
  val (lox, loy) = (io.x(nlo-1, 0), io.y(nlo-1, 0))
  lo.io.x := lox
  lo.io.y := loy
  lo.io.cin := io.cin

  val (loprop, logenr) = propgenr(lo, lox, loy)

  val hi = Module(higen(nhi))
  val (hix, hiy) = (io.x(n-1, nlo), io.y(n-1, nlo))
  hi.io.x := hix
  hi.io.y := hiy
  hi.io.cin := logenr | (loprop & io.cin) 

  val (hiprop, higenr) = propgenr(hi, hix, hiy)

  val prop = hiprop & loprop
  val genr = higenr | (hiprop & logenr)

  io.cout := genr | (prop & io.cin)
  io.sum := Cat(hi.io.sum, lo.io.sum)

  iogp.prop := prop
  iogp.genr := genr

  //printf(desc + ": x %d y %d cin %d -> sum %d cout %d\n", io.x, io.y, io.cin, io.sum, io.cout)

  def desc = "clkahd" + n + "[" + hi.desc + "," + lo.desc + "]"
}
