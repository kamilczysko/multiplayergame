import { players, rockets, sendSteeringAction } from "./connector.js";

const app = new PIXI.Application({
    interactionFrequency: 2000
});
await app.init({ antialias: true, resizeTo: window })
document.getElementById("gameplay").appendChild(app.canvas)

const rocketSprites = []

const graphics = new PIXI.Graphics();

const container = new PIXI.Container();
const containerIndicator = new PIXI.Container();

container.x = app.screen.width / 2;
container.y = app.screen.height;


containerIndicator.x = app.screen.width / 2;
containerIndicator.y = app.screen.height;

let scale = -10;

container.scale.x *= -1;

app.stage.addChild(container);
app.stage.addChild(containerIndicator)

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
const duration = 3;
app.ticker.add((delta) => {

    const myRocket = rocketSprites[getPlayerId()];
    if (myRocket) {
        scale = lerp(scale, -35, 0.1);
        container.scale.set(scale);
        container.scale.x *= -1;
        container.x = app.renderer.width / 2 - myRocket.x * container.scale.x;
        container.y = app.renderer.height / 1.2 - myRocket.y * container.scale.y;

        containerIndicator.scale.set(scale);
        containerIndicator.scale.x *= -1;
        containerIndicator.x = app.renderer.width / 2 - myRocket.x * containerIndicator.scale.x;
        containerIndicator.y = app.renderer.height / 1.2 - myRocket.y * containerIndicator.scale.y;
    }

    const now = performance.now();
    lastTimeStamp = now;

    const progress = Math.min((now - startTime) / duration, 1);

    Object.values(rockets).forEach(rocketPositions => {
        if (rocketPositions.length == 0) {
            return;
        }
        const rocket = rocketPositions[0];
        const r = rocketSprites[rocket.playerId];
        r.x = interpolate(r.x, rocket.x, progress)
        r.y = interpolate(r.y, rocket.y, progress)
        r.rotation = interpolate(r.rotation, rocket.angle, progress)

        if (moonIndicator != null) {
            moonIndicator.visible = Math.sqrt((moonX - r.x)**2 + (moonY - r.y)**2) > 30;
            
            moonIndicator.x = (r.x) + (moonX - r.x) * 0.07 ;
            moonIndicator.y = (r.y) + (moonY - r.y) * 0.07;
            moonIndicator.rotation = -Math.atan2((moonX - r.x), (moonY - r.y));
        }   

        if (rocketNames[rocket.playerId]) {
            rocketNames[rocket.playerId][0].x = r.x;
            rocketNames[rocket.playerId][0].y = r.y;
            rocketNames[rocket.playerId][0].rotation = r.rotation;

            rocketNames[rocket.playerId][1].text = `${rocket.fuel}%`
            rocketNames[rocket.playerId][1].x = r.x;
            rocketNames[rocket.playerId][1].y = r.y;
            rocketNames[rocket.playerId][1].rotation = r.rotation;
        }

        if (progress == 1) {
            rocketPositions.shift();
            startTime = performance.now();
        }
    })
})

function interpolate(start, end, time) {
    return (1 - time) * start + time * end
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

const rocketNames = {}

export function addRocket(x, y, angle, playerId) {
    if (rocketSprites[playerId] != null) {
        return;
    }
    const block = new PIXI.Graphics()
        .rect(0, 0, 1, 3)
        .fill("green");
    container.addChild(block);
    rocketSprites[playerId] = block;
    if (players[playerId]) {
        rocketNames[playerId] = [new PIXI.BitmapText(players[playerId].playerName, {
            fontName: 'Arial',
            fontSize: 1.5,
            fill: 'yellow'
        }),
        new PIXI.BitmapText("", {
            fontName: 'Arial',
            fontSize: 1.5,
            fill: 'yellow'
        })]
        container.addChild(rocketNames[playerId][0])
        container.addChild(rocketNames[playerId][1])

        rocketNames[playerId][0].scale.y *= -1;
        rocketNames[playerId][0].x = x;
        rocketNames[playerId][0].y = y;
        rocketNames[playerId][0].pivot.x = -1;
        rocketNames[playerId][0].pivot.y = +0.5;

        rocketNames[playerId][1].scale.y *= -1;
        rocketNames[playerId][1].x = x;
        rocketNames[playerId][1].y = y;
        rocketNames[playerId][1].pivot.x = +1.3;
        rocketNames[playerId][1].pivot.y = +4;
    }
    block.pivot.x = 0.5;
    block.pivot.y = 1.5;
    block.x = x;
    block.y = y;
    block.rotate = 0;
}

let moonIndicator = null;
let moonX = 0;
let moonY = 0;
let moon = null;
export function addMoon(x, y, radius) {
    moonIndicator = new PIXI.Graphics();//.circle(0, 0, .5).fill('white');
    
    moonIndicator.beginFill(0xff0000); // Wypełnienie w kolorze czerwonym
    moonIndicator.lineStyle(0.2, "white"); // Linie w kolorze białym
    moonIndicator.moveTo(0, 0.001); // Pierwszy wierzchołek
    moonIndicator.lineTo(-0.001, 0); // Drugi wierzchołek
    moonIndicator.lineTo(0.001, 0); // Trzeci wierzchołek
    moonIndicator.closePath(); // Zamknięcie kształtu
    moonIndicator.endFill();

    moonX = x;
    moonY = y;

    moon = graphics
        .circle(x, y, radius)
        .fill('white');
    moon.uid="debil"
    containerIndicator.addChild(moonIndicator)
    container.addChild(moon);
}