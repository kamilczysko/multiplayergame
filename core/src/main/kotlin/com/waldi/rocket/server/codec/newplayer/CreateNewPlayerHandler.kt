package com.waldi.rocket.server.codec.newplayer

import com.waldi.rocket.server.gamestate.GameState
import com.waldi.rocket.server.gamestate.InMemoryGameState
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class CreateNewPlayerHandler : SimpleChannelInboundHandler<CreateNewPlayer>() {

    private val gameState:GameState = InMemoryGameState.getInstance();

    override fun messageReceived(p0: ChannelHandlerContext?, p1: CreateNewPlayer?) {
        TODO("Not yet implemented")
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        val channel = ctx?.channel() ?: return;
        val newSessionId = channel.id()?.asShortText() ?: return

        val newPlayer = msg as CreateNewPlayer;
        val oldSession = newPlayer.sessionId;

        println("${oldSession} - old session")
        println("new sessionID: ${newSessionId}")

        if(oldSession.isBlank() || !gameState.hasSession(oldSession)) {
            val freshPlayer = gameState.addNewPlayer(newPlayer.name, newSessionId, channel);
            ctx.writeAndFlush(CreateNewPlayer(freshPlayer.playerName, freshPlayer.gameId, freshPlayer.playerSessionId));
        } else {
            val gameId = gameState.getPlayerGameId(oldSession) ?: return;
            gameState.updatePlayer(oldSession, newSessionId, newPlayer.name, channel);
            ctx.writeAndFlush(CreateNewPlayer(newPlayer.name, gameId, newSessionId));
        }
    }

}
