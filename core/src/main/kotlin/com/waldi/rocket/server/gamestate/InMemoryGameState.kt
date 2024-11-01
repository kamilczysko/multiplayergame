package com.waldi.rocket.server.gamestate

import io.netty.channel.Channel
import java.util.concurrent.ConcurrentHashMap

class InMemoryGameState private constructor(): GameState {

    companion object {
        private val instance: InMemoryGameState = InMemoryGameState();
        fun getInstance() : InMemoryGameState {
            return instance;
        }
    }

    private val playerSessionIdToName: ConcurrentHashMap<String, Player> = ConcurrentHashMap();

    fun getPlayerBySessionId(sessionId: String): Player? {
        return playerSessionIdToName[sessionId];
    }

    override fun updatePlayer(prevSessionId: String, actualSessionId: String, newName: String, actualChannel: Channel) {
        val player = playerSessionIdToName[prevSessionId] ?: return;
        player.playerSessionId = actualSessionId;
        player.playerName = newName;
        player.playerChannel = actualChannel;

        playerSessionIdToName.remove(prevSessionId);
        playerSessionIdToName[actualSessionId] = player;
    }

    override fun getPlayerGameId(sessionId: String): String? {
        return playerSessionIdToName[sessionId]?.gameId
    }

    override fun addNewPlayer(name: String, newSessionId: String, channel: Channel): Player {
        val newPlayer = Player(name, newSessionId, channel);
        playerSessionIdToName[newSessionId] = newPlayer;
        return newPlayer;
    }

    override fun hasSession(sessionId: String): Boolean = playerSessionIdToName.containsKey(sessionId);


}
