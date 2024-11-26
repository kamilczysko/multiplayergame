package com.waldi.rocket.server.codec.newplayer

import com.waldi.rocket.server.codec.MessageDecoder
import io.netty.buffer.ByteBuf
import io.netty.util.CharsetUtil

class CreateNewPlayerDecoder: MessageDecoder<CreateNewPlayer> {
    override fun decode(bytes: ByteBuf): CreateNewPlayer {
        val gameId = bytes.readBytes(5).toString(CharsetUtil.UTF_8);
        bytes.readerIndex(5);
        val sessionId = bytes.readBytes(bytes.readableBytes()).toString(CharsetUtil.UTF_8);
        return CreateNewPlayer(gameId, sessionId);
    }
}
