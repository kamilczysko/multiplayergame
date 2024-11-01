package com.waldi.rocket.server.codec.newplayer

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame

class CreateNewPlayerEncoder: MessageToMessageEncoder<CreateNewPlayer>() {
    override fun encode(p0: ChannelHandlerContext?, p1: CreateNewPlayer?, p2: MutableList<Any>?) {
        if(p2 == null) {
            return;
        }
        val newPlayerName = p1?.name ?: return;
        val newPlayerNameBytes = newPlayerName.encodeToByteArray()
        val sessionId = p1.sessionId.encodeToByteArray();
        val gameId = p1.gameId.encodeToByteArray()

        val buffer = p0?.alloc()?.buffer(2 + sessionId.size + newPlayerNameBytes.size + gameId.size) ?: return;
        buffer.writeByte(0x01);
        buffer.writeByte(sessionId.size);
        buffer.writeBytes(sessionId);
        buffer.writeBytes(gameId)
        buffer.writeBytes(newPlayerNameBytes);

        p2.add(BinaryWebSocketFrame(buffer));
    }
}
/*1 - message type
* 2 - sessionId length
* 3 - ... - sessionId
* after - gameId - 5 bytes
* after - playerName */
