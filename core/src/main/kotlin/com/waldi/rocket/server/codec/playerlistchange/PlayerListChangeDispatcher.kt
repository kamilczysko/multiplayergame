package com.waldi.rocket.server.codec.playerlistchange

import com.waldi.rocket.server.gamestate.Player
import com.waldi.rocket.server.gamestate.events.GameStateListener
import io.netty.channel.group.DefaultChannelGroup
import io.netty.util.concurrent.GlobalEventExecutor
import java.util.stream.Collectors

class PlayerListChangeDispatcher : GameStateListener {
    override fun update(playersToNotify: Collection<Player>, changedPlayers: Collection<Player>) {
        val dataToSend = changedPlayers.stream()
            .map { player -> PlayerData(player.playerName, player.gameId, player.points) }
            .collect(Collectors.toList())

        val group = DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        for (p in playersToNotify) {
            group.add(p.playerChannel);
        }
        group.writeAndFlush(dataToSend);
    }
}
