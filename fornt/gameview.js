import { rockets } from "./connector.js";

const app = new PIXI.Application();
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
app.ticker.add((ticker) => {
    elapsed += ticker.deltaTime;
    console.log(elapsed)
    const myRocket = rocketSprites[getPlayerId()];
    if (myRocket) {
        scale = lerp(scale, -40, 0.1);
        container.scale.set(scale);
        container.scale.x *= -1;
        container.x = app.renderer.width / 2 - myRocket.x * container.scale.x;
        container.y = app.renderer.height / 1.2 - myRocket.y * container.scale.y;
    }

    Object.values(rockets).forEach(rocket => {
        const r = rocketSprites[rocket.playerId];
        r.x = lerp(r.x, rocket.x, 0.07);
        r.y = lerp(r.y, rocket.y, 0.07);
        r.rotate = lerp(r.rotate, rocket.angle)
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
        .rect(x, y, width, heigth)
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
