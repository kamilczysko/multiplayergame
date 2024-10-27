package com.waldi.rocket.game.gameworld

import com.waldi.rocket.game.gameworld.objects.Rocket
import java.util.Stack

class InMemoryGameState : GameState {
    private val gameState: HashMap<Short, Rocket> = HashMap();
    private val ids = Stack<Short>();

    init {
        ids.addAll((0..255).map { it.toShort() });
    }

    override fun hasId(id: Short): Boolean = gameState.contains(id);

    override fun addPlayer(name: String): Short {
        if (isGameFull()) {
            throw IllegalStateException("Game is full")
        }
        val newId = ids.pop()
        gameState[newId] = Rocket(newId, name);
        return newId;
    }

    override fun removePlayer(id: Short) {
        gameState.remove(id);
        ids.push(id);
    }

    override fun score(id: Short) {
        gameState[id]?.addPoint();
    }

    override fun isGameFull(): Boolean {
        return ids.isEmpty();
    }

    override fun getPlayer(name: String): Rocket = gameState.values.stream().filter { player -> player.name == name }.findFirst().orElseThrow{ IllegalStateException("Player not found")};

    override fun getPlayer(id: Short) : Rocket = gameState[id]!!;
    override fun getAllPlayers(): Set<Rocket> = gameState.values.toSet();
}
