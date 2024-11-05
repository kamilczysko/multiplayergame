package com.waldi.rocket.shared.gamebus

interface GameEventBus {
    fun initNewMap();
    fun resetMap();
    fun getMapData() : MapData;
    fun addRocket(id: String, name: String);
    fun deleteRocket(playerId: String);
    fun getRocketsData(): List<RocketData>;
    fun resetRocket(playerId: String);
}
