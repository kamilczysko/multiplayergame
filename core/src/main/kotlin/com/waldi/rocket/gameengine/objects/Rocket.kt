package com.waldi.rocket.gameengine.objects

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ktx.log.logger
import kotlin.math.log

const val ROCKET_WIDTH = 0.5f;
const val ROCKET_HEIGHT = 1.5f;
const val THRUST_FORCE = 16f;

private const val FUEL_CONSUMPTION = 0.00095f

class Rocket(val rocketId: String, private var initXPos: Float, private var initYPos: Float) : WorldObject {
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
        rocketBody.applyForce(thrustForce, rocketAbsolutePosition, false);
        rocketFixtureDef.density *= fuel;
        fuel -= FUEL_CONSUMPTION;
    }

    fun rotate(angle: Float) {
        if (rocketBody.linearVelocity.x == 0.0f || rocketBody.linearVelocity.y == 0.0f) {
            return
        }
        rocketBody.setTransform(rocketBody.position, -angle);
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
        rocketFixtureDef.restitution = .3f;
        rocketFixtureDef.density = .35f;
        rocketFixtureDef.friction = .95f;

        rocketBody.angularDamping = .85f;

        rocketBody.isFixedRotation = false;

        val rocketBodyFixture = rocketBody.createFixture(rocketFixtureDef);
        rocketBodyFixture.userData = rocketId;

        boxShape.dispose();
    }

    override fun deleteFromWorld(world: World) {
        world.destroyBody(rocketBody);
    }

    fun setPosition(x: Float, y: Float) {
        rocketBody.setLinearVelocity(0f, 0f);
        rocketBody.setTransform(Vector2(x, y),0f);
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
