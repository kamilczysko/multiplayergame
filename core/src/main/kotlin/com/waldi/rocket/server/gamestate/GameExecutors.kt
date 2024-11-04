package com.waldi.rocket.server.gamestate

import java.util.concurrent.Executors

public val EXECUTORS = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
