package com.waldi.rocket.server

import java.util.*

fun main() {
    val a = UUID.randomUUID().mostSignificantBits.toString().replace("-", "").take(5)
    print(a.encodeToByteArray().size)
}
