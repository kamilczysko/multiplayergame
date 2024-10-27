package com.waldi.rocket.game.gameworld.objects

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World

class Box(x: Float, y: Float, width: Float, height: Float, world: World) {
    init {
        val boxBodyDef = BodyDef();
        boxBodyDef.type = BodyDef.BodyType.DynamicBody;
        boxBodyDef.position.set(x, y);

        val boxBody = world.createBody(boxBodyDef);
        val boxShape = PolygonShape()
        boxShape.setAsBox(width, height)

        val boxFixture = FixtureDef();
        boxFixture.shape = boxShape;
        boxFixture.restitution = .3f;
        boxFixture.density = .3f;
        boxFixture.friction = 1f;

        boxBody.createFixture(boxFixture);

        boxShape.dispose();
    }
}
