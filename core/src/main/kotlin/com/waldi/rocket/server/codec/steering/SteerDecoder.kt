package com.waldi.rocket.server.codec.steering

import com.waldi.rocket.server.codec.MessageDecoder
import io.netty.buffer.ByteBuf

class SteerDecoder: MessageDecoder<SteeringMessage> {
    override fun decode(bytes: ByteBuf): SteeringMessage {
        val angleFromBytes = bytes.readByte().toInt()
        val angle = ((angleFromBytes / 100.0) * Math.PI).toFloat();
        bytes.readerIndex(2);
        val isAccelerating = bytes.readBoolean();

        return SteeringMessage(angle, isAccelerating);
    }

}
