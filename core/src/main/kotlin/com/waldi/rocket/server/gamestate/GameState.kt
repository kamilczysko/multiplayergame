package com.waldi.rocket.server.gamestate

import io.netty.channel.Channel

interface GameState {
    fun updatePlayer(prevSessionId: String, actualSessionId: String, newName: String, actualChannel: Channel);
    fun getPlayerGameId(sessionId: String): String?;
    fun addNewPlayer(name: String, newSessionId: String, channel: Channel): Player;
    fun hasSession(sessionId: String): Boolean;
}
