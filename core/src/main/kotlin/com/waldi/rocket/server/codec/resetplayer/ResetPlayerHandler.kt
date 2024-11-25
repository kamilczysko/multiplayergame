package com.waldi.rocket.server.codec.resetplayer

import com.waldi.rocket.server.gamestate.GameServerState
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import mu.two.KotlinLogging

class ResetPlayerHandler(private val gameServerState: GameServerState) : SimpleChannelInboundHandler<ResetPlayer>() {
    private val logger = KotlinLogging.logger{}

    override fun messageReceived(p0: ChannelHandlerContext?, p1: ResetPlayer?) {
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if(msg !is ResetPlayer) {
            ctx?.fireChannelRead(msg);
            return
        }
        val sessionId = ctx?.channel()?.id()?.asShortText()
        sessionId ?: return;
        logger.info("RESET PLAYER: ${sessionId}")
        gameServerState.restartRocket(sessionId)
    }
}
