package com.waldi.rocket.game.gameworld.objects

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World

class Moon(x: Float, y: Float, radius: Float, world: World) {
    init {
        val moonBodyDef = BodyDef();
        moonBodyDef.type = BodyDef.BodyType.StaticBody;
        moonBodyDef.position.set(x, y);

        val moonBody = world.createBody(moonBodyDef);
        val moonShape = CircleShape()
        moonShape.radius = radius;
        val moonFixtureDef = FixtureDef();
        moonFixtureDef.shape = moonShape

        val moonFixture = moonBody.createFixture(moonFixtureDef);
        moonFixture.userData = "MOON";

        moonShape.dispose();
    }
}
