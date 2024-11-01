package com.waldi.rocket.server.codec.message

import io.netty.buffer.ByteBuf

interface MessageDecoder<T> {
    fun decode(bytes: ByteBuf): T;
}
