package com.waldi.rocket.server.codec.newplayer

import com.waldi.rocket.server.codec.Message

class CreateNewPlayer(val name: String, val gameId: String, val sessionId: String): Message {
}
