package com.waldi.rocket.server.codec.resetplayer

import com.waldi.rocket.server.codec.MessageDecoder
import io.netty.buffer.ByteBuf

class ResetPlayerDecoder: MessageDecoder<ResetPlayer> {
    override fun decode(bytes: ByteBuf): ResetPlayer {
        return ResetPlayer();
    }
}
