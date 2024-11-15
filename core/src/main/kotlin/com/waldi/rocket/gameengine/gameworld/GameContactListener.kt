package com.waldi.rocket.gameengine.gameworld

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold

class GameContactListener(val scorePoint: (rocketId: String) -> Unit):ContactListener {

    override fun beginContact(p0: Contact?) {
        //todo implement scoring points
    }

    override fun endContact(p0: Contact?) {
    }

    override fun preSolve(p0: Contact?, p1: Manifold?) {
    }

    override fun postSolve(p0: Contact?, p1: ContactImpulse?) {
    }
}
