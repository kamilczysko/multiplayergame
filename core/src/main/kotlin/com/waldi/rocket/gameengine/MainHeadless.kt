package com.waldi.rocket.gameengine

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.physics.box2d.World
import com.waldi.rocket.gameengine.gameworld.GameWorld
import com.waldi.rocket.server.bootstrapServer
import com.waldi.rocket.shared.GameController
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

val GLOBAL_EXECUTOR2: ExecutorService = Executors.newSingleThreadExecutor()

class MainHeadless(): ApplicationAdapter() {

    private lateinit var gameWorld: GameWorld;

    init{

        Runtime.getRuntime().addShutdownHook(Thread{
            try {
                GLOBAL_EXECUTOR2.shutdown();
                if (GLOBAL_EXECUTOR2.awaitTermination(5, TimeUnit.SECONDS)) {
                    GLOBAL_EXECUTOR2.shutdownNow()
                }
            } catch (exception: InterruptedException) {
                GLOBAL_EXECUTOR2.shutdownNow();
            }
        })
        val gameController = GameController();
        gameWorld = gameController.gameWorld;

        GLOBAL_EXECUTOR2.execute { bootstrapServer(gameController.gameServerState, gameController.gameWorld) };
    }

    override fun render() {
        gameWorld.render();
    }
}
