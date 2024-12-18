package com.waldi.rocket.server.gamestate

import com.waldi.rocket.server.codec.gameplay.encode
import com.waldi.rocket.shared.GameController
import com.waldi.rocket.shared.MapData
import com.waldi.rocket.shared.RocketData
import io.netty.channel.Channel
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
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

    override fun refreshPlayer(oldSessionId: String, newSessionId: String, channel: Channel): Player {
        logger.info { "Players list before add new: ${sessionIdToPlayer.keys}" }
        if(oldSessionId.isBlank() || !sessionIdToPlayer.containsKey(oldSessionId)) {
            return initNewPlayer(newSessionId, channel)
        }
        return refreshSessionForPlayer(oldSessionId, channel, newSessionId)
    }

    override fun joinGame(sessionId: String) {
        val player = sessionIdToPlayer[sessionId];
        logger.info("JOIN GAME: "+player);
        if(player == null) {
            logger.info("Player does not exists")
            return;
        }
        gameController.initRocket(player.gameId);
    }

    override fun initNewPlayer(sessionId: String, channel: Channel): Player {
        val newPlayer = Player(sessionId, channel);
        sessionIdToPlayer[sessionId] = newPlayer;
        return newPlayer;
    }

    private fun refreshSessionForPlayer(oldSessionId: String, channel: Channel, newSessionId: String): Player {
        val player = sessionIdToPlayer[oldSessionId]!!
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

    override fun updateRocketsPositionsBatched(timestampToRocketDataList: Map<Int, List<RocketData>>) {

        val data = encode(timestampToRocketDataList);
        try {
            sessionIdToPlayer.values.
            forEach{player: Player ->  player.playerChannel.writeAndFlush(BinaryWebSocketFrame(data.retain().duplicate())) }
        } finally {
            data.release();
        }
    }

    override fun restartRocket(sessionId: String) {
        val playerId = sessionIdToPlayer[sessionId]?.gameId ?: return;
        gameController.resetRocket(playerId);
    }

}
