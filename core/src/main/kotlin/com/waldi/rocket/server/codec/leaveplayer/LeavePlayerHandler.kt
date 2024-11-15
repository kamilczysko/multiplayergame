package com.waldi.rocket.server.codec.leaveplayer

import com.waldi.rocket.server.gamestate.GameState
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class LeavePlayerHandler(val gameState: GameState): SimpleChannelInboundHandler<LeavePlayer>() {
    override fun messageReceived(p0: ChannelHandlerContext?, p1: LeavePlayer?) {
        TODO("Not yet implemented")
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if(msg !is LeavePlayer) {
            return;
        }
        if(ctx?.channel() != null) {
                gameState.removePlayer(ctx.channel().id().asShortText());
            }
    }
}
