package com.waldi.rocket.server.codec.newplayer

import com.waldi.rocket.server.codec.message.MessageDecoder
import io.netty.buffer.ByteBuf
import io.netty.util.CharsetUtil

class CreateNewPlayerDecoder: MessageDecoder<CreateNewPlayer> {
    override fun decode(bytes: ByteBuf): CreateNewPlayer {
        val numberOfBytesForName = bytes.readByte().toInt();
        bytes.readerIndex(2);
        val name = bytes.readBytes(numberOfBytesForName).toString(CharsetUtil.UTF_8)
        println("new name ${name}")
        bytes.readerIndex(numberOfBytesForName + 2)
        val gameId = bytes.readBytes(5).toString(CharsetUtil.UTF_8);
        println("gameId ${gameId}")

        bytes.readerIndex(numberOfBytesForName + 2 + 5);
        val sessionId = bytes.readBytes(bytes.readableBytes()).toString(CharsetUtil.UTF_8);
        println("${gameId} - ${name} - ${sessionId}")
        return CreateNewPlayer(name, gameId, sessionId);
    }
}