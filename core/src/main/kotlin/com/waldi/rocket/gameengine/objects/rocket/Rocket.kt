package com.waldi.rocket.gameengine.objects.rocket

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.waldi.rocket.gameengine.objects.WorldObject

const val ROCKET_WIDTH = 0.15f
const val ROCKET_HEIGHT = 0.6f
const val THRUST_FORCE = 6.5f

private const val FUEL_CONSUMPTION = 0.0009f

class Rocket(val id: String, val name: String, private var x: Float, private var y: Float) : WorldObject {
    var fuel: Float = 1.0f;
    var points: Short = 0;

    lateinit var body: Body;
    private lateinit var rocketFixtureDef: FixtureDef;

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
        val angle = body.angle
        val thrustForce = Vector2(0.0f, THRUST_FORCE).rotateRad(angle);
        val enginePosition = body.worldCenter.add(Vector2(0.0f, ROCKET_HEIGHT / 2).rotateRad(angle));
        body.applyForce(thrustForce, enginePosition, true);
        rocketFixtureDef.density *= fuel;
        fuel -= FUEL_CONSUMPTION;
    }

    fun rotate(angle: Float) {
        if (body.linearVelocity.x == 0.0f || body.linearVelocity.y == 0.0f) {
            return
        }
        body.setTransform(body.position, -angle);
    }

    override fun addToWorld(world: World) {
        val dynamicBodyDef = BodyDef();
        dynamicBodyDef.type = BodyDef.BodyType.DynamicBody;
        dynamicBodyDef.position.set(x, y);

        body = world.createBody(dynamicBodyDef);

        val boxShape = PolygonShape()
        boxShape.setAsBox(ROCKET_WIDTH, ROCKET_HEIGHT)

        rocketFixtureDef = FixtureDef();
        rocketFixtureDef.shape = boxShape;
        rocketFixtureDef.restitution = .3f;
        rocketFixtureDef.density = 1f;
        rocketFixtureDef.friction = .95f;

        body.angularDamping = .7f;

        body.isFixedRotation = false;

        val rocketBodyFixture = body.createFixture(rocketFixtureDef);

        rocketBodyFixture.userData = "ROCKET_$id"

        boxShape.dispose();
    }

    override fun deleteFromWorld(world: World) {
        world.destroyBody(body);
    }

    override fun getPosition(): Pair<Float, Float> {
        return Pair(body.position.x, body.position.y);
    }

    fun getDimension(): Pair<Float, Float> {
        return Pair(ROCKET_WIDTH, ROCKET_HEIGHT)
    }

    fun getAngleRad(): Float {
        return body.angle;
    }
}
