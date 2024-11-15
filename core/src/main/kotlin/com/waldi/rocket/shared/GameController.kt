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

    fun initRocket(rocketName: String, rocketId: String) {
        gameWorld.initRocket(rocketName, rocketId)
    }

    fun removeRocket(rocketId: String) {
        gameWorld.removeRocket(rocketId);
    }

    fun getMap(): MapData {
        return gameWorld.getMap();
    }

    fun startAccelerating(rocketId: String) {
        gameWorld.startAccelerating(rocketId);
    }
    fun stopAccelerating(rocketId: String) {
        gameWorld.stopAccelerating(rocketId);

    }
    fun rotate(rocketId: String, angle: Float) {
        gameWorld.rotate(rocketId, angle);
    }

    fun notifyAboutGameState(rocketData: List<RocketPositionData>) {
        gameServerState.getAllPlayers().stream().forEach {
            it.playerChannel.writeAndFlush("sdfsdfsf") //todo implement, maybe method to encode also and send
        }
    }

}