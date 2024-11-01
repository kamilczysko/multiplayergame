package com.waldi.rocket.server.codec.newplayer

import com.waldi.rocket.server.codec.message.Message

class CreateNewPlayer(val name: String, val gameId: String, val sessionId: String): Message {
}
