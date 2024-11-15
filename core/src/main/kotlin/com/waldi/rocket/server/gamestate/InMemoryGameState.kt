package com.waldi.rocket.server.gamestate

import com.waldi.rocket.shared.GameController
import com.waldi.rocket.shared.MapData
import com.waldi.rocket.shared.RocketPositionData
import io.netty.channel.Channel
import java.util.concurrent.ConcurrentHashMap

class InMemoryGameState: GameState {
    private var sessionIdToPlayer = ConcurrentHashMap<String, Player>();

    private lateinit var gameController: GameController;

    override fun setController(gameController: GameController) {
        this.gameController = gameController;
    }

    override fun getMapData(): MapData {
        return gameController.getMap();
    }

    override fun getAllPlayers(): List<Player> {
        return sessionIdToPlayer.values.stream().toList();
    }

    override fun hasSession(sessionId: String): Boolean {
        return sessionIdToPlayer.contains(sessionId);
    }

    override fun getPlayerBySessionId(sessionId: String): Player? {
        return sessionIdToPlayer[sessionId];
    }

    override fun getPlayerByGameId(gameId: String): Player? {
        return sessionIdToPlayer.values.stream()
            .filter { it.gameId == gameId }
            .findAny().orElseGet { null };
    }

    override fun addOrUpdatePlayer(oldSessionId: String, newSessionId: String, name: String, channel: Channel): Player {
        if(oldSessionId.isBlank() || !sessionIdToPlayer.containsKey(newSessionId)) {
            return createNewPlayer(name, newSessionId, channel)
        }
        return refreshSessionForPlayer(oldSessionId, name, channel, newSessionId)
    }

    override fun removePlayer(sessionId: String) {
        val playerToRemove = sessionIdToPlayer[sessionId] ?: return;

        gameController.removeRocket(playerToRemove.gameId);

        sessionIdToPlayer.remove(sessionId);
    }

    private fun createNewPlayer(name: String, newSessionId: String, channel: Channel): Player {
        val newPlayer = Player(name, newSessionId, channel);
        sessionIdToPlayer[newSessionId] = newPlayer;

        gameController.initRocket(newPlayer.playerName, newPlayer.gameId); //name is unnecessary, todo remove later

        return newPlayer;
    }

    private fun refreshSessionForPlayer(oldSessionId: String, name: String, channel: Channel, newSessionId: String): Player {
        val player = sessionIdToPlayer[oldSessionId]!!
        player.playerName = name;
        player.playerChannel = channel;
        sessionIdToPlayer[newSessionId] = player;
        sessionIdToPlayer.remove(oldSessionId);

        return player;
    }

    fun updateRocketsPositions(rocketData: List<RocketPositionData>) {
        TODO("Not yet implemented")
    }

}
