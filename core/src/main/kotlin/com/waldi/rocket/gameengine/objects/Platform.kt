package com.waldi.rocket.gameengine.objects

import com.badlogic.gdx.physics.box2d.*

class Platform(val x: Float, val y: Float, val width: Float, val height: Float): WorldObject {
    private val floorBodyDef = BodyDef();
    private val floorFixture = FixtureDef();
    private lateinit var floorBody: Body;

    init {
        floorBodyDef.type = BodyDef.BodyType.StaticBody;
        floorBodyDef.position.set(x, y);
    }

    override fun addToWorld(world: World) {
        floorBody = world.createBody(floorBodyDef);

        val floorShape = PolygonShape()
        floorShape.setAsBox(width, height)
        floorFixture.shape = floorShape
        floorBody.createFixture(floorFixture);
        floorShape.dispose();
    }

    override fun deleteFromWorld(world: World) {
        world.destroyBody(floorBody);
    }

    override fun getPosition(): Pair<Float, Float> {
        return Pair(floorBody.position.x, floorBody.position.y);
    }

    fun getDimension(): Pair<Float, Float> {
        return Pair(width, height);
    }
}
