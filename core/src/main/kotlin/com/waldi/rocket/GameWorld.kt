package com.waldi.rocket

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World

class GameWorld {
    val gravity = Vector2(0.0f, -10.0f)
    val world: World = World(gravity, true);
    val camera: OrthographicCamera = OrthographicCamera(460f, 240f);
    val debug: Box2DDebugRenderer = Box2DDebugRenderer();
    val batch: Batch = SpriteBatch();
    val font: BitmapFont = BitmapFont();

    private val rocket: Rocket;

    init {
        world.setContactListener(GameContactListener());

        camera.position.set(0.0f, 130.0f, 0.0f);
        camera.zoom += 0.1f;
        camera.update();
        debug.isDrawVelocities = true;

        generate(world);

        rocket = Rocket((Math.random() * PLATFORM_WIDTH_HALF).toFloat(), 1.0f, world);

    }

    fun render() {
        if (Gdx.input.isTouched) {
            val mousePosition = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0.0f);
            camera.unproject(mousePosition)

            rocket.flyRocket(mousePosition.x, mousePosition.y);
        }

        batch.begin();
        font.draw(batch, "${(rocket.fuel * 100).toInt()}%", 10.0f, 100.0f);
        batch.end();


        world.step(1 / 60f, 6, 2);
        debug.render(world, camera.combined);
    }
}
