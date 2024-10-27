package com.waldi.rocket.game.gameworld

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.waldi.rocket.game.gameworld.objects.Rocket
import ktx.math.vec2
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class GameWorld {
    val gravity = Vector2(0.0f, -10.0f)
    val world: World = World(gravity, true);
    val camera: OrthographicCamera = OrthographicCamera(460f, 240f);
    val debug: Box2DDebugRenderer = Box2DDebugRenderer();
    val batch: Batch = SpriteBatch();
    val font: BitmapFont = BitmapFont();

    private val gameState: GameState;
    private val rockets: HashMap<Short, Rocket>;
    private val rocketsToMove: HashSet<Short>;

    init {
        camera.position.set(0.0f, 130.0f, 0.0f);
        camera.zoom += 0.1f;
        camera.update();
        debug.isDrawVelocities = true;

        gameState = InMemoryGameState();
        rockets = HashMap<Short, Rocket>();
        rocketsToMove = HashSet();

        generate(world);

        addPlayer("Kamil");
        addPlayer("Kamil1");
        addPlayer("Kamil2");
        addPlayer("Kamil3");
        addPlayer("Kamil4");
    }

    fun addPlayer(name: String): Short {
        val newPlayerId = gameState.addPlayer(name);

        val newRocket = Rocket(newPlayerId, name);
        newRocket.addRocketToWorld((Math.random() * PLATFORM_WIDTH_HALF).toFloat(), 1.0f, world);

        rockets[newPlayerId] = newRocket;

        return newPlayerId;
    }

    fun render() {
//        if (Gdx.input.isTouched) {
//
//            val rocketToFly = rockets.get(251)!!;
//
//            var mousePosition = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
//            camera.unproject(mousePosition)
//            val targetDirection = (vec2(mousePosition.x, mousePosition.y).sub(rocketToFly.rocketBody.position).nor());
//            var currentDir = vec2(cos(rocketToFly.rocketBody.angle), sin(rocketToFly.rocketBody.angle));
//            currentDir = (currentDir.add(targetDirection.sub(currentDir)))
//            val angle = atan2(currentDir.x, currentDir.y)
//
//            rocketToFly.flyRocket(angle);
//        }
//
//        if(Gdx.input.isKeyPressed(Keys.W)){
//            val rocketToFly = rockets.get(253)!!;
//
//            var mousePosition = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
//            camera.unproject(mousePosition)
//            val targetDirection = (vec2(mousePosition.x, mousePosition.y).sub(rocketToFly.rocketBody.position).nor());
//            var currentDir = vec2(cos(rocketToFly.rocketBody.angle), sin(rocketToFly.rocketBody.angle));
//            currentDir = (currentDir.add(targetDirection.sub(currentDir)))
//            val angle = atan2(currentDir.x, currentDir.y)
//
//            rocketToFly.flyRocket(angle);
//        }

        batch.begin();
        for ((index, rocket) in rockets.values.withIndex()) {
            font.draw(batch, "${rocket.name} ${(rocket.fuel * 100).toInt()}%", 10.0f, index * 20.0f + 15.0f);
        }
        batch.end();

        world.step(1 / 60f, 6, 2);
        debug.render(world, camera.combined);
    }
}
