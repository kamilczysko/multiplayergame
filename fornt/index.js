let socket = null;
const INIT_NEW_PLAYER = 0x01;

const hasActiveSession = getSessionFromCookie() !== "";

document.getElementById("nameInput").value = getNameFromCookie() || "";

if(hasActiveSession) {
    makeConnection();
}

function joinGame() {
    const name = document.getElementById("nameInput").value;
    if (!name || hasActiveSession) {
        return;
    }
    makeConnection();
}

function makeConnection() {
    socket = new WebSocket("ws://127.0.0.1:60231/game");
    socket.binaryType = "arraybuffer";

    socket.onopen = (e) => {
        console.log("has connection...");
        const name = document.getElementById("nameInput").value;
        const textEncoder = new TextEncoder();
        const existingSessionCookieBytes = textEncoder.encode(getCookieValue("sessionId"));
        const gameId = textEncoder.encode(getCookieValue("gameId"))
        const nameBytes = textEncoder.encode(name);

        const buffer = new Uint8Array(nameBytes.length + 2 + existingSessionCookieBytes.length + 5);
        buffer[0] = INIT_NEW_PLAYER;
        buffer[1] = nameBytes.length;
        buffer.set(nameBytes, 2);
        buffer.set(gameId, nameBytes.length + 2)
        buffer.set(existingSessionCookieBytes, nameBytes.length + 2 + 5)

        socket.send(buffer)
    }

    socket.onmessage = (event) => {
        const dataView = new DataView(new Uint8Array(event.data).buffer);
        const messageType = dataView.getUint8(0);
        switch (messageType) {
            case 0x01:
                decodeNewUser(dataView, event.data);
                break;
        }

    }
}

function decodeNewUser(dataView, bytes) {
    const buffer = new Uint8Array(bytes).buffer;

    const sessionIdLength = dataView.getUint8(1);
    const sessionIdBytes = new Uint8Array(buffer, 2, sessionIdLength);
    const sessionId = new TextDecoder("utf-8").decode(sessionIdBytes);
    console.log("new Session" + sessionId)

    const gameIdBytes = new Uint8Array(buffer, 2 + sessionIdLength, 5);
    const gameId = new TextDecoder("utf-8").decode(gameIdBytes);

    console.log("new game id " + gameId)

    const nameBytes = new Uint8Array(buffer, 2 + sessionIdLength + 5)
    const name = new TextDecoder("utf-8").decode(nameBytes);
    console.log("new Name: " + name)

    document.cookie = `name=${name};`
    document.cookie = `sessionId=${sessionId};`
    document.cookie = `gameId=${gameId};`;
}

function getSessionFromCookie() {
    return document.cookie.split(";")
        .filter(chunk => chunk.includes("sessionId"))
        .map(sessionChunk => sessionChunk.split("=")[1])[0] || ""
}

function getNameFromCookie() {
    return document.cookie.split(";")
        .filter(chunk => chunk.includes("name"))
        .map(sessionChunk => sessionChunk.split("=")[1])[0]
}

function setCookie(v, k) {
    document.cookie = `${v} = ${k};`
}

function getCookieValue(key) {
    return document.cookie.split(";")
        .filter(chunk => chunk.includes(key))
        .map(sessionChunk => sessionChunk.split("=")[1])
}

function leaveGame() {
    setCookie("sessionId", "")
    setCookie("gameId", "")
}