package com.waldi.rocket.gameengine.gameworld

import com.waldi.rocket.gameengine.objects.WorldObject

fun interface GameWorldCommand {
    fun execute(worldObject: WorldObject);
}
