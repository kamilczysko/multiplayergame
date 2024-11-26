import './style/style.css'
import "./graphics/gameview.ts"
import { Connector } from './connection/connection.ts';
import { joinGameData, leaveGame, resetRocket } from './connection/controller.ts';

export let sendMessage: (buffer: ArrayBuffer) => void;

if (getCookieValue("playerId")) {
    new Connector(() => joinGameData());
}

function getCookieValue(key: string) {
    return document.cookie
        .split(";")
        .filter((chunk) => chunk.includes(key))
        .map((sessionChunk) => sessionChunk.split("=")[1])[0];
}

document.getElementById("join")!.onclick = () => {
    new Connector(() => joinGameData());
}

document.getElementById("leave")!.onclick = () => {
    leaveGame()
    setCookie("sessionId", "");
    setCookie("playerId", "");
}

document.getElementById("reset")!.onclick = () => {
    resetRocket();
}

function setCookie(value: string, key: string) {
    document.cookie = `${value} = ${key};`;
}