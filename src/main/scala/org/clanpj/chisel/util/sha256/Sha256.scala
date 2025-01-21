/*
 * https://en.wikipedia.org/wiki/SHA-2
 */

package org.clanpj.chisel.util.sha256

import chisel3._
import chisel3.util._

object Sha256Round {
  class State extends Bundle {
    val valid = Bool()

    val w = Vec(32, UInt(32.W))

    val a = UInt(32.W)
    val b = UInt(32.W)
    val c = UInt(32.W)
    val d = UInt(32.W)
    val e = UInt(32.W)
    val f = UInt(32.W)
    val g = UInt(32.W)
    val h = UInt(32.W)
  }

  def invalidState(): State = {
    val s = new State
    s.valid := false.B
    s
  }
}

// For use in fully unrolled SHA256
// TODO - include a "label", e.g. the nonce
class Sha256Round(round: Int, reg: Boolean) extends Module {
  import Sha256Round._
  import Sha256._

  assert(0 <= round && round < 64)

  val in = IO(Input(new State))
  val out = IO(Output(new State))

  val s = Wire(new State)
  s := in

  val (w, a, b, c, d, e, f, g, h) = (s.w, s.a, s.b, s.c, s.d, s.e, s.f, s.g, s.h)

  // msg schedule
  val w1 = Wire(Vec(32, UInt(32.W)))
  w1 := w

  if (round < 48) {
    val wm15 = w((round+16-15)%32)
    val s0 = R(wm15, 7) ^ R(wm15, 18) ^ (wm15 >> 3);
    val wm2 = w((round+16-2)%32)
    val s1 = R(wm2, 17) ^ R(wm2, 19) ^ (wm2 >> 10);
    val wm7 = w((round+16-7)%32)
    w1((round+16)%32) := w(round%32) + s0 + wm7 + s1;
  }

  // sha256 round
  val S1 = R(e, 6) ^ R(e, 11) ^ R(e, 25)
  val ch = (e & f) ^ ((~e) & g)
  val temp1 = h + S1 + ch + k(round).U(32.W) + w(round%32)
  val S0 = R(a, 2) ^ R(a, 13) ^ R(a, 22)
  val maj = (a & b) ^ (a & c) ^ (b & c)
  val temp2 = S0 + maj

  val (h1, g1, f1, e1, d1, c1, b1, a1) = (g, f, e, d + temp1, c, b, a, temp1 + temp2)

  val s1 = Wire(new State)
  s1.valid := in.valid
  s1.w := w1
  s1.a := a1; s1.b := b1; s1.c := c1; s1.d := d1
  s1.e := e1; s1.f := f1; s1.g := g1; s1.h := h1

  if (reg) {
    val sReg = RegInit(invalidState())
    sReg := s1
    out := sReg
  } else {
    out := s1
  }
}

// TODO - need pass-thru "label" for registered case
class Sha256Core(reg: Boolean) extends Module {
  val in = IO(new Bundle {
    val h = Input(Vec(8, UInt(32.W)))
    val msg = Input(Vec(16, UInt(32.W)))
  })
  val out = IO(new Bundle {
    val valid = Output(Bool())
    val h = Output(Vec(8, UInt(32.W)))
  })

  val round = (0 to 63).map(round => Module(new Sha256Round(round, reg)))

  val w0 = Wire(Vec(32, UInt(32.W)))
  for (i <- 0 to 15) { w0(i) := in.msg(i) }
  for (i <- 16 to 31) { w0(i) := 0.U }

  val s0 = Wire(new Sha256Round.State)
  s0.valid := true.B
  s0.w := w0
  s0.a := in.h(0)
  s0.b := in.h(1)
  s0.c := in.h(2)
  s0.d := in.h(3)
  s0.e := in.h(4)
  s0.f := in.h(5)
  s0.g := in.h(6)
  s0.h := in.h(7)

  round(0).in := s0
  for (i <- 1 to 63) { round(i).in := round(i-1).out }

  out.valid := round(63).out.valid
  out.h(0) := in.h(0) + round(63).out.a
  out.h(1) := in.h(1) + round(63).out.b
  out.h(2) := in.h(2) + round(63).out.c
  out.h(3) := in.h(3) + round(63).out.d
  out.h(4) := in.h(4) + round(63).out.e
  out.h(5) := in.h(5) + round(63).out.f
  out.h(6) := in.h(6) + round(63).out.g
  out.h(7) := in.h(7) + round(63).out.h
}


object Sha256 {
  def bix(s: String) = BigInt(s, 16)

  def R(v: UInt, n: Int) = Cat(v(n-1, 0), v(32-1, n))

  val k = Seq(
	bix("428a2f98"), bix("71374491"), bix("b5c0fbcf"), bix("e9b5dba5"),
	bix("3956c25b"), bix("59f111f1"), bix("923f82a4"), bix("ab1c5ed5"),
	bix("d807aa98"), bix("12835b01"), bix("243185be"), bix("550c7dc3"),
	bix("72be5d74"), bix("80deb1fe"), bix("9bdc06a7"), bix("c19bf174"),
	bix("e49b69c1"), bix("efbe4786"), bix("0fc19dc6"), bix("240ca1cc"),
	bix("2de92c6f"), bix("4a7484aa"), bix("5cb0a9dc"), bix("76f988da"),
	bix("983e5152"), bix("a831c66d"), bix("b00327c8"), bix("bf597fc7"),
	bix("c6e00bf3"), bix("d5a79147"), bix("06ca6351"), bix("14292967"),
	bix("27b70a85"), bix("2e1b2138"), bix("4d2c6dfc"), bix("53380d13"),
	bix("650a7354"), bix("766a0abb"), bix("81c2c92e"), bix("92722c85"),
	bix("a2bfe8a1"), bix("a81a664b"), bix("c24b8b70"), bix("c76c51a3"),
	bix("d192e819"), bix("d6990624"), bix("f40e3585"), bix("106aa070"),
	bix("19a4c116"), bix("1e376c08"), bix("2748774c"), bix("34b0bcb5"),
	bix("391c0cb3"), bix("4ed8aa4a"), bix("5b9cca4f"), bix("682e6ff3"),
	bix("748f82ee"), bix("78a5636f"), bix("84c87814"), bix("8cc70208"),
	bix("90befffa"), bix("a4506ceb"), bix("bef9a3f7"), bix("c67178f2"),
  )

  val h = Seq(
	bix("6a09e667"), bix("bb67ae85"), bix("3c6ef372"), bix("a54ff53a"),
	bix("510e527f"), bix("9b05688c"), bix("1f83d9ab"), bix("5be0cd19"),
  )

}
