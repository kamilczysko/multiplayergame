package com.waldi.rocket.gameengine.gameworld

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.utils.TimeUtils
import mu.two.KotlinLogging

class GameContactListener(val scorePoint: (rocketId: String) -> Unit):ContactListener {

    private val candidatesToScore: HashMap<String, Long> = HashMap();
    private val TIME_TO_SCORE_IN_SECONDS = 10f;
    private val logger = KotlinLogging.logger {}

    override fun beginContact(p0: Contact?) {
        var rocketId: String? = null;
        if(p0?.fixtureA?.userData == "MOON") {
            rocketId= p0?.fixtureB?.userData.toString();
        } else if (p0?.fixtureB?.userData == "MOON") {
            rocketId= p0?.fixtureA?.userData.toString();
        }

        if(rocketId != null ) {
            logger.info("HAS CONTACT")
            candidatesToScore[rocketId] = TimeUtils.millis();
        }

    }

    override fun endContact(p0: Contact?) {
        var rocketId: String? = null;
        if(p0?.fixtureA?.userData == "MOON") {
            rocketId= p0?.fixtureB?.userData.toString();
        } else if (p0?.fixtureB?.userData == "MOON") {
            rocketId= p0?.fixtureA?.userData.toString();
        }
        if(rocketId != null) {
            candidatesToScore.remove(rocketId);
        }
    }

    fun update() {
        val now = TimeUtils.millis();
        candidatesToScore.entries.forEach{
            if(((now - it.value) / 1000) > TIME_TO_SCORE_IN_SECONDS) {
                candidatesToScore.remove(it.key);
                scorePoint.invoke(it.key);
                logger.info("${it.key} - SCORED POINT");
        } }
    }

    override fun preSolve(p0: Contact?, p1: Manifold?) {
    }

    override fun postSolve(p0: Contact?, p1: ContactImpulse?) {
    }
}
