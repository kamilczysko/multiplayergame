package com.waldi.rocket.gameengine.objects

import com.badlogic.gdx.physics.box2d.World

interface WorldObject {
    fun addToWorld(world: World);
    fun deleteFromWorld(world: World);
    fun getPosition(): Pair<Float, Float>
}
