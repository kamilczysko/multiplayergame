package com.waldi.rocket.gameengine.gameworld

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.waldi.rocket.gameengine.objects.Moon
import com.waldi.rocket.gameengine.objects.Platform
import com.waldi.rocket.gameengine.objects.Rocket
import com.waldi.rocket.shared.*
import ktx.math.random
import mu.two.KotlinLogging

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class GameWorld {

    private val gravity = Vector2(0.0f, -10.0f)
    private val world: World = World(gravity, false);
//    private val camera: OrthographicCamera = OrthographicCamera(100f, 240f);
//    private val debug: Box2DDebugRenderer = Box2DDebugRenderer();
//    private val batch: Batch = SpriteBatch();
//    private val font: BitmapFont = BitmapFont();


    private val rocketIdToEntity = ConcurrentHashMap<String, Rocket>();
    private var platforms: List<Platform> = emptyList();
    private var moon: Moon? = null;
    private lateinit var mapHash: String;

    private lateinit var gameController: GameController;
    private var gameContactListener: GameContactListener;

    private var gameTimeStamp: Int = 0;

    private val logger = KotlinLogging.logger {}

    private val executor = Executors.newFixedThreadPool(10);

    init {
//        camera.position.set(0.0f, 130.0f, 0.0f);
//        camera.zoom += 0.1f;
//        camera.update();
//        debug.isDrawVelocities = true;
        gameContactListener = GameContactListener({ rocketId -> scorePoint(rocketId) });
        world.setContactListener(gameContactListener);
        logger.info { "Starting game engine..." };
    }

    fun setController(gameController: GameController) {
        this.gameController = gameController;
    }

    fun render() {
//        batch.begin();
//        font.draw(batch, "TEST", 510.0f, 1 * 20.0f + 15.0f);
        for ((index, rocket) in rocketIdToEntity.values.withIndex()) {
//            font.draw(batch, "${rocket.name} ${(rocket.fuel * 100).toInt()}%", 10.0f, index * 20.0f + 15.0f);
            if (rocket.isAccelerating) {
                rocket.accelerate();
            }
            val rocketsData = rocketIdToEntity.values.stream()
                .map { RocketData(it.x, it.y, it.angle, it.rocketId, it.fuel, it.points) }
                .collect(Collectors.toList())
            if (rocket.isInMotion()) {
                executor.execute() {
                    gameTimeStamp++;
                    gameController.notifyAboutGameState(rocketsData, gameTimeStamp);
                }
            }
            if (rocket.y < -300) {
                resetRocket(rocket)
            }
        }
        gameContactListener.update();
//        batch.end();

        world.step(1 / 30f, 5, 10);
//        debug.render(world, camera.combined);
    }

    private fun scorePoint(rocketId: String) {
        logger.info("ADD POINTTT")
        rocketIdToEntity[rocketId]?.addPoint()
        resetRocket(rocketId);
    }

    fun initRocket(rocketId: String) {
        val newRocket =
            Rocket(rocketId, (-BASE_PLATFORM_WIDTH..BASE_PLATFORM_WIDTH).random().toFloat(), 5f);

        assert(!rocketIdToEntity.contains(rocketId)) { "rocket $rocketId already exists in game" }

        rocketIdToEntity[rocketId] = newRocket;
        newRocket.addToWorld(world);
    }

    fun removeRocket(rocketId: String) {
        rocketIdToEntity[rocketId]?.deleteFromWorld(world);
        rocketIdToEntity.remove(rocketId);
    }

    fun resetRocket(rocketId: String) {
        resetRocket(rocketIdToEntity[rocketId]!!);
    }

    fun resetRocket(entity: Rocket) {
        entity.fuel = 1.0f;
        entity.setPosition((-BASE_PLATFORM_WIDTH..BASE_PLATFORM_WIDTH).random().toFloat(), 7f);
    }

    fun getMap(): MapData {
        if (this.platforms.isEmpty() || moon == null) {
            logger.info("Create new map")
            gameTimeStamp = 0;
            return initNewMap();
        }

        logger.info("Get existing map data")
        val platformsData = platforms.stream()
            .map { PlatformData(it.x - it.width, it.y - it.height, it.width * 2, it.height * 2) }
            .toList();

        val moonData = MoonData(moon!!.radius, moon!!.x, moon!!.y - 200); //little secret with front

        return MapData(platformsData, moonData, mapHash);
    }

    private fun initNewMap(): MapData {
        moon?.deleteFromWorld(world);

        platforms.stream().forEach { it.deleteFromWorld(world) };

        val newMap = generate();
        platforms = ArrayList(newMap.first);
        moon = newMap.second;

        moon?.addToWorld(world)
        platforms.stream().forEach { it.addToWorld(world) }

        mapHash = UUID.randomUUID().mostSignificantBits.toString().replace("-", "").take(5);

        return getMap();
    }

    fun startAccelerating(rocketId: String) {
        rocketIdToEntity[rocketId]?.isAccelerating = true;
    }

    fun stopAccelerating(rocketId: String) {
        rocketIdToEntity[rocketId]?.isAccelerating = false;
    }

    fun rotate(rocketId: String, angle: Float) {
        rocketIdToEntity[rocketId]?.rotate(angle);
    }
}
