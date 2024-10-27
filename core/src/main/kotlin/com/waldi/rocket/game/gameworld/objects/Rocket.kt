package com.waldi.rocket.game.gameworld.objects

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*

const val ROCKET_WIDTH = 0.15f
const val ROCKET_HEIGHT = 0.6f
const val THRUST_FORCE = 6.5f

private const val FUEL_CONSUMPTION = 0.0009f

class Rocket(id: Short, name: String) {
    val id: Short;
    var fuel: Float = 1.0f;
    var points: Short = 0;
    val name: String;

    lateinit var rocketBody: Body;
    private lateinit var rocketFixtureDef: FixtureDef;

    init {
        this.id = id;
        this.name = name;

    }

    fun addRocketToWorld(x: Float, y: Float, world: World) {
        val dynamicBodyDef = BodyDef();
        dynamicBodyDef.type = BodyDef.BodyType.DynamicBody;
        dynamicBodyDef.position.set(x, y);

        rocketBody = world.createBody(dynamicBodyDef);

        val boxShape = PolygonShape()
        boxShape.setAsBox(ROCKET_WIDTH, ROCKET_HEIGHT)

        rocketFixtureDef = FixtureDef();
        rocketFixtureDef.shape = boxShape;
        rocketFixtureDef.restitution = .3f;
        rocketFixtureDef.density = 1f;
        rocketFixtureDef.friction = .95f;

        rocketBody.angularDamping = .7f;

        rocketBody.isFixedRotation = false;

        val rocketBodyFixture = rocketBody.createFixture(rocketFixtureDef);

        rocketBodyFixture.userData = "ROCKET_$id"

        boxShape.dispose();
    }

    fun addPoint() {
        this.points++;
    }

    fun flyRocket(angle: Float) {
        if (fuel <= 0.0f) {
            return;
        }
        accelerate();
        rotate(angle);
    }

    fun accelerate() {
        val angle = rocketBody.angle
        val thrustForce = Vector2(0.0f, THRUST_FORCE).rotateRad(angle);
        val enginePosition = rocketBody.worldCenter.add(Vector2(0.0f, ROCKET_HEIGHT / 2).rotateRad(angle));
        rocketBody.applyForce(thrustForce, enginePosition, true);
        rocketFixtureDef.density *= fuel;
        fuel -= FUEL_CONSUMPTION;
    }

    fun rotate(angle: Float) {
        if (rocketBody.linearVelocity.x == 0.0f || rocketBody.linearVelocity.y == 0.0f) {
            return
        }
        rocketBody.setTransform(rocketBody.position, -angle);
    }
}
