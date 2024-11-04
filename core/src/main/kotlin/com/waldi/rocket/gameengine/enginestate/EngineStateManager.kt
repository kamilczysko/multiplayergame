package com.waldi.rocket.gameengine.enginestate

import java.util.concurrent.ConcurrentHashMap

class EngineStateManager {

    private val listeners = ConcurrentHashMap<EngineStateEventType, MutableList<EngineStateListener>>();

    fun subscribe(listener: EngineStateListener, engineStateEventType: EngineStateEventType) {
        listeners.getOrPut(engineStateEventType) { mutableListOf() }.add(listener);
    }

    fun notify(type: EngineStateEventType, rocketId: String) {
        val listeners = listeners[type] ?: return
        listeners.stream().forEach { listener -> listener.update(rocketId) }
    }


}
