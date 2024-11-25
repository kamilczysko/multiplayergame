package com.waldi.rocket.gameengine.objects

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World

class Moon(val x: Float,val y: Float, val radius: Float): WorldObject {
    private val moonBodyDef = BodyDef();
    private val moonFixtureDef = FixtureDef();
    private val moonShape = CircleShape();

    private lateinit var moonBody: Body;

    init {
        moonBodyDef.type = BodyDef.BodyType.StaticBody;
        moonBodyDef.position.set(x, y + 150);//little secret with front
        moonShape.radius = radius;
        moonFixtureDef.shape = moonShape
    }

    override fun addToWorld(world: World) {
        moonBody = world.createBody(moonBodyDef);
        val moonFixture = moonBody.createFixture(moonFixtureDef);
        moonFixture.userData = "MOON";
        moonShape.dispose();
    }

    override fun deleteFromWorld(world: World) {
        world.destroyBody(moonBody);
    }
}
