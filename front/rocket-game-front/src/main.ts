import './style/style.css'
import "./graphics/gameview.ts"
import { Connector } from './connection/connection.ts';
import { joinGame, joinNewGameSession, leaveGame, resetRocket } from './connection/controller.ts';

export let sendMessage: (buffer: ArrayBuffer) => void;


const connection = new Connector(() => joinNewGameSession());

document.getElementById("join")!.onclick = () => {
    connection.socket?.send(joinGame());
    document.getElementById("join")!.style.display = "none"
    document.getElementById("leave")!.style.display = "block"
}

document.getElementById("leave")!.onclick = () => {
    leaveGame()
    setCookie("sessionId", "");
    setCookie("playerId", "");
    document.getElementById("join")!.style.display = "block"
    document.getElementById("leave")!.style.display = "none"
    window.location.reload();
}


document.getElementById("reset")!.onclick = () => {
    resetRocket();
}

function setCookie(value: string, key: string) {
    document.cookie = `${value} = ${key};`;
}
