/*
 * https://en.wikipedia.org/wiki/SHA-2
 */

package org.clanpj.chisel.util.sha256

import chisel3._
import chisel3.util._

// For use in fully unrolled SHA256
class Sha256Round(round: Int, reg: Boolean) extends Module {
  import Sha256._

  assert(0 <= round && round < 64)

  // TODO - really need to work out Bundles, Wires, Input, Output :(
  val in = IO(new Bundle {
    val w = Input(Vec(32, UInt(32.W)))
    val a = Input(UInt(32.W))
    val b = Input(UInt(32.W))
    val c = Input(UInt(32.W))
    val d = Input(UInt(32.W))
    val e = Input(UInt(32.W))
    val f = Input(UInt(32.W))
    val g = Input(UInt(32.W))
    val h = Input(UInt(32.W))
  })
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

  val (xx, yy) = (1, 2)

  val w = in.w

  val a = in.a
  val b = in.b
  val c = in.c
  val d = in.d
  val e = in.e
  val f = in.f
  val g = in.g
  val h = in.h

  // msg schedule
  val w1 = w

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
  def bi(s: String) = BigInt(s)

  def R(v: UInt, n: Int) = Cat(v(n-1, 0), v(32-1, n))

  val k = Seq(
	bi("0x428a2f98"), bi("0x71374491"), bi("0xb5c0fbcf"), bi("0xe9b5dba5"),
	bi("0x3956c25b"), bi("0x59f111f1"), bi("0x923f82a4"), bi("0xab1c5ed5"),
	bi("0xd807aa98"), bi("0x12835b01"), bi("0x243185be"), bi("0x550c7dc3"),
	bi("0x72be5d74"), bi("0x80deb1fe"), bi("0x9bdc06a7"), bi("0xc19bf174"),
	bi("0xe49b69c1"), bi("0xefbe4786"), bi("0x0fc19dc6"), bi("0x240ca1cc"),
	bi("0x2de92c6f"), bi("0x4a7484aa"), bi("0x5cb0a9dc"), bi("0x76f988da"),
	bi("0x983e5152"), bi("0xa831c66d"), bi("0xb00327c8"), bi("0xbf597fc7"),
	bi("0xc6e00bf3"), bi("0xd5a79147"), bi("0x06ca6351"), bi("0x14292967"),
	bi("0x27b70a85"), bi("0x2e1b2138"), bi("0x4d2c6dfc"), bi("0x53380d13"),
	bi("0x650a7354"), bi("0x766a0abb"), bi("0x81c2c92e"), bi("0x92722c85"),
	bi("0xa2bfe8a1"), bi("0xa81a664b"), bi("0xc24b8b70"), bi("0xc76c51a3"),
	bi("0xd192e819"), bi("0xd6990624"), bi("0xf40e3585"), bi("0x106aa070"),
	bi("0x19a4c116"), bi("0x1e376c08"), bi("0x2748774c"), bi("0x34b0bcb5"),
	bi("0x391c0cb3"), bi("0x4ed8aa4a"), bi("0x5b9cca4f"), bi("0x682e6ff3"),
	bi("0x748f82ee"), bi("0x78a5636f"), bi("0x84c87814"), bi("0x8cc70208"),
	bi("0x90befffa"), bi("0xa4506ceb"), bi("0xbef9a3f7"), bi("0xc67178f2"),
  )

  val h = Seq(
	bi("0x6a09e667"), bi("0xbb67ae85"), bi("0x3c6ef372"), bi("0xa54ff53a"),
	bi("0x510e527f"), bi("0x9b05688c"), bi("0x1f83d9ab"), bi("0x5be0cd19"),
  )

}
