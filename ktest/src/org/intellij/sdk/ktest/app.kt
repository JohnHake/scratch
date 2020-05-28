package org.intellij.sdk.ktest

fun main(vararg args: String) {
  println("Hello! " + args.size + " arguments passed into the script!")
  if (args.size > 0) {
    for (i in 0..args.lastIndex)
    println("    Argument #" + i + " is: " + args[i].toString())
  }
}