package com.waldi.rocket.server.codec.leaveplayer

import com.waldi.rocket.server.gamestate.GameServerState
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import mu.two.KotlinLogging

class LeavePlayerHandler(val gameServerState: GameServerState): SimpleChannelInboundHandler<LeavePlayer>() {

    private val logger = KotlinLogging.logger{}

    override fun messageReceived(p0: ChannelHandlerContext?, p1: LeavePlayer?) {
        TODO("Not yet implemented")
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        logger.info { "Remove player..." }
        if(msg !is LeavePlayer) {
            return;
        }
        if(ctx?.channel() != null) {
            logger.info { "Remove player ${ctx.channel().id().asShortText()}" }
            gameServerState.removePlayer(ctx.channel().id().asShortText());
        }
    }
}
