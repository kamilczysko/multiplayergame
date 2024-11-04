package com.waldi.rocket.gameengine

import com.waldi.rocket.gameengine.gameworld.GameWorld
import com.waldi.rocket.gameengine.enginestate.EngineState
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

class Main : KtxGame<KtxScreen>() {
    override fun create() {
//        runServer();

        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val engineState = EngineState();
    private val gameWorld = GameWorld(engineState);

    override fun render(delta: Float) {
        gameWorld.render();
    }

    override fun dispose() {

    }
}
