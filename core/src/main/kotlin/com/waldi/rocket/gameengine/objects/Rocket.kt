package com.waldi.rocket.gameengine.objects

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ktx.log.logger
import kotlin.math.log

const val ROCKET_WIDTH = 0.5f;
const val ROCKET_HEIGHT = 1.5f;
const val THRUST_FORCE = 17f;

private const val FUEL_CONSUMPTION = 0.0009f

class Rocket(val rocketId: String, val name: String, private var initXPos: Float, private var initYPos: Float) : WorldObject {
    var fuel: Float = 1.0f;
    var points: Short = 0;
    var isAccelerating = false;

    private lateinit var rocketBody: Body;
    private lateinit var rocketFixtureDef: FixtureDef;

    fun addPoint() {
        this.points++;
    }

    fun accelerate() {
        if (fuel <= 0.0f) {
            return;
        }
        val angle = rocketBody.angle
        val thrustForce = Vector2(0.0f, THRUST_FORCE).rotateRad(angle);
        val rocketAbsolutePosition = rocketBody.worldCenter.add(Vector2(0.0f, ROCKET_HEIGHT / 2).rotateRad(angle));
        rocketBody.applyForce(thrustForce, rocketAbsolutePosition, true);
        rocketFixtureDef.density *= fuel;
        fuel -= FUEL_CONSUMPTION;
    }

    fun rotate(angle: Float) {
        if (rocketBody.linearVelocity.x == 0.0f || rocketBody.linearVelocity.y == 0.0f) {
            return
        }
        rocketBody.setTransform(rocketBody.position, -angle);
    }

    fun isInMotion(): Boolean {
        return rocketBody.linearVelocity.len() >= 0.001f || rocketBody.angularVelocity >= 0.01f;
    }

    override fun addToWorld(world: World) {
        val dynamicBodyDef = BodyDef();
        dynamicBodyDef.type = BodyDef.BodyType.DynamicBody;
        dynamicBodyDef.position.set(initXPos, initYPos);

        rocketBody = world.createBody(dynamicBodyDef);

        val boxShape = PolygonShape()
        boxShape.setAsBox(ROCKET_WIDTH, ROCKET_HEIGHT)

        rocketFixtureDef = FixtureDef();
        rocketFixtureDef.shape = boxShape;
        rocketFixtureDef.restitution = .2f;
        rocketFixtureDef.density = .4f;
        rocketFixtureDef.friction = .95f;

        rocketBody.angularDamping = .7f;

        rocketBody.isFixedRotation = false;

        val rocketBodyFixture = rocketBody.createFixture(rocketFixtureDef);

        rocketBodyFixture.userData = "ROCKET_$rocketId"

        boxShape.dispose();
    }

    override fun deleteFromWorld(world: World) {
        world.destroyBody(rocketBody);
    }

    public var x: Float = initXPos
        get() {
            return rocketBody.position.x
        }

    public var y: Float = initYPos
        get() {
            return rocketBody.position.y;
        }

    public var angle: Float = 0.0f
     get() {
         return rocketBody.angle;
    }

}
