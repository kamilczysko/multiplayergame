package com.waldi.rocket.server.gamestate

import io.netty.channel.Channel
import java.util.UUID

class Player(private var name: String, private var sessionId: String, private var channel: Channel) {
    val gameId: String = UUID.randomUUID().mostSignificantBits.toString().replace("-", "").take(5)

    var playerName: String
        get() = this.name;
        set(newName) { this.name = newName; }

    var playerSessionId: String
        get() = this.sessionId;
        set(newSession) { this.sessionId = newSession; }

    var playerChannel: Channel
        get() = channel;
        set(newChannel) { this.channel = newChannel; }

    val id: String get() = this.gameId;

}
