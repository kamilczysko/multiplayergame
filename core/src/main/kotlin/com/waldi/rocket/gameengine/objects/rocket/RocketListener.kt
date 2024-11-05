package com.waldi.rocket.gameengine.objects.rocket

fun interface RocketListener {
    fun rocketScoredPoint(rocketId: String, points: Int);
}
