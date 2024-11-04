package com.waldi.rocket.server

import com.waldi.rocket.server.codec.playerlistchange.PlayerLeaveDispatcher
import com.waldi.rocket.server.codec.playerlistchange.PlayerListChangeDispatcher
import com.waldi.rocket.server.gamestate.GameState
import com.waldi.rocket.server.gamestate.InMemoryGameState
import com.waldi.rocket.server.gamestate.events.GameStateEventType
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel

private const val PORT = 60231;

fun runServer() {
    val gameState: GameState = InMemoryGameState.getInstance();
    gameState.addListener(PlayerListChangeDispatcher(), GameStateEventType.PLAYER_LIST_UPDATE);
    gameState.addListener(PlayerLeaveDispatcher(), GameStateEventType.PLAYER_LEAVE);
    bootstrapServer(gameState)
}

private fun bootstrapServer(gameState: GameState) {
    val bossGroup = NioEventLoopGroup(1);
    val workGroup = NioEventLoopGroup(); //poczytać co to jest

    val bootstrap = ServerBootstrap();
    bootstrap.group(bossGroup, workGroup)
        .channel(NioServerSocketChannel::class.java)
        .localAddress(PORT)
        .option(ChannelOption.SO_BACKLOG, 128) //poczytać
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childHandler(SocketChannelInitializer(gameState)); //poczytać

    try {
        val sync: ChannelFuture = bootstrap.bind().sync();
        sync.channel().closeFuture().sync();
    } finally {

        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}

fun main() {
    runServer()
}
