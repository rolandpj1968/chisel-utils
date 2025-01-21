/*
 * https://en.wikipedia.org/wiki/SHA-2
 */

package org.clanpj.chisel.util.sha256

import chisel3._
import chisel3.util._

// TODO - optional register
class Sha256Round(round: Int) extends Module {
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

  // msg schedule
  out.w := in.w

  if (round < 48) {
    val wm15 = in.w((round+16-15)%32)
    val s0 = R(wm15, 7) ^ R(wm15, 18) ^ (wm15 >> 3);
    val wm2 = in.w((round+16-2)%32)
    val s1 = R(wm2, 17) ^ R(wm2, 19) ^ (wm2 >> 10);
    val wm7 = in.w((round+16-7)%32)
    out.w((round+16)%32) := in.w(round%32) + s0 + wm7 + s1;
  }

  // sha256 round
  val S1 = R(in.e, 6) ^ R(in.e, 11) ^ R(in.e, 25)
  val ch = (in.e & in.f) ^ ((~in.e) & in.g)
  val temp1 = in.h + S1 + ch + k(round).U(32.W) + in.w(round%32)
  val S0 = R(in.a, 2) ^ R(in.a, 13) ^ R(in.a, 22)
  val maj = (in.a & in.b) ^ (in.a & in.c) ^ (in.b & in.c)
  val temp2 = S0 + maj

  out.h := in.g
  out.g := in.f
  out.f := in.e
  out.e := in.d + temp1
  out.d := in.c
  out.c := in.b
  out.b := in.a
  out.a := temp1 + temp2
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
