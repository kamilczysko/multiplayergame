package com.waldi.rocket.gameengine.enginestate

import com.badlogic.gdx.physics.box2d.World
import com.waldi.rocket.gameengine.gameworld.BASE_PLATFORM_WIDTH
import com.waldi.rocket.gameengine.gameworld.GameWorldCommand
import com.waldi.rocket.gameengine.gameworld.generate
import com.waldi.rocket.gameengine.objects.Moon
import com.waldi.rocket.gameengine.objects.Platform
import com.waldi.rocket.gameengine.objects.Rocket
import com.waldi.rocket.shared.GameEventBus
import ktx.math.random
import java.util.concurrent.ConcurrentSkipListMap

class EngineState() {
    private val rockets: ConcurrentSkipListMap<String, Rocket> = ConcurrentSkipListMap<String, Rocket>();
    private lateinit var basePlatforms: List<Platform>;
    private lateinit var platforms: List<Platform>;
    private lateinit var moon: Moon;

    private lateinit var onNewRocket: GameWorldCommand;
    private lateinit var onRemoveRocket: GameWorldCommand;


    fun initState() {
        val (basePlatforms, platforms, moon) = generate();
        this.basePlatforms = basePlatforms;
        this.platforms = platforms;
        this.moon = moon;
    }

    fun getBasePlatforms(): List<Platform> {
        return ArrayList(this.basePlatforms);
    }

    fun getPlatforms(): List<Platform> {
        return ArrayList(this.platforms);
    }

    fun getMoon(): Moon {
        return this.moon;
    }

    fun addRocket(id: String, name: String) {
        val newRocket = Rocket(id, name, (-BASE_PLATFORM_WIDTH..BASE_PLATFORM_WIDTH).random().toFloat(), .6f)
        rockets.putIfAbsent(id, newRocket);
        onNewRocket.execute(newRocket);
    }

    fun removeRocket(id: String) {
        val rocket: Rocket = rockets[id] ?: return;
        onRemoveRocket.execute(rocket)
        rockets.remove(id);
    }

    fun resetRocket(id: String) {
        val rocket: Rocket = rockets[id] ?: return;
        onRemoveRocket.execute(rocket)
        val newRocket =
            Rocket(rocket.id, rocket.name, (-BASE_PLATFORM_WIDTH..BASE_PLATFORM_WIDTH).random().toFloat(), .6f)
        rockets[id] = newRocket;
        onNewRocket.execute(newRocket);
    }

    fun addPlatformsToTheWorld(world: World) {
        basePlatforms.stream().forEach { p -> p.addToWorld(world) }
        platforms.stream().forEach { p -> p.addToWorld(world) }
    }

    fun addMoonToTheWorld(world: World) {
        this.moon.addToWorld(world);
    }

    fun clearGame(world: World) {
        basePlatforms.stream().forEach { p -> p.deleteFromWorld(world) };
        platforms.stream().forEach { p -> p.deleteFromWorld(world) };
        moon.deleteFromWorld(world);
        initState();
        rockets.keys.stream()
            .forEach { id -> resetRocket(id) }
    }

    fun onNewRocket(command: GameWorldCommand) {
        onNewRocket = command;
    }

    fun onRemoveRocket(command: GameWorldCommand) {
        onRemoveRocket = command;
    }

}
