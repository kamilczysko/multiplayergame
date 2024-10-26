package com.waldi.rocket
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold

class GameContactListener: ContactListener {
    override fun beginContact(p0: Contact?) {
    }

    override fun endContact(p0: Contact?) {
    }

    override fun preSolve(p0: Contact?, p1: Manifold?) {
    }

    override fun postSolve(p0: Contact?, p1: ContactImpulse?) {
    }
}
