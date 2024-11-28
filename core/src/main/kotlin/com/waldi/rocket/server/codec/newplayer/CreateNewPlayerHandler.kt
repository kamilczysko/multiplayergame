package com.waldi.rocket.server.codec.newplayer

import com.waldi.rocket.server.gamestate.GameServerState
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import mu.two.KotlinLogging

class CreateNewPlayerHandler(private val gameServerState: GameServerState) :
    SimpleChannelInboundHandler<CreateNewPlayer>() {

    private val logger = KotlinLogging.logger {}

    override fun messageReceived(p0: ChannelHandlerContext?, p1: CreateNewPlayer?) {
        TODO("Not yet implemented")
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if (msg !is CreateNewPlayer) {
            ctx?.fireChannelRead(msg);
            return;
        }
        logger.info("NEW PLAYER HANDLER")
        val channel = ctx?.channel() ?: return;
        val newSessionId = channel.id()?.asShortText() ?: return

        val newPlayer: CreateNewPlayer = msg
        val oldSession = newPlayer.sessionId;
        if (oldSession.isBlank()) {
            logger.info { "Init new session"};
            val newPlayerWithoutGameplay = gameServerState.initNewPlayer(newSessionId, channel);
            ctx.writeAndFlush(CreateNewPlayer(newPlayerWithoutGameplay.gameId, newPlayerWithoutGameplay.playerSessionId));
        } else {
            val freshPlayer = gameServerState.refreshPlayer(oldSession, newSessionId, channel);
            logger.info { "Refresh session for $freshPlayer" }
            ctx.writeAndFlush(CreateNewPlayer(freshPlayer.gameId, freshPlayer.playerSessionId));
        }
        logger.info("PLAYER PLAYER")
        val mapData = gameServerState.getMapData();
        logger.info("SEND DATA: "+mapData)
        ctx.writeAndFlush(mapData);
    }

}
