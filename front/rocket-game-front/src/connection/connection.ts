import { initSocket, leaveGame, readMessage } from "./controller";

export class Connector {

    socket: WebSocket | undefined;

    constructor(joinFunction: () => Uint8Array) {
        this.socket = new WebSocket("ws://192.168.8.60:5656/game");
        // this.socket = new WebSocket("ws://localhost:5656/game");

        this.socket.onopen = () => {
            console.log("Connection opened...");
            this.socket?.send(joinFunction());
            console.log("init socket")
            initSocket(this.socket!)
        }

        this.socket.onmessage = (msg: MessageEvent) => {
            readMessage(msg.data)
        }

        this.socket.onclose = () => {
            leaveGame();
        }
    }

    dispose() {
        this.socket?.close();
    }

}
