package com.waldi.rocket.server.codec.mapdata

import com.waldi.rocket.shared.MapData
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame

class MapDataEncoder: MessageToMessageEncoder<MapData>() {

    override fun encode(p0: ChannelHandlerContext?, p1: MapData?, p2: MutableList<Any>?) {
        p1 ?: return;
        p2?.add(BinaryWebSocketFrame(encode(p1)));
    }

    private fun encode(mapData: MapData): ByteBuf {
        val buffer = Unpooled.buffer(1 + 3 + (4 * mapData.platforms.size));
        buffer.writeByte(0x04)
        buffer.writeByte((mapData.moon.x).toInt())
        buffer.writeByte((mapData.moon.y).toInt())
        buffer.writeByte((mapData.moon.radius).toInt())

        for(platform in mapData.platforms) {
            buffer.writeByte(platform.x.toInt())
            buffer.writeByte(platform.y.toInt())
            buffer.writeByte(platform.width.toInt())
            buffer.writeByte(platform.height.toInt())
        }

        return buffer;
    }
}
