package com.waldi.rocket.gameengine

import com.waldi.rocket.gameengine.gameworld.GameWorld
import com.waldi.rocket.gameengine.enginestate.EngineState
import com.waldi.rocket.server.bootstrapServer
import com.waldi.rocket.server.codec.playerlistchange.PlayerLeaveDispatcher
import com.waldi.rocket.server.codec.playerlistchange.PlayerListChangeDispatcher
import com.waldi.rocket.server.gamestate.GameState
import com.waldi.rocket.server.gamestate.InMemoryGameState
import com.waldi.rocket.server.gamestate.events.GameStateEventType
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        val engineState = EngineState();

        val gameState: GameState = InMemoryGameState.getInstance();
        gameState.addGameEventBus(engineState);
        gameState.addListener(PlayerListChangeDispatcher(), GameStateEventType.PLAYER_LIST_UPDATE);
        gameState.addListener(PlayerLeaveDispatcher(), GameStateEventType.PLAYER_LEAVE);

        val gameWorld = GameWorld(engineState, gameState);

        KtxAsync.initiate()
        addScreen(FirstScreen(gameWorld))
        setScreen<FirstScreen>()

        bootstrapServer(gameState)

        gameState
    }
}

class FirstScreen(private val gameWorld: GameWorld) : KtxScreen {
    override fun render(delta: Float) {
        gameWorld.render();
    }

    override fun dispose() {

    }
}
