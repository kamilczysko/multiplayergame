import { rockets } from "./connector.js";

const app = new PIXI.Application();
await app.init({ antialias: true, resizeTo: window })
document.getElementById("gameplay").appendChild(app.canvas)

const graphics = new PIXI.Graphics();

const container = new PIXI.Container();

app.stage.addChild(container);

let elapsed = 0.0;
app.ticker.add((ticker) => {
    elapsed += ticker.deltaTime;
    console.log(rockets)
    rockets.forEach( rocket => {
        console.log(rocket)
    })
})

container.x = app.screen.width / 2;
container.y = app.screen.height;

container.scale.y = -1;
// container.scale.set(-95);
// container.filters = [new PIXI.BlurFilter(.5)];


export function addBlock(x, y, width, heigth) {
    const block = graphics
        .rect(x, y, width, heigth)
        .fill('white');
    container.addChild(block);
}


export function addMoon(x, y, radius) {
    const moon = graphics
        .circle(x, y, radius)
        .fill('white');
    container.addChild(moon);
}

export function addRocket() {

}