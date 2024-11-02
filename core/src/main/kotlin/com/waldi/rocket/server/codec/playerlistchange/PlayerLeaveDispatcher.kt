package com.waldi.rocket.server.codec.playerlistchange

import com.waldi.rocket.server.gamestate.Player
import com.waldi.rocket.server.gamestate.events.GameStateListener
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame

class PlayerLeaveDispatcher : GameStateListener {
    override fun update(playersToNotify: Collection<Player>, changedPlayers: Collection<Player>) {
        changedPlayers.stream().findFirst().map { encode(it) }
            .ifPresent { playerId ->
                send(playerId, playersToNotify)
            }

    }

    private fun send(bytesToSend: ByteBuf, playersToNotify: Collection<Player>) {
        try {
            for (p in playersToNotify) {
                p.playerChannel.writeAndFlush(BinaryWebSocketFrame(bytesToSend.retain()))
            }
        } finally {
            bytesToSend.release();
        }
    }

    private fun encode(player: Player): ByteBuf { //todo make support list, and in forontend
        val buffer = Unpooled.buffer(6)
        buffer.writeByte(0x03);
        buffer.writeBytes(player.id.toByteArray());
        return buffer;
    }
}
