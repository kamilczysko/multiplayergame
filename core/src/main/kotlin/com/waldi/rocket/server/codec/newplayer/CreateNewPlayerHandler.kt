package com.waldi.rocket.server.codec.newplayer

import com.waldi.rocket.server.gamestate.GameState
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class CreateNewPlayerHandler(private val gameState: GameState) : SimpleChannelInboundHandler<CreateNewPlayer>() {

    override fun messageReceived(p0: ChannelHandlerContext?, p1: CreateNewPlayer?) {
        TODO("Not yet implemented")
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if (msg !is CreateNewPlayer) {
            ctx?.fireChannelRead(msg);
            return;
        }
        val channel = ctx?.channel() ?: return;
        val newSessionId = channel.id()?.asShortText() ?: return

        val newPlayer: CreateNewPlayer = msg
        val oldSession = newPlayer.sessionId;

        val freshPlayer = gameState.addOrUpdatePlayer(oldSession, newSessionId, newPlayer.name, channel);

        ctx.writeAndFlush(CreateNewPlayer(freshPlayer.playerName, freshPlayer.gameId, freshPlayer.playerSessionId));

        val mapData = gameState.getMapData();
        ctx.writeAndFlush(mapData);
    }

}
