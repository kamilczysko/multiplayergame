package com.waldi.rocket.server.gamestate

import com.waldi.rocket.gameengine.objects.rocket.RocketListener
import com.waldi.rocket.server.gamestate.events.GameStateEventType
import com.waldi.rocket.server.gamestate.events.GameStateListener
import com.waldi.rocket.shared.gamebus.GameEventBus
import com.waldi.rocket.shared.gamebus.MapData
import io.netty.channel.Channel

interface GameState: RocketListener {
    fun updatePlayerSession(prevSessionId: String, actualSessionId: String, newName: String, actualChannel: Channel);
    fun getPlayerGameId(sessionId: String): String?;
    fun addNewPlayer(name: String, newSessionId: String, channel: Channel): Player;
    fun hasSession(sessionId: String): Boolean;
    fun getAllPlayers(): List<Player>;
    fun getPlayerBySessionId(sessionId: String): Player?
    fun addListener(listener: GameStateListener, eventType: GameStateEventType);
    fun removePlayer(sessionId: String);
    fun addGameEventBus(bus: GameEventBus);
    fun addPoint(playerId: String)
    fun initMap();
    fun getMapData(): MapData;
    fun resetMap();
}
