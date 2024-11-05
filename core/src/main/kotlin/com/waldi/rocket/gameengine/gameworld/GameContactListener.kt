package com.waldi.rocket.gameengine.gameworld

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.waldi.rocket.gameengine.objects.rocket.RocketListener

class GameContactListener(private val rocketListener: RocketListener?):ContactListener {

    override fun beginContact(p0: Contact?) {
        rocketListener ?: return;
//        rocketListener.rocketScoredPoint()
        //todo implement scoring points
    }

    override fun endContact(p0: Contact?) {
    }

    override fun preSolve(p0: Contact?, p1: Manifold?) {
    }

    override fun postSolve(p0: Contact?, p1: ContactImpulse?) {
    }
}
