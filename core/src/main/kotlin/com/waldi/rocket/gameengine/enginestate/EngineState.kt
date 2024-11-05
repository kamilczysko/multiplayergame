package com.waldi.rocket.gameengine.enginestate

import com.badlogic.gdx.physics.box2d.World
import com.waldi.rocket.gameengine.gameworld.BASE_PLATFORM_WIDTH
import com.waldi.rocket.gameengine.gameworld.GameWorldCommand
import com.waldi.rocket.gameengine.gameworld.generate
import com.waldi.rocket.gameengine.objects.Moon
import com.waldi.rocket.gameengine.objects.Platform
import com.waldi.rocket.gameengine.objects.rocket.Rocket
import com.waldi.rocket.shared.gamebus.GameEventBus
import com.waldi.rocket.shared.gamebus.RocketData
import com.waldi.rocket.shared.gamebus.MapData
import com.waldi.rocket.shared.gamebus.MoonData
import com.waldi.rocket.shared.gamebus.PlatformData
import ktx.math.random
import java.util.UUID
import java.util.concurrent.ConcurrentSkipListMap
import java.util.stream.Collectors

class EngineState() : GameEventBus {
    private val rockets: ConcurrentSkipListMap<String, Rocket> = ConcurrentSkipListMap<String, Rocket>();
    private lateinit var basePlatforms: List<Platform>;
    private lateinit var platforms: List<Platform>;
    private lateinit var moon: Moon;
    private lateinit var mapHash: String;

    private lateinit var onNewRocketCommand: GameWorldCommand;
    private lateinit var onRemoveRocketCommand: GameWorldCommand;
    private lateinit var onRemoveObjectFromTheWorld: GameWorldCommand;
    private lateinit var onAddObjectToTheWorldCommand: GameWorldCommand;

    override fun initNewMap() {
        val (basePlatforms, platforms, moon) = generate();
        this.basePlatforms = basePlatforms;
        this.platforms = platforms;
        this.moon = moon;
        this.mapHash = UUID.randomUUID().mostSignificantBits.toString().replace("-", "").take(5);
    }

    override fun resetMap() {
        basePlatforms.stream().forEach { p -> onRemoveObjectFromTheWorld.execute(p)  };
        platforms.stream().forEach { p -> onRemoveObjectFromTheWorld.execute(p) };
        onRemoveObjectFromTheWorld.execute(moon);
        initNewMap();
        rockets.keys.stream()
            .forEach { id -> resetRocket(id) }
    }

    override fun getMapData(): MapData {
        val platforms = mutableListOf<PlatformData>();
        for (platform in this.basePlatforms) {
            platforms.add(PlatformData(platform.x, platform.y, platform.width, platform.height))
        }
        for (platform in this.platforms) {
            platforms.add(PlatformData(platform.x, platform.y, platform.width, platform.height))
        }
        val moon = MoonData(moon.radius, moon.x, moon.y);

        return MapData(platforms, moon, mapHash);
    }

    override fun addRocket(id: String, name: String) {
        val newRocket = Rocket(id, name, (-BASE_PLATFORM_WIDTH..BASE_PLATFORM_WIDTH).random().toFloat(), .6f)
        rockets.putIfAbsent(id, newRocket);
        onNewRocketCommand.execute(newRocket); }

    override fun deleteRocket(playerId: String) {
        val rocket: Rocket = rockets[playerId] ?: return;
        onRemoveRocketCommand.execute(rocket)
        rockets.remove(playerId); }

    override fun getRocketsData(): List<RocketData> {
        return rockets.values.stream()
            .map { rocket ->
                RocketData(
                    rocket.body.position.x,
                    rocket.body.position.y,
                    rocket.body.position.angleRad(),
                    rocket.id,
                    rocket.fuel
                )
            }
            .collect(Collectors.toList());
    }

    override fun resetRocket(playerId: String) {
        val rocket: Rocket = rockets[playerId] ?: return;
        onRemoveRocketCommand.execute(rocket)
        val newRocket =
            Rocket(rocket.id, rocket.name, (-BASE_PLATFORM_WIDTH..BASE_PLATFORM_WIDTH).random().toFloat(), .6f)
        rockets[playerId] = newRocket;
        onNewRocketCommand.execute(newRocket);
    }

    fun addPlatformsToTheWorld(world: World) {
        basePlatforms.stream().forEach { p -> p.addToWorld(world) }
        platforms.stream().forEach { p -> p.addToWorld(world) }
    }

    fun addMoonToTheWorld(world: World) {
        this.moon.addToWorld(world);
    }

    fun onNewRocket(command: GameWorldCommand) {
        onNewRocketCommand = command;
    }

    fun onRemoveRocket(command: GameWorldCommand) {
        onRemoveRocketCommand = command;
    }

    fun onAddObjectToTheWorld (command: GameWorldCommand) {
        this.onAddObjectToTheWorldCommand = command;
    }
    fun onDestroyObject (command: GameWorldCommand) {
        this.onRemoveObjectFromTheWorld = command;
    }

}
