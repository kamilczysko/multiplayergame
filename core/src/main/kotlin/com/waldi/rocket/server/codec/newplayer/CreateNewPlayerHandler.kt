package com.waldi.rocket.server.codec.newplayer

import com.waldi.rocket.server.gamestate.GameServerState
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import mu.two.KotlinLogging

class CreateNewPlayerHandler(private val gameServerState: GameServerState) : SimpleChannelInboundHandler<CreateNewPlayer>() {

    private val logger = KotlinLogging.logger{}

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
        val freshPlayer = gameServerState.addOrUpdatePlayer(oldSession, newSessionId, newPlayer.name, channel);

        logger.info { "Create new player or refresh session for $freshPlayer" }

        ctx.writeAndFlush(CreateNewPlayer(freshPlayer.playerName, freshPlayer.gameId, freshPlayer.playerSessionId));

        logger.info { "Send map data to player ${freshPlayer.gameId}" }

        val mapData = gameServerState.getMapData();
        ctx.writeAndFlush(mapData);
    }

}
