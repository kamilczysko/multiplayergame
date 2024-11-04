package com.waldi.rocket.server.gamestate.events

import com.waldi.rocket.server.gamestate.EXECUTORS
import com.waldi.rocket.server.gamestate.Player
import java.util.concurrent.ConcurrentHashMap

class GameStateManager {

    private val listeners = ConcurrentHashMap<GameStateEventType, MutableList<GameStateListener>>();

    fun subscribe(gameStateSubscriber: GameStateListener, eventType: GameStateEventType) {
        listeners.getOrPut(eventType) { mutableListOf() }.add(gameStateSubscriber);
    }

    fun notify(eventType: GameStateEventType, playersToNotify: Collection<Player>, playerData: Collection<Player>) {
        listeners[eventType]?.forEach {
            EXECUTORS.execute { it.update(playersToNotify, playerData) }
        }
    }
}
