package com.waldi.rocket.server.gamestate

import com.waldi.rocket.server.gamestate.events.GameStateEventType
import com.waldi.rocket.server.gamestate.events.GameStateListener
import com.waldi.rocket.server.gamestate.events.GameStateManager
import io.netty.channel.Channel
import java.util.concurrent.ConcurrentHashMap

class InMemoryGameState private constructor(): GameState {

    private val sessionIdToPlayer: ConcurrentHashMap<String, Player> = ConcurrentHashMap();
    private val sessionManager: GameStateManager = GameStateManager();

    companion object {
        private val instance: InMemoryGameState = InMemoryGameState();
        fun getInstance() : InMemoryGameState {
            return instance;
        }
    }

    override fun getPlayerBySessionId(sessionId: String): Player? {
        return sessionIdToPlayer[sessionId];
    }

    override fun addListener(listener: GameStateListener, eventType: GameStateEventType) {
        sessionManager.subscribe(listener, eventType);
    }

    override fun removePlayer(sessionId: String) {
        val playerToRemote = sessionIdToPlayer[sessionId] ?: return;
        sessionIdToPlayer.remove(sessionId);
        sessionManager.notify(GameStateEventType.PLAYER_LEAVE, sessionIdToPlayer.values, listOf(playerToRemote))
    }

    override fun updatePlayerSession(prevSessionId: String, actualSessionId: String, newName: String, actualChannel: Channel) {
        val player = sessionIdToPlayer[prevSessionId] ?: return;
        player.playerSessionId = actualSessionId;
        player.playerName = newName;
        player.playerChannel = actualChannel;

        sessionIdToPlayer.remove(prevSessionId);
        sessionIdToPlayer[actualSessionId] = player;
        sessionManager.notify(GameStateEventType.PLAYER_LIST_UPDATE, listOf(player), sessionIdToPlayer.values);

    }

    override fun getPlayerGameId(sessionId: String): String? {
        return sessionIdToPlayer[sessionId]?.gameId
    }

    override fun addNewPlayer(name: String, newSessionId: String, channel: Channel): Player {
        val newPlayer = Player(name, newSessionId, channel);
        sessionManager.notify(GameStateEventType.PLAYER_LIST_UPDATE, sessionIdToPlayer.values, listOf(newPlayer));
        sessionIdToPlayer[newSessionId] = newPlayer;
        sessionManager.notify(GameStateEventType.PLAYER_LIST_UPDATE, listOf(newPlayer), sessionIdToPlayer.values);
        return newPlayer;
    }

    override fun hasSession(sessionId: String): Boolean = sessionIdToPlayer.containsKey(sessionId);

    override fun getAllPlayers(): List<Player> {
        return sessionIdToPlayer.values.toList();
    }


}
