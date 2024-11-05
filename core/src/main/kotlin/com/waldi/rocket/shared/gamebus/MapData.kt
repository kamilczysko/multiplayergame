package com.waldi.rocket.shared.gamebus

class MapData(val platforms: List<PlatformData>, val moon: MoonData, mapHash: String): GameEvent {
}
