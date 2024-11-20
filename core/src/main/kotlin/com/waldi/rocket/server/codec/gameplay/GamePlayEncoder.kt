package com.waldi.rocket.server.codec.gameplay

import com.waldi.rocket.shared.RocketData
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

fun encode(rocketData: List<RocketData>, timestamp: Int): ByteBuf {
    val buffer = Unpooled.buffer().alloc().buffer(1 + 25 * rocketData.size +  4);

    buffer.writeByte(0x05);
    buffer.writeInt(timestamp);

    for(rocket in rocketData) {
        buffer.writeBytes(rocket.rocketId.toByteArray())
        buffer.writeDouble(rocket.x.toDouble());
        buffer.writeDouble(rocket.y.toDouble());
        buffer.writeByte((rocket.angleRad / Math.PI * 100).toInt()); // angle in percentage 0 - 0 rad, 100 - PI rad, -100 - -PI rad etc
        buffer.writeByte((rocket.fuel * 100).toInt());
        buffer.writeByte(rocket.points.toInt());
    }

    return buffer;
}
