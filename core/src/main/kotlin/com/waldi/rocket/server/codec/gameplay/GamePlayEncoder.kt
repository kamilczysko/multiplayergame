package com.waldi.rocket.server.codec.gameplay

import com.waldi.rocket.shared.RocketData
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

fun encode(timestampToRocketDataList: Map<Int, List<RocketData>>): ByteBuf {
    val totalNumberOfRockets = timestampToRocketDataList.values.stream().mapToInt() { it.size }.sum();
    val buffer = Unpooled.buffer().alloc().buffer(1 + (4 + 1) * timestampToRocketDataList.size + totalNumberOfRockets * (4 + 16)); //todo fix

    buffer.writeByte(0x05);
    //header, number of items, timestamp, items..., number of items, timestamp, items...

    timestampToRocketDataList.forEach { (timestamp, rocketDataList) ->
        buffer.writeInt(timestamp);
        buffer.writeByte(rocketDataList.size);
        encodeBatch(rocketDataList, buffer);
    }

    return buffer;
}

fun encodeBatch(rocketData: List<RocketData>, buffer: ByteBuf): ByteBuf {
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


