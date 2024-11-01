package com.waldi.rocket.server.gamestate.events

import com.waldi.rocket.server.gamestate.Player

class GameStateManager {

    private val listeners: MutableMap<GameStateEventType, MutableList<GameStateListener>> = mutableMapOf()

    fun subscribe(gameStateSubscriber: GameStateListener, eventType: GameStateEventType) {
        listeners.getOrPut(eventType) { mutableListOf() }.add(gameStateSubscriber);
    }

    fun notify(eventType: GameStateEventType, playersToNotify: Collection<Player>, playerData: Collection<Player>){
        listeners[eventType]?.forEach{ it.update(playersToNotify, playerData)}
    }
}
