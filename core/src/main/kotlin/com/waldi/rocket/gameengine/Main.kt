package com.waldi.rocket.gameengine

import com.waldi.rocket.gameengine.gameworld.GameWorld
import com.waldi.rocket.server.bootstrapServer
import com.waldi.rocket.shared.GameController
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

val GLOBAL_EXECUTOR: ExecutorService = Executors.newSingleThreadExecutor()

class Main : KtxGame<KtxScreen>() {

    override fun create() {

        Runtime.getRuntime().addShutdownHook(Thread{
            try {
                GLOBAL_EXECUTOR.shutdown();
                if (GLOBAL_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                    GLOBAL_EXECUTOR.shutdownNow()
                }
            } catch (exception: InterruptedException) {
                GLOBAL_EXECUTOR.shutdownNow();
            }
        })

        val gameController = GameController();

        KtxAsync.initiate()
        addScreen(FirstScreen(gameController.gameWorld))
        setScreen<FirstScreen>()

        GLOBAL_EXECUTOR.execute { bootstrapServer(gameController.gameServerState, gameController.gameWorld) };
    }
}

class FirstScreen(private val gameWorld: GameWorld) : KtxScreen {
    override fun render(delta: Float) {
        gameWorld.render();
    }

    override fun dispose() {
    }
}
