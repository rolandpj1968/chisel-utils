/*
 * https://en.wikipedia.org/wiki/SHA-2
 */

package org.clanpj.chisel.util.sha256

import chisel3._
import chisel3.util._

object Sha256Round {
  class State extends Bundle {
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
}

// For use in fully unrolled SHA256
class Sha256Round(round: Int, reg: Boolean) extends Module {
  import Sha256Round._
  import Sha256._

  assert(0 <= round && round < 64)

  val in1 = IO(Input(new State))
  val out1 = IO(Output(new State))

  // TODO - really need to work out Bundles, Wires, Input, Output :(
  // val in = IO(new Bundle {
  //   val w = Input(Vec(32, UInt(32.W)))
  //   val a = Input(UInt(32.W))
  //   val b = Input(UInt(32.W))
  //   val c = Input(UInt(32.W))
  //   val d = Input(UInt(32.W))
  //   val e = Input(UInt(32.W))
  //   val f = Input(UInt(32.W))
  //   val g = Input(UInt(32.W))
  //   val h = Input(UInt(32.W))
  // })
  val out = IO(new Bundle {
    val w = Output(Vec(32, UInt(32.W)))
    val a = Output(UInt(32.W))
    val b = Output(UInt(32.W))
    val c = Output(UInt(32.W))
    val d = Output(UInt(32.W))
    val e = Output(UInt(32.W))
    val f = Output(UInt(32.W))
    val g = Output(UInt(32.W))
    val h = Output(UInt(32.W))
  })

  val s = Wire(new State)
  s := in1

  val (w, a, b, c, d, e, f, g, h) = (s.w, s.a, s.b, s.c, s.d, s.e, s.f, s.g, s.h)

  // val w = in.w

  // val a = in.a
  // val b = in.b
  // val c = in.c
  // val d = in.d
  // val e = in.e
  // val f = in.f
  // val g = in.g
  // val h = in.h

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

  val tmp = Wire(new State)
  tmp.w := w1

  tmp.a := a1
  tmp.b := b1
  tmp.c := c1
  tmp.d := d1
  tmp.e := e1
  tmp.f := f1
  tmp.g := g1
  tmp.h := h1

  val w2 = Wire(Vec(32, UInt(32.W)))
  val a2 = Wire(UInt(32.W))
  val b2 = Wire(UInt(32.W))
  val c2 = Wire(UInt(32.W))
  val d2 = Wire(UInt(32.W))
  val e2 = Wire(UInt(32.W))
  val f2 = Wire(UInt(32.W))
  val g2 = Wire(UInt(32.W))
  val h2 = Wire(UInt(32.W))

  if (reg) {
    val sReg = Reg(new State)
    sReg := tmp
    out1 := sReg

    val wReg = Reg(Vec(32, UInt(32.W))); w2 := wReg; wReg := w1

    val aReg = Reg(UInt(32.W)); a2 := aReg; aReg := a1
    val bReg = Reg(UInt(32.W)); b2 := bReg; bReg := b1
    val cReg = Reg(UInt(32.W)); c2 := cReg; cReg := c1
    val dReg = Reg(UInt(32.W)); d2 := dReg; dReg := d1
    val eReg = Reg(UInt(32.W)); e2 := eReg; eReg := e1
    val fReg = Reg(UInt(32.W)); f2 := fReg; fReg := f1
    val gReg = Reg(UInt(32.W)); g2 := gReg; gReg := g1
    val hReg = Reg(UInt(32.W)); h2 := hReg; hReg := h1
  } else {
    out1 := tmp

    w2 := w1

    a2 := a1
    b2 := b1
    c2 := c1
    d2 := d1
    e2 := e1
    f2 := f1
    g2 := g1
    h2 := h1
  }

  out.w := w2

  out.a := a2
  out.b := b2
  out.c := c2
  out.d := d2
  out.e := e2
  out.f := f2
  out.g := g2
  out.h := h2
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
