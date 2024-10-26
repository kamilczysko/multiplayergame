package com.waldi.rocket

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*

import ktx.math.vec2
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

const val ROCKET_WIDTH = 0.15f
const val ROCKET_HEIGHT = 0.6f

const val THRUST_FORCE = 6.5f

private const val FUEL_CONSUMPTION = 0.0009f

class Rocket(x: Float, y: Float, world: World) {
    val id: Long = 0;
    private val rocketBody: Body;
    var fuel: Float = 1.0f;
    private val rocketFixtureDef: FixtureDef;

    init {
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

    fun flyRocket(directionX: Float, directionY: Float) {
        if(fuel <= 0.0f) {
            return;
        }
        accelerate();
        rotate(directionX, directionY);
    }

    private fun accelerate() {
        val angle = rocketBody.angle
        println(Math.toDegrees(angle.toDouble()))
        val thrustForce = Vector2(0.0f, THRUST_FORCE).rotateRad(angle);
        val enginePosition = rocketBody.worldCenter.add(Vector2(0.0f, ROCKET_HEIGHT / 2).rotateRad(angle));
        rocketBody.applyForce(thrustForce, enginePosition, true);
        rocketFixtureDef.density *= fuel;
        fuel -= FUEL_CONSUMPTION;
    }

    private fun rotate(directionX: Float, directionY: Float) {
        val targetDirection = (vec2(directionX, directionY).sub(rocketBody.position).nor());
        var currentDir = vec2(cos(rocketBody.angle), sin(rocketBody.angle));
        currentDir = (currentDir.add(targetDirection.sub(currentDir)))
        val rocketAngle = atan2(currentDir.x, currentDir.y);
        rocketBody.setTransform(rocketBody.position, -rocketAngle);
    }
}
