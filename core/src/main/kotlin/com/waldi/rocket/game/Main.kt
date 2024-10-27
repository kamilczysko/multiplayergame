package com.waldi.rocket.game

import com.waldi.rocket.game.gameworld.GameWorld
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val gameWorld = GameWorld();

    override fun render(delta: Float) {
        gameWorld.render();
    }

    override fun dispose() {

    }
}
