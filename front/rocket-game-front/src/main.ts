import './style/style.css'
import "./graphics/gameview.ts"
import { Connector } from './connection/connection.ts';
import { joinGameData } from './connection/controller.ts';

export let sendMessage: (buffer: ArrayBuffer) => void;

if (getCookieValue("name")) {
    const connector = new Connector(() => joinGameData(getCookieValue("name")));
    sendMessage = (data: ArrayBuffer) => { connector.sendMessage(data) };
}

function getCookieValue(key: string) {
    return document.cookie
        .split(";")
        .filter((chunk) => chunk.includes(key))
        .map((sessionChunk) => sessionChunk.split("=")[1])[0];
}

document.getElementById("join")!.onclick = () => {
    const name: string = document.getElementById("playerName")!.value;
    const connector = new Connector(() => joinGameData(name));
    sendMessage = (data: ArrayBuffer) => { connector.sendMessage(data) };
}

document.getElementById("leave")!.onclick = () => {
    setCookie("name", "");
    setCookie("sessionId", "");
    setCookie("playerId", "");
}

function setCookie(value: string, key: string) {
    document.cookie = `${value} = ${key};`;
}