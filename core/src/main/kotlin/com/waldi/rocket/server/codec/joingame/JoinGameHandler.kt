package com.waldi.rocket.server.codec.resetplayer

import com.waldi.rocket.server.gamestate.GameServerState
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import mu.two.KotlinLogging

class JoinGameHandler(private val gameServerState: GameServerState) : SimpleChannelInboundHandler<JoinGame>() {
    private val logger = KotlinLogging.logger{}

    override fun messageReceived(p0: ChannelHandlerContext?, p1: JoinGame?) {
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if(msg !is JoinGame) {
            ctx?.fireChannelRead(msg);
            return
        }
        val sessionId = ctx?.channel()?.id()?.asShortText()
        sessionId ?: return;
        gameServerState.joinGame(sessionId);
    }
}
