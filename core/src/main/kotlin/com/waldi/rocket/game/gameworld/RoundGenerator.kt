package com.waldi.rocket.game.gameworld
import com.badlogic.gdx.physics.box2d.World
import com.waldi.rocket.game.gameworld.objects.Box
import com.waldi.rocket.game.gameworld.objects.Moon
import com.waldi.rocket.game.gameworld.objects.Platform


const val PLATFORM_WIDTH_HALF = 20.0f
private const val BOX_SIZE = .7f

fun generate(world: World) {
    Platform(0.0f, 0.0f, PLATFORM_WIDTH_HALF, 0.1f, world);
    Platform(-20.0f, 70.0f, 0.1f, 70.0f, world);
    Platform(20.0f, 70.0f, 0.1f, 70.0f, world);

    val numberOfLayers = (8..14).random()
    val yOffset = 140.0f/numberOfLayers;
    for (i in (2..numberOfLayers)) {
        createLayerSingle(yOffset * i, world);
    }

    Moon(0.0f, 210f, 10.0f, world);
//    wallOfBoxes(0.0f, 180f, world);

}

private fun createLayerSingle(y: Float = 8.0f, world: World) {
    val width = (4..10).random().toFloat();
    val x = ((0..(PLATFORM_WIDTH_HALF * 2).toInt()).random()).toFloat() - PLATFORM_WIDTH_HALF;
    Platform(x, y + (0..2).random(), width, 0.1f, world)
//    val seed = (1..15).random()
//    val wallXPos = x + (1..width.toInt() - 2).random()
//    if(seed % 2 == 0) {
//        wallOfBoxes(wallXPos, y, world)
//    } else if(seed % 3 == 0) {
//        tripleWall(wallXPos, y, world);
//    }
}

private fun wallOfBoxes(x: Float, y: Float, world: World) {
    for(i in (3..(5..9).random())) {
        Box(x, y + i * BOX_SIZE * 2, BOX_SIZE, BOX_SIZE, world);
    }
}

private fun tripleWall(x: Float, y: Float, world: World) {
    wallOfBoxes(x, y, world);
    wallOfBoxes(x + BOX_SIZE * 2, y, world);
    wallOfBoxes(x - BOX_SIZE * 2, y, world);
}
