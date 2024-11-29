package com.waldi.rocket.server.gamestate

import com.waldi.rocket.shared.GameController
import com.waldi.rocket.shared.MapData
import com.waldi.rocket.shared.RocketData
import io.netty.channel.Channel

interface GameServerState {
    fun getAllPlayers(): List<Player>;
    fun hasSession(sessionId: String): Boolean;
    fun getPlayerBySessionId(sessionId: String): Player?;
    fun getPlayerByGameId(gameId: String): Player?;
    fun initNewPlayer(sessionId: String, channel: Channel):Player;
    fun refreshPlayer(oldSessionId: String, newSessionId: String, channel: Channel): Player;
    fun joinGame(sessionId: String);
    fun removePlayer(sessionId: String);
    fun setController(gameController: GameController);
    fun getMapData(): MapData?;
    fun updateRocketsPositionsBatched(timestampToRocketDataList: Map<Int, List<RocketData>>)
    fun restartRocket(sessionId: String)
}
