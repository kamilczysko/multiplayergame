package com.waldi.rocket.server.codec.steering

import com.waldi.rocket.server.codec.Message

class SteeringMessage(val angle: Float, val isAccelerating: Boolean) : Message {
}
