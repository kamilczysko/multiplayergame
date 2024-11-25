package com.waldi.rocket.server.codec.restartPLAYER

import com.waldi.rocket.server.codec.MessageDecoder
import io.netty.buffer.ByteBuf

class RestartPlayerDecoder: MessageDecoder<RestartPlayer> {
    override fun decode(bytes: ByteBuf): RestartPlayer {
        return RestartPlayer();
    }
}
