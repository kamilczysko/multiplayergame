package com.waldi.rocket.server.codec

import com.waldi.rocket.server.codec.leaveplayer.LeavePlayerDecoder
import com.waldi.rocket.server.codec.newplayer.CreateNewPlayerDecoder
import com.waldi.rocket.server.codec.resetplayer.JoinGameDecoder
import com.waldi.rocket.server.codec.resetplayer.ResetPlayerDecoder
import com.waldi.rocket.server.codec.steering.SteerDecoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame

class GameDecoder : MessageToMessageDecoder<BinaryWebSocketFrame>() {
    private val decoders: HashMap<Byte, (ByteBuf) -> Message> = hashMapOf(
        0x00.toByte() to { buffer -> JoinGameDecoder().decode(buffer) },
        0x01.toByte() to { buffer -> CreateNewPlayerDecoder().decode(buffer) },
        0x03.toByte() to { buffer -> LeavePlayerDecoder().decode(buffer) },
        0x06.toByte() to { buffer -> SteerDecoder().decode(buffer) },
        0x07.toByte() to { buffer -> ResetPlayerDecoder().decode(buffer) }
    )

    override fun decode(p0: ChannelHandlerContext?, p1: BinaryWebSocketFrame?, p2: MutableList<Any>?) {
        if (p1 == null) {
            return;
        }
        val decoder = decoders[p1.content().readByte()]
        val message = decoder?.invoke(p1.content().readerIndex(1)) ?: return;

        p0?.fireChannelRead(message)
    }
}
