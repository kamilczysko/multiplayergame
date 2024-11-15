package com.waldi.rocket.server

import com.waldi.rocket.server.gamestate.GameServerState
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import mu.two.KotlinLogging

private const val PORT = 60231;

private val logger = KotlinLogging.logger {  }

fun bootstrapServer(gameServerState: GameServerState) {
    logger.info { "Starting server..." }
    val bossGroup = NioEventLoopGroup(1);
    val workGroup = NioEventLoopGroup();

    val bootstrap = ServerBootstrap();
    bootstrap.group(bossGroup, workGroup)
        .channel(NioServerSocketChannel::class.java)
        .localAddress(PORT)
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childHandler(SocketChannelInitializer(gameServerState));

    try {
        val sync: ChannelFuture = bootstrap.bind().sync();
        sync.channel().closeFuture().sync();
    } finally {

        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}

