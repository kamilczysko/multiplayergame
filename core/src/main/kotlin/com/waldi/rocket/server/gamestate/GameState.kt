package com.waldi.rocket.server.gamestate

import com.waldi.rocket.shared.GameController
import com.waldi.rocket.shared.MapData
import io.netty.channel.Channel

interface GameState {
    fun getAllPlayers(): List<Player>;
    fun hasSession(sessionId: String): Boolean;
    fun getPlayerBySessionId(sessionId: String): Player?;
    fun getPlayerByGameId(gameId: String): Player?;
    fun addOrUpdatePlayer(oldSessionId: String, newSessionId: String, name: String, channel: Channel): Player;
    fun removePlayer(sessionId: String);
    fun setController(gameController: GameController);
    fun getMapData(): MapData?;
}
