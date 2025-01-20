/*
 * Trivial n-bit shifter
 */

package org.clanpj.chisel.util.shiftern

import chisel3._
import chisel3.util._

class SimpleShifterN(n: Int) extends ShifterN(n) {
  val nbits = log2Ceil(n)
  val shift = io.y(nbits-1, 0)
  val over = io.y(n-1, nbits).orR
  val sign = io.x(n-1)

  when (over) {
    when (io.left || !io.arith) {
      io.v := 0.U
    } otherwise {
      io.v := Fill(n, sign)
    }
  } otherwise {
    when (io.left) {
      io.v := io.x << shift
    } otherwise {
      when (io.arith) {
        io.v := (io.x.asSInt >> shift).asUInt
      } otherwise {
        io.v := io.x >> shift
      }
    }
  }
  //printf("simple-shiftern: x %d y %d left %d arith %d -> v %d\n", io.x, io.y, io.left, io.arith, io.v)

  def desc = "simple" + n
}
