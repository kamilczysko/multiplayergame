package com.waldi.rocket.server.codec.playerlistchange

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame

class PlayerListChangeEncoder : MessageToMessageEncoder<List<PlayerData>>() {
    override fun encode(p0: ChannelHandlerContext?, p1: List<PlayerData>?, p2: MutableList<Any>?) {
        p2 ?: return;
        val sizeOfBuffer =
            p1?.stream()?.mapToInt() { player -> 1 + player.name.toByteArray().size + 5 + 1 }?.sum() ?: return;

        val buffer = p0?.alloc()?.buffer(1 + sizeOfBuffer) ?: return;
        buffer.writeByte(0x02)
        for (p in p1) {
            val nameBytes = p.name.toByteArray();
            buffer.writeByte(nameBytes.size)
            buffer.writeBytes(nameBytes)
            buffer.writeBytes(p.gameId.toByteArray())
            buffer.writeByte(p.points)
        }
        p2.add(BinaryWebSocketFrame(buffer));
    }

}
