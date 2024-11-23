import './style/style.css'
import "./graphics/gameview.ts"
import { Connector } from './connection/connection.ts';
import { joinGameData } from './connection/controller.ts';


document.getElementById("join")!.onclick = () => {
    const name: string = document.getElementById("playerName")!.value;
    const connector = new Connector(() => joinGameData(name));
}


document.getElementById("leave")!.onclick = () => {
    setCookie("name", "");
    setCookie("sessionId", "");
    setCookie("playerId", "");
}

function setCookie(value: string, key: string) {
    document.cookie = `${value} = ${key};`;
}