package com.waldi.rocket.server.gamestate

import com.waldi.rocket.shared.GameController
import com.waldi.rocket.shared.MapData
import com.waldi.rocket.shared.RocketPositionData
import io.netty.channel.Channel
import mu.two.KotlinLogging
import java.util.concurrent.ConcurrentHashMap

class InMemoryGameServerState: GameServerState {
    private var sessionIdToPlayer = ConcurrentHashMap<String, Player>();

    private lateinit var gameController: GameController;
    private val logger = KotlinLogging.logger{};

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
        logger.info { "Players list before add new: ${sessionIdToPlayer.keys}" }
        if(oldSessionId.isBlank() || !sessionIdToPlayer.containsKey(oldSessionId)) {
            return createNewPlayer(name, newSessionId, channel)
        }
        return refreshSessionForPlayer(oldSessionId, name, channel, newSessionId)
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
        player.playerSessionId = newSessionId;
        sessionIdToPlayer[newSessionId] = player;
        sessionIdToPlayer.remove(oldSessionId);

        return player;
    }

    override fun removePlayer(sessionId: String) {
        val playerToRemove = sessionIdToPlayer[sessionId] ?: return;

        logger.info { "Players list before remove: ${sessionIdToPlayer.keys}" }

        gameController.removeRocket(playerToRemove.gameId);

        sessionIdToPlayer.remove(sessionId);
    }

    fun updateRocketsPositions(rocketData: List<RocketPositionData>) {
        TODO("Not yet implemented")
    }

}
