package com.waldi.rocket.shared

import com.waldi.rocket.gameengine.gameworld.GameWorld
import com.waldi.rocket.server.gamestate.GameServerState
import com.waldi.rocket.server.gamestate.InMemoryGameServerState

class GameController() {
    val gameWorld: GameWorld = GameWorld();
    val gameServerState: GameServerState = InMemoryGameServerState();

    init {
        this.gameWorld.setController(this);
        this.gameServerState.setController(this);
    }

    fun initRocket(rocketId: String) {
        gameWorld.initRocket(rocketId)
    }

    fun removeRocket(rocketId: String) {
        gameWorld.removeRocket(rocketId);
    }

    fun getMap(): MapData {
        return gameWorld.getMap();
    }

    fun notifyAboutGameState(timestampToRocketDataList: Map<Int, List<RocketData>>) {
        gameServerState.updateRocketsPositionsBatched(timestampToRocketDataList);
    }

    fun resetRocket(playerId: String) {
        gameWorld.resetRocketById(playerId);
    }

}
