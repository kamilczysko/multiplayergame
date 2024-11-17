package com.waldi.rocket.server

import com.waldi.rocket.gameengine.gameworld.GameWorld
import com.waldi.rocket.server.codec.GameDecoder
import com.waldi.rocket.server.codec.leaveplayer.LeavePlayerHandler
import com.waldi.rocket.server.codec.mapdata.MapDataEncoder
import com.waldi.rocket.server.codec.newplayer.CreateNewPlayerEncoder
import com.waldi.rocket.server.codec.newplayer.CreateNewPlayerHandler
import com.waldi.rocket.server.codec.steering.SteerDecoder
import com.waldi.rocket.server.codec.steering.SteeringHandler
import com.waldi.rocket.server.codec.steering.SteeringMessage
import com.waldi.rocket.server.gamestate.GameServerState
import com.waldi.rocket.shared.GameController
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpHeaderNames.WEBSOCKET_PROTOCOL
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.stream.ChunkedWriteHandler
import java.net.InetAddress

class SocketChannelInitializer(private val gameServerState: GameServerState, private val gameWorld: GameWorld) : ChannelInitializer<SocketChannel>() {

    override fun initChannel(channel: SocketChannel) {
        val host = InetAddress.getLocalHost();
        val pipeline = channel.pipeline();
        pipeline.addLast(HttpServerCodec());
        pipeline.addLast(ChunkedWriteHandler());
        pipeline.addLast(HttpObjectAggregator(8192));
        pipeline.addLast(WebSocketServerProtocolHandler("ws://$host:60231/game", WEBSOCKET_PROTOCOL.toString(), true, 655360));

        pipeline.addLast(GameDecoder())
        pipeline.addLast(CreateNewPlayerEncoder())
        pipeline.addLast(MapDataEncoder())
        pipeline.addLast(SteeringHandler(gameWorld, gameServerState))
        pipeline.addLast(CreateNewPlayerHandler(gameServerState))
        pipeline.addLast(LeavePlayerHandler(gameServerState))
    }
}
