package com.waldi.rocket.gameengine.gameworld

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.waldi.rocket.gameengine.enginestate.EngineState
import com.waldi.rocket.gameengine.objects.rocket.RocketListener

class GameWorld(val engineState: EngineState, val rocketListener: RocketListener?) {
    val gravity = Vector2(0.0f, -10.0f)
    val world: World = World(gravity, true);
    val camera: OrthographicCamera = OrthographicCamera(100f, 240f);
    val debug: Box2DDebugRenderer = Box2DDebugRenderer();
    val batch: Batch = SpriteBatch();
    val font: BitmapFont = BitmapFont();

    init {
        camera.position.set(0.0f, 130.0f, 0.0f);
        camera.zoom += 0.1f;
        camera.update();
        debug.isDrawVelocities = true;
        world.setContactListener(GameContactListener(rocketListener));

        engineState.initNewMap();
        engineState.addPlatformsToTheWorld(world);
        engineState.addMoonToTheWorld(world);

        engineState.onAddObjectToTheWorld { worldObject -> worldObject.addToWorld(world) }
        engineState.onDestroyObject { worldObject -> worldObject.addToWorld(world) }
        engineState.onNewRocket{  rocket -> rocket.addToWorld(world) }
        engineState.onRemoveRocket{ rocket -> rocket.deleteFromWorld(world)}
    }

    fun render() {

        batch.begin();
//        for ((index, rocket) in gameState.getAllPlayers().withIndex()) {
//            font.draw(batch, "${rocket.name} ${(rocket.fuel * 100).toInt()}%", 10.0f, index * 20.0f + 15.0f);
//        }
        batch.end();

        world.step(1 / 60f, 6, 2);
        debug.render(world, camera.combined);
    }

//    private fun flyMeToTheMoon(id: Short) {
//        val rocketToFly = gameState.getPlayer(id);
//
//        var mousePosition = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
//        camera.unproject(mousePosition)
//
//        val rocketBody = rocketToFly.rocketBody
//        val targetDirection = (vec2(mousePosition.x, mousePosition.y).sub(rocketBody.position).nor());
//        var currentDir = vec2(cos(rocketBody.angle), sin(rocketBody.angle));
//        currentDir = (currentDir.add(targetDirection.sub(currentDir)))
//        val angle = atan2(currentDir.x, currentDir.y)
//
//        rocketToFly.rotate(angle);
//    }
}
