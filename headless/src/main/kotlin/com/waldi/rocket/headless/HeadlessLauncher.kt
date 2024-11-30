@file:JvmName("HeadlessLauncher")

package com.waldi.rocket.headless

import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import com.waldi.rocket.gameengine.MainHeadless


/** Launches the headless application. Can be converted into a server application or a scripting utility. */
fun main() {
    HeadlessApplication(MainHeadless(), HeadlessApplicationConfiguration().apply {
        // When this value is negative, Main#render() is never called:
        updatesPerSecond = 25;
    })
}
