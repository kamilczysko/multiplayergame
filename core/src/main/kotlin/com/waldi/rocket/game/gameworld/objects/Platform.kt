package com.waldi.rocket.game.gameworld.objects

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World

class Platform(x: Float, y: Float, width: Float, height: Float, world: World) {
    init {
        val floorBodyDef = BodyDef();
        floorBodyDef.type = BodyDef.BodyType.StaticBody;
        floorBodyDef.position.set(x, y);

        val floorBody = world.createBody(floorBodyDef);
        val floorShape = PolygonShape()
        floorShape.setAsBox(width, height)
        val floorFixture = FixtureDef();
        floorFixture.shape = floorShape

        floorBody.createFixture(floorFixture);

        floorShape.dispose();
    }

}
