package com.waldi.rocket.gameengine.gameworld

import com.waldi.rocket.gameengine.objects.Moon
import com.waldi.rocket.gameengine.objects.Platform


private const val BOX_SIZE = .7f

public const val BASE_PLATFORM_WIDTH: Float = 20.0f

fun generate(): Triple<List<Platform>, List<Platform>, Moon> {
    val basePlatforms: List<Platform> = listOf(
        Platform(0.0f, 0.0f, BASE_PLATFORM_WIDTH, 1.0f),
        Platform(BASE_PLATFORM_WIDTH, 70.0f, 1.0f, 70.0f),
        Platform(-BASE_PLATFORM_WIDTH, 70.0f, 1.0f, 70.0f)
    )
    val platforms: List<Platform> = generatePlatforms()
    val moon = Moon(0.0f, (210..260).random().toFloat(), (4..13).random().toFloat());
    return Triple(basePlatforms, platforms, moon);
}

private fun generatePlatforms(): List<Platform> {
    val platforms = mutableListOf<Platform>()
    val numberOfLayers = (8..14).random()
    val yOffset = 140.0f / numberOfLayers;
    for (i in (2..numberOfLayers)) {
        platforms.add(createLayerSingle(yOffset * i));
    }

    return platforms
}

private fun createLayerSingle(y: Float = 8.0f): Platform {
    val width = (4..10).random().toFloat();
    val x = ((0..(BASE_PLATFORM_WIDTH * 2).toInt()).random()).toFloat() - BASE_PLATFORM_WIDTH;
    return Platform(x, y + (0..2).random(), width, .5f)
}

//private fun wallOfBoxes(x: Float, y: Float) {
//    for (i in (3..(5..9).random())) {
//        Box(x, y + i * BOX_SIZE * 2, BOX_SIZE, BOX_SIZE);
//    }
//}

//private fun tripleWall(x: Float, y: Float) {
//    wallOfBoxes(x, y);
//    wallOfBoxes(x + BOX_SIZE * 2, y);
//    wallOfBoxes(x - BOX_SIZE * 2, y);
//}
