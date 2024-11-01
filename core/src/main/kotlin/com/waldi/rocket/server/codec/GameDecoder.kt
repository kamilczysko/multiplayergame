package com.waldi.rocket.server.codec

import com.waldi.rocket.server.codec.newplayer.CreateNewPlayerDecoder
import com.waldi.rocket.server.codec.message.Message
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame

class GameDecoder : MessageToMessageDecoder<BinaryWebSocketFrame>() {
    private val decoders: HashMap<Byte, (ByteBuf) -> Message> =
        hashMapOf(0x01.toByte() to { buffer -> CreateNewPlayerDecoder().decode(buffer) })

    override fun decode(p0: ChannelHandlerContext?, p1: BinaryWebSocketFrame?, p2: MutableList<Any>?) {
        if (p1 == null) {
            return;
        }
        val decoder = decoders[p1.content().readByte()]
        val message = decoder?.invoke(p1.content().readerIndex(1)) ?: return;

        p2?.add(message);
    }
}
