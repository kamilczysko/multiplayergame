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

let elapsed = 0.0;

function getAngle(rocket, mouseX, mouseY) {
    const deltaX = mouseX - rocket.x;
    const deltaY = mouseY - rocket.y;
    const angle = Math.atan2(deltaY, deltaX);
    return angle;
}



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
    if(accelerate === false) { 
        return
    }
    const rocket = rocketSprites[getPlayerId()];
    if(rocket == null) {
        return;
    }
    const mousePosition = rocket.toLocal(event.data.global);
    angle = parseInt(getAngle(rocket, mousePosition.x, mousePosition.y) / Math.PI * 100);
});

app.ticker.add((ticker) => {
    elapsed += ticker.deltaTime;
    const myRocket = rocketSprites[getPlayerId()];
    if (myRocket) {
        scale = lerp(scale, -10, 0.1);
        container.scale.set(scale);
        container.scale.x *= -1;
        container.x = app.renderer.width / 2 - myRocket.x * container.scale.x;
        container.y = app.renderer.height / 1.2 - myRocket.y * container.scale.y;
        // container.pivot.x = app.renderer.width / 2
        // container.pivot.y = app.renderer.height / 1.2
    }

    Object.values(rockets).forEach(rocket => {
        const r = rocketSprites[rocket.playerId];
        r.x = lerp(r.x, rocket.x, 0.07);
        r.y = lerp(r.y, rocket.y, 0.07);
        r.rotation = lerp(r.rotation, rocket.angle, 0.05)
        // container.rotation = r.rotation
        // //add rotation
    })
})

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
        .rect(x,y , width, heigth)
        .fill("white");
        container.addChild(block);
}

export function addRocket(x, y, angle, playerId) {
    if (rocketSprites[playerId] != null) {
        return;
    }
    const block = new PIXI.Graphics()
        .rect(0, 0 , 1, 3)
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