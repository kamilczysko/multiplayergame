package com.waldi.rocket.server.codec.steering

import com.waldi.rocket.gameengine.gameworld.GameWorld
import com.waldi.rocket.server.gamestate.GameServerState
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class SteeringHandler(private val gameWorld: GameWorld, private val gameServerState: GameServerState) : SimpleChannelInboundHandler<SteeringMessage>() {
    override fun messageReceived(p0: ChannelHandlerContext?, p1: SteeringMessage?) {
        TODO("Not yet implemented")
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if (msg !is SteeringMessage) {
            ctx?.fireChannelRead(msg);
            return;
        }

        val sessionId = ctx?.channel()?.id()?.asShortText() ?: return
        val rocketId = gameServerState.getPlayerBySessionId(sessionId)?.gameId ?: return;

        if(msg.isAccelerating) {
            gameWorld.startAccelerating(rocketId);
        } else {
            gameWorld.stopAccelerating(rocketId);
        }
        if(gameWorld.allowedTransform()) {
            gameWorld.rotate(rocketId, msg.angle);
        }
    }
}
