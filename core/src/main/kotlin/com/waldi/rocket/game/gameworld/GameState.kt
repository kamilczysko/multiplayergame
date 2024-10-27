package com.waldi.rocket.game.gameworld

import com.waldi.rocket.game.gameworld.objects.Platform
import com.waldi.rocket.game.gameworld.objects.Rocket


interface GameState {

    fun hasId(id: Short): Boolean;
    fun addPlayer(name: String): Short;
    fun removePlayer(id: Short);
    fun score(id:Short);
    fun isGameFull(): Boolean;
    fun getPlayer(name:String) : Rocket?;
    fun getPlayer(id: Short) : Rocket;
    fun getAllPlayers(): Set<Rocket>;

}
