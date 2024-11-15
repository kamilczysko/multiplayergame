package com.waldi.rocket.server.gamestate

import io.netty.channel.Channel
import java.util.UUID

class Player(private var name: String, private var sessionId: String, private var channel: Channel) {
    val gameId: String = UUID.randomUUID().mostSignificantBits.toString().replace("-", "").take(5)
    private var points: Int = 0;
    private var fuel: Float = 1.0f;

    var playerName: String
        get() = this.name;
        set(newName) { this.name = newName; }

    var playerSessionId: String
        get() = this.sessionId;
        set(newSession) { this.sessionId = newSession; }

    var playerChannel: Channel
        get() = channel;
        set(newChannel) { this.channel = newChannel; }

    fun addPoint() {
        points ++;
    }

    fun getPoints(): Int {
        return points;
    }

    fun getFuel(): Float {
        return this.fuel;
    }

    fun setFuel(fuelLevel: Float) {
        this.fuel = fuelLevel;
    }

    override fun toString(): String {
        return "Player(name='$name', sessionId='$sessionId', gameId='$gameId')"
    }
}
