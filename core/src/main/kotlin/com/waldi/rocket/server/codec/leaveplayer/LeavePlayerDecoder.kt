package com.waldi.rocket.server.codec.leaveplayer

import com.waldi.rocket.server.codec.MessageDecoder
import io.netty.buffer.ByteBuf
import io.netty.util.CharsetUtil

class LeavePlayerDecoder: MessageDecoder<LeavePlayer> {
    override fun decode(bytes: ByteBuf): LeavePlayer {
        return LeavePlayer(bytes.toString(CharsetUtil.UTF_8));
    }
}
