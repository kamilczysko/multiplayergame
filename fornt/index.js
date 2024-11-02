let socket = null;
const INIT_NEW_PLAYER = 0x01;

const players = new Map()
const hasActiveSession = getSessionFromCookie() !== "";

document.getElementById("nameInput").value = getNameFromCookie() || "";

if (hasActiveSession) {
    makeConnection();
}

function joinGame() {
    const name = document.getElementById("nameInput").value;
    if (!name || hasActiveSession) {
        console.log("not connecting")
        return;
    }
    makeConnection();
}

onfocus = () => {
    if(socket !== null && socket.readyState === WebSocket.CLOSED) {
        console.log("Connection was closed. Reconnecting..")
        makeConnection();
    }
}

function makeConnection() {
    socket = new WebSocket("ws://127.0.0.1:60231/game");
    socket.binaryType = "arraybuffer";

    socket.onopen = (e) => {
        console.log("has connection...");
        const name = document.getElementById("nameInput").value;
        const textEncoder = new TextEncoder();
        const existingSessionCookieBytes = textEncoder.encode(getCookieValue("sessionId"));
        const playerId = textEncoder.encode(getCookieValue("playerId"))
        const nameBytes = textEncoder.encode(name);

        const buffer = new Uint8Array(nameBytes.length + 2 + existingSessionCookieBytes.length + 5);
        buffer[0] = INIT_NEW_PLAYER;
        buffer[1] = nameBytes.length;
        buffer.set(nameBytes, 2);
        buffer.set(playerId, nameBytes.length + 2)
        buffer.set(existingSessionCookieBytes, nameBytes.length + 2 + 5)

        socket.send(buffer)
    }

    socket.onmessage = (event) => {
        const dataView = new DataView(new Uint8Array(event.data).buffer);
        const messageType = dataView.getUint8(0);
        console.log("got message: " + messageType)
        switch (messageType) {
            case 0x01:
                decodeNewUser(event.data);
                break;
            case 0x02:
                decodeUserData(event.data);
                break;
            case 0x03:
                decodePlayerLeave(event.data);
                break;
        }
    }
}

function decodePlayerLeave(bytes) {
    console.log("got message 3")
    const buffer = new Uint8Array(bytes).buffer;
    const dataView = new DataView(buffer);
    let mark = 1;
    while (mark < dataView.byteLength) {
        const playerId = new TextDecoder("utf-8").decode(new Uint8Array(buffer, mark, 5));
        mark += 5;
        document.getElementById(playerId).remove();
    }
}

function decodeUserData(bytes) {
    const buffer = new Uint8Array(bytes).buffer;
    const dataView = new DataView(buffer);
    let mark = 1;
    const players = new Map()
    while (mark < dataView.byteLength) {
        const numberOfBytesForName = dataView.getUint8(mark);
        mark++;
        const name = new TextDecoder("utf-8").decode(new Uint8Array(buffer, mark, numberOfBytesForName));
        mark = mark + numberOfBytesForName;
        const playerId = new TextDecoder("utf-8").decode(new Uint8Array(buffer, mark, 5));
        mark += 5; //5 is equal to playerId.length
        const points = dataView.getUint8(mark);
        mark++;
        players[playerId] = { "playerName": name, "points": points }
        drawScoreBoard(playerId, name, points);
    }
}

function decodeNewUser(bytes) {
    const dataView = new DataView(new Uint8Array(bytes).buffer);
    const buffer = new Uint8Array(bytes).buffer;

    const sessionIdLength = dataView.getUint8(1);
    const sessionIdBytes = new Uint8Array(buffer, 2, sessionIdLength);
    const sessionId = new TextDecoder("utf-8").decode(sessionIdBytes);

    const playerIdIdBytes = new Uint8Array(buffer, 2 + sessionIdLength, 5);
    const playerId = new TextDecoder("utf-8").decode(playerIdIdBytes);

    const nameBytes = new Uint8Array(buffer, 2 + sessionIdLength + 5)
    const name = new TextDecoder("utf-8").decode(nameBytes);

    document.cookie = `name=${name};`
    document.cookie = `sessionId=${sessionId};`
    document.cookie = `playerId=${playerId};`;

    highlightOnScoreboard(playerId);
}

function highlightOnScoreboard(playerId) {
    const playersScore = document.getElementById(playerId);
    if (playersScore) {
        playersScore.className = "playerScore me"
    }
}

function drawScoreBoard(playerId, name, points) {
    const playersScore = document.getElementById(playerId);
    if (!playersScore) {
        addNewScore(playerId, name, points);
    } else {
        playersScore.getElementsByClassName("scoreBoardPoints")[0].innerText = points;
    }
}

function addNewScore(playerId, name, points) {
    const li = document.createElement("li");
    li.id = playerId;
    li.className = "playerScore"
    if (playerId === getCookieValue("playerId")) {
        li.className = "playerScore me"
    }

    const nameElement = document.createElement("a")
    nameElement.className = "scoreBoardName"
    nameElement.innerText = name;

    const pointsElement = document.createElement("a")
    pointsElement.className = "scoreBoardPoints"
    pointsElement.innerText = points

    li.appendChild(nameElement);
    li.appendChild(pointsElement);

    document.getElementById("scoreBoardList").appendChild(li);
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
        .map(sessionChunk => sessionChunk.split("=")[1])[0]
}

function leaveGame() {
    const playerIdFromCookie = getCookieValue("playerId");
    if(!playerIdFromCookie) {
        return;
    }
    const playerId = new TextEncoder().encode(playerIdFromCookie);
    const buffer = new Uint8Array(1 + playerId.length);
    buffer[0] = 0x03;
    buffer.set(playerId, 1, playerId.length)

    socket.send(buffer);

    setCookie("sessionId", "")
    setCookie("playerId", "")

    document.getElementById("scoreBoardList").innerHTML = ""
}