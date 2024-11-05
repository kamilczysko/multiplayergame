package com.waldi.rocket.server.codec.playerlistchange

import com.waldi.rocket.server.gamestate.Player
import com.waldi.rocket.server.gamestate.events.GameStateListener
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import java.util.stream.Collectors

class PlayerListChangeDispatcher : GameStateListener {
    override fun update(playersToNotify: Collection<Player>, changedPlayers: Collection<Player>) {
        val dataToSend = changedPlayers.stream()
            .map { player -> PlayerData(player.playerName, player.gameId, player.getPoints(), player.getFuel()) }
            .collect(Collectors.toList())

        val encodedDataToSend = encodeMessage(dataToSend).retain();
        try {
            for (p in playersToNotify) {
                p.playerChannel.writeAndFlush(BinaryWebSocketFrame(encodedDataToSend));
            }
        } finally {
            encodedDataToSend.release();
        }
    }

    private fun encodeMessage(players: List<PlayerData>): ByteBuf {
        val sizeOfBuffer =
            players.stream().mapToInt() { player -> 1 + 5 + 2 + player.name.toByteArray().size }?.sum()!!;
        val buffer: ByteBuf = Unpooled.buffer(1 + sizeOfBuffer);
        buffer.writeByte(0x02)
        for (p in players) {
            buffer.writeBytes(p.gameId.toByteArray())
            buffer.writeByte((p.fuel * 100).toInt())
            buffer.writeByte(p.points)
            buffer.writeBytes(p.name.toByteArray())
        }
        return buffer;
    }
}
