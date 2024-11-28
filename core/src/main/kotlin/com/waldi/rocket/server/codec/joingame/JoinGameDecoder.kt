package com.waldi.rocket.server.codec.resetplayer

import com.waldi.rocket.server.codec.MessageDecoder
import io.netty.buffer.ByteBuf

class JoinGameDecoder: MessageDecoder<JoinGame> {
    override fun decode(bytes: ByteBuf): JoinGame {
        return JoinGame();
    }
}
