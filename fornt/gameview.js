import { rockets, sendSteeringAction } from "./connector.js";

const app = new PIXI.Application({
    interactionFrequency: 2000
});
await app.init({ antialias: true, resizeTo: window })
document.getElementById("gameplay").appendChild(app.canvas)

const rocketSprites = []

const graphics = new PIXI.Graphics();

const container = new PIXI.Container();

container.x = app.screen.width / 2;
container.y = app.screen.height;

let scale = -10;

// container.scale.set(scale);
container.scale.x *= -1;

app.stage.addChild(container);

app.stage.interactive = true;
app.stage.eventMode = 'static';
app.stage.hitArea = app.screen;

let accelerate = false;
let angle = 0;
let interval = null;

app.stage.on('pointerdown', (event) => {
    accelerate = true;
    interval = setInterval(() => sendSteeringAction(angle, accelerate), 100)
});
app.stage.on('pointerup', (event) => {
    accelerate = false;
    clearInterval(interval)
    sendSteeringAction(angle, false)
});
app.stage.on('pointerupoutside', (event) => {
    accelerate = false;
    clearInterval(interval)
    sendSteeringAction(angle, false)
});

app.stage.on('pointermove', (event) => {
    if (accelerate === false) {
        return
    }
    const rocket = rocketSprites[getPlayerId()];
    if (rocket == null) {
        return;
    }
    const mousePosition = container.toLocal(event.data.global);
    let deltaX = mousePosition.x - rocket.x;
    let deltaY = mousePosition.y - rocket.y;
    angle = parseInt(Math.atan2(deltaX, deltaY) / Math.PI * 100);
});

let lastTimeStamp = performance.now();
let startTime = 0;
const duration = 5;
// app.ticker.speed = 2;
app.ticker.add((delta) => {

    const myRocket = rocketSprites[getPlayerId()];
    if (myRocket) {
        scale = lerp(scale, -10, 0.1);
        container.scale.set(scale);
        container.scale.x *= -1;
        container.x = app.renderer.width / 2 - myRocket.x * container.scale.x;
        container.y = app.renderer.height / 1.2 - myRocket.y * container.scale.y;
    }

    const now = performance.now();
    lastTimeStamp = now;

    const progress = Math.min((now - startTime) / duration, 1);

    Object.values(rockets).forEach(rocketPositions => {
        if(rocketPositions.length <= 2) {
            return;
        }
        const rocket = rocketPositions[0];
        const r = rocketSprites[rocket.playerId];
        r.x = interpolate(r.x, rocket.x, progress)
        r.y = interpolate(r.y, rocket.y, progress)
        r.rotation = interpolate(r.rotation, rocket.angle, progress)
        if (progress == 1) {
            rocketPositions.shift();
            startTime = performance.now();
        }
    })
})

function interpolate(start, end, time) {
    const res = (1 - time) * start + time * end
    // console.log(start, end, time, res)
    return res
}

function lerp(start, end, amt) {
    return (1 - amt) * start + amt * end
}


function getPlayerId() {
    return document.cookie.split(";")
        .filter(chunk => chunk.includes("playerId"))
        .map(sessionChunk => sessionChunk.split("=")[1])[0]
}

export function addBlock(x, y, width, heigth) {
    const block = graphics
        .rect(x, y, width, heigth)
        .fill("white");
    container.addChild(block);
}

export function addRocket(x, y, angle, playerId) {
    if (rocketSprites[playerId] != null) {
        return;
    }
    const block = new PIXI.Graphics()
        .rect(0, 0, 1, 3)
        .fill("green");
    container.addChild(block);
    rocketSprites[playerId] = block;
    block.pivot.x = 0.5;
    block.pivot.y = 1.5;
    block.x = x;
    block.y = y;
    block.rotate = 0;
}


export function addMoon(x, y, radius) {
    const moon = graphics
        .circle(x, y, radius)
        .fill('white');
    container.addChild(moon);
}