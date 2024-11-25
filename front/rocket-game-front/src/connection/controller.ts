import { addRocketToView, drawLevel, removeRocket, zoomOut } from "../graphics/gameview";
import { Level } from "../infrastructure/level";
import { Rocket } from "../infrastructure/rocket";

type playerData = {
  playerName: string;
  points: number;
};

export const players: Record<string, playerData> = {};
export const rockets: Record<string, Rocket> = {};

export async function readMessage(data: Blob) {
  const arrayBuffer = await data.arrayBuffer();
  const dataView: DataView = new DataView(new Uint8Array(arrayBuffer).buffer);
  const messageHeader = dataView.getUint8(0);
  switch (messageHeader) {
    case 0x01:
      console.log("My player initialization...");
      decodeNewUser(arrayBuffer);
      break;
    case 0x02:
      console.log("Received players list...");
      decodePlayersList(arrayBuffer);
      break;
    case 0x03:
      console.log("Remove player...");
      decodePlayerLeave(arrayBuffer);
      break;
    case 0x04:
      console.log("Received map data...");
      decodeMapInfo(arrayBuffer);
      break;
    case 0x05:
      decodeRockets(arrayBuffer);
      break;
  }
}

function decodeNewUser(bytes: ArrayBuffer) {
  const dataView = new DataView(new Uint8Array(bytes).buffer);
  const buffer = new Uint8Array(bytes).buffer;

  const sessionIdLength = dataView.getUint8(1);
  const sessionIdBytes = new Uint8Array(buffer, 2, sessionIdLength);
  const sessionId = new TextDecoder("utf-8").decode(sessionIdBytes);

  const playerIdIdBytes = new Uint8Array(buffer, 2 + sessionIdLength, 5);
  const playerId = new TextDecoder("utf-8").decode(playerIdIdBytes);

  const nameBytes = new Uint8Array(buffer, 2 + sessionIdLength + 5);
  const name = new TextDecoder("utf-8").decode(nameBytes);

  document.cookie = `name=${name};`;
  document.cookie = `sessionId=${sessionId};`;
  document.cookie = `playerId=${playerId};`;

  players[playerId] = { playerName: name, points: 0 };
}

function decodePlayersList(bytes: ArrayBuffer) {
  const buffer = new Uint8Array(bytes).buffer;
  const dataView = new DataView(buffer);
  let mark = 1;
  while (mark < dataView.byteLength) {
    const numberOfBytesForName = dataView.getUint8(mark);
    mark++;
    const name = new TextDecoder("utf-8").decode(
      new Uint8Array(buffer, mark, numberOfBytesForName)
    );
    mark = mark + numberOfBytesForName;
    const playerId = new TextDecoder("utf-8").decode(
      new Uint8Array(buffer, mark, 5)
    );
    mark += 5; //5 is equal to playerId.length
    players[playerId] = { playerName: name, points: 0 };
  }
}

function decodePlayerLeave(bytes: ArrayBuffer) {
  const buffer = new Uint8Array(bytes).buffer;
  const dataView = new DataView(buffer);
  let mark = 1;
  while (mark < dataView.byteLength) {
    const playerId: string = new TextDecoder("utf-8").decode(
      new Uint8Array(buffer, mark, 5)
    );
    mark += 5;
    removeRocket(playerId);
  }
}

function decodeMapInfo(bytes: ArrayBuffer) {
  const buffer = new Uint8Array(bytes).buffer;
  const dataView = new DataView(buffer);

  const newLevel = new Level();

  const moon = {
    x: dataView.getInt8(1),
    y: dataView.getUint8(2),
    radius: dataView.getUint8(3),
  };
  newLevel.setMoon(moon.x, moon.y, moon.radius);

  let mark = 4;
  while (mark < dataView.byteLength) {
    const block = {
      x: dataView.getInt8(mark),
      y: dataView.getUint8(++mark),
      width: dataView.getUint8(++mark),
      height: dataView.getUint8(++mark),
    };
    newLevel.addBlock(block.x, block.y, block.width, block.height);
    mark++;
  }
  drawLevel(newLevel);
}

let timestamp = -1;

function decodeRockets(bytes: ArrayBuffer) {
  const buffer = new Uint8Array(bytes).buffer;
  const dataView = new DataView(buffer);
  let mark = 0;
  const serverTimestamp = dataView.getUint32(++mark);
  if (serverTimestamp < timestamp) {
    return;
  }
  timestamp = serverTimestamp;
  mark += 4;
  while (mark < dataView.byteLength) {
    const playerId = new TextDecoder("utf-8").decode(new Uint8Array(buffer, mark, 5));
    mark += 5;
    const x = dataView.getFloat64(mark);
    mark += 8;
    const y = dataView.getFloat64(mark);
    mark += 8;
    const angle = dataView.getInt8(mark) / 100 * Math.PI;
    mark++;
    const fuel = dataView.getUint8(mark);
    mark++;
    const points = dataView.getUint8(mark);
    mark++;

    const rocket: Rocket = rockets[playerId];
    if (!rocket) {
      rockets[playerId] = new Rocket(x, y, angle, fuel, players[playerId]?.playerName, playerId);
      addRocketToView(rockets[playerId]);
    } else {
      rocket.addRocketState(x, y, angle, fuel);
    }
  }
}

function setCookie(value: string, key: string) {
  document.cookie = `${value} = ${key};`;
}

function getCookieValue(key: string) {
  return document.cookie
    .split(";")
    .filter((chunk) => chunk.includes(key))
    .map((sessionChunk) => sessionChunk.split("=")[1])[0];
}

export function leaveGame() {
  const playerIdFromCookie = getCookieValue("playerId");
  if (!playerIdFromCookie) {
    return;
  }
  const playerId = new TextEncoder().encode(playerIdFromCookie);
  const buffer = new Uint8Array(1 + playerId.length);

  buffer[0] = 0x03;
  buffer.set(playerId, 1);
  socket?.send(buffer);

  setCookie("sessionId", "");
  setCookie("playerId", "");
  zoomOut();
}

let socket: WebSocket;

export function initSocket(soc: WebSocket) {
  socket = soc;
}

export function sendSteeringAction(angle: number, accelerate: boolean): Int8Array {
  const buffer = new Int8Array(3);
  buffer[0] = 0x06;
  buffer[1] = angle;
  buffer[2] = accelerate ? 1 : 0;

  socket?.send(buffer);
  return buffer;
}

export function joinGameData(name: string): Uint8Array {
  const textEncoder = new TextEncoder();
  const existingSessionCookieBytes = textEncoder.encode(getCookieValue("sessionId"));
  const playerId = textEncoder.encode(getCookieValue("playerId"))
  const nameBytes = textEncoder.encode(name);

  const buffer = new Uint8Array(nameBytes.length + 2 + existingSessionCookieBytes.length + 5);
  buffer[0] = 0x01;
  buffer[1] = nameBytes.length;
  buffer.set(nameBytes, 2);
  buffer.set(playerId, nameBytes.length + 2)
  buffer.set(existingSessionCookieBytes, nameBytes.length + 2 + 5)

  return buffer;
}

export function resetRocket() {
  console.log("reset")
  const buffer = new Uint8Array(1);
  buffer[0] = 0x07;
  socket!.send(buffer);
}