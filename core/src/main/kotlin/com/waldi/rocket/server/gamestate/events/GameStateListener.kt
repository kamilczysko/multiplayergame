package com.waldi.rocket.server.gamestate.events

import com.waldi.rocket.server.gamestate.Player

interface GameStateListener {
    fun update(playersToNotify: Collection<Player>, changedPlayers: Collection<Player>);
}
