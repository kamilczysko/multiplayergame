@file:JvmName("Lwjgl3Launcher")

package com.waldi.rocket.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.waldi.rocket.gameengine.Main

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
      return
    Lwjgl3Application(Main(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("RocketGame")
        setWindowedMode(560, 1324)
        setWindowIcon("logo.png")
    })
}
