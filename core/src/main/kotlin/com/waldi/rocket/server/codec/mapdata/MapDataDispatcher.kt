package com.waldi.rocket.server.codec.mapdata

import com.waldi.rocket.server.gamestate.Player
import com.waldi.rocket.shared.gamebus.MapData
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

class MapDataDispatcher {
    fun sendMapData(playersToNotify: Collection<Player>, mapData: MapData) {
        val encodedData = encode(mapData).retain();
        try {
            playersToNotify.stream()
                .forEach { player ->
                    player.playerChannel.writeAndFlush(encodedData);
                }
        } finally {
            encodedData.release();
        }
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
