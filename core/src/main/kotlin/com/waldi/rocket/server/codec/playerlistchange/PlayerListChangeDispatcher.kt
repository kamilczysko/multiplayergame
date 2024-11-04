package com.waldi.rocket.server.codec.playerlistchange

import com.waldi.rocket.server.gamestate.Player
import com.waldi.rocket.server.gamestate.events.GameStateListener
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.group.DefaultChannelGroup
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.util.concurrent.GlobalEventExecutor
import java.util.stream.Collectors

class PlayerListChangeDispatcher : GameStateListener {
    override fun update(playersToNotify: Collection<Player>, changedPlayers: Collection<Player>) {
        val dataToSend = changedPlayers.stream()
            .map { player -> PlayerData(player.playerName, player.gameId) }
            .collect(Collectors.toList())

        val encodedDataToSend = encodeMessage(dataToSend);
        try {
            for (p in playersToNotify) {
                p.playerChannel.writeAndFlush(BinaryWebSocketFrame(encodedDataToSend.retain()));
            }
        } finally {
            encodedDataToSend.release();
        }
    }

    private fun encodeMessage(players: List<PlayerData>): ByteBuf {
        val sizeOfBuffer =
            players.stream().mapToInt() { player -> 1 + player.name.toByteArray().size + 5 + 1 }?.sum()!!;
        val buffer: ByteBuf = Unpooled.buffer(1 + sizeOfBuffer);
        buffer.writeByte(0x02)
        for (p in players) {
            val nameBytes = p.name.toByteArray();
            buffer.writeByte(nameBytes.size)
            buffer.writeBytes(nameBytes)
            buffer.writeBytes(p.gameId.toByteArray())
        }
        return buffer;
    }
}
