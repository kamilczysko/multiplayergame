import * as PIXI from "pixi.js";
import { Rocket } from "../infrastructure/rocket";
import { Level } from "../infrastructure/level";
import { rockets, sendSteeringAction } from "../connection/controller";

let accelerateRocket: boolean = false;
let interval: any;

const container = new PIXI.Container();
const containerIndicator = new PIXI.Container();
const containerFire = new PIXI.Container();

// (async () => {
const app = new PIXI.Application({
  width: 1200,
  height: 760,
  background: "#1099bb",
  forceCanvas: false
});

app.stage.interactive = true;
app.stage.eventMode = "static";
app.stage.hitArea = app.screen;

document.getElementById("gameplay")!.appendChild(app.view);

container.x = app.screen.width / 2;
container.y = app.screen.height;
container.scale.x *= -1;

containerIndicator.x = app.screen.width / 2;
containerIndicator.y = app.screen.height;

containerFire.x = app.screen.width / 2;
containerFire.y = app.screen.height;

app.stage.addChild(containerFire);
app.stage.addChild(container);
app.stage.addChild(containerIndicator);

let scale = -10;
container.scale.set(scale)
containerIndicator.scale.set(scale)
containerFire.scale.set(scale)

let moonX = 0;
let moonY = 0;
let moonIndicator: PIXI.Graphics | null | undefined = null;

let elapsedTime = 0;
let animationDuration = 1;

app.ticker.add((delta) => {
  elapsedTime += app.ticker.elapsedMS;
  const progress = Math.min(elapsedTime / animationDuration, 1);
  const myRocket = rockets[getCookieValue("playerId")];
  if (myRocket) {
    myRocket.accelerating = accelerateRocket;
    myRocket.animate(1);

    scale = lerp(scale, -35, 0.1);
    container.scale.set(scale);
    container.scale.x *= -1;
    container.x = app.renderer.width / 2 - myRocket.getRocketAcutalPosition().x * container.scale.x;
    container.y = app.renderer.height / 1.4 - myRocket.getRocketAcutalPosition().y * container.scale.y;

    containerIndicator.scale.set(scale);
    containerIndicator.scale.x *= -1;
    containerIndicator.x = app.renderer.width / 2 - myRocket.getRocketAcutalPosition().x * containerIndicator.scale.x;
    containerIndicator.y = app.renderer.height / 1.4 - myRocket.getRocketAcutalPosition().y * containerIndicator.scale.y;

    containerFire.scale.set(scale);
    containerFire.scale.x *= -1;
    containerFire.x = app.renderer.width / 2 - myRocket.getRocketAcutalPosition().x * containerIndicator.scale.x;
    containerFire.y = app.renderer.height / 1.4 - myRocket.getRocketAcutalPosition().y * containerIndicator.scale.y;

    if (moonIndicator == null) {
      moonIndicator = new PIXI.Graphics();
      moonIndicator.beginFill(0xffffff);
      moonIndicator.lineStyle(0.2, 0x000000);
      moonIndicator.moveTo(0, 0.001);
      moonIndicator.lineTo(-0.001, 0);
      moonIndicator.lineTo(0.001, 0);
      moonIndicator.closePath();
      moonIndicator.endFill();

      containerIndicator.addChild(moonIndicator);
    }

    if (moonIndicator != null) {
      moonIndicator.visible = Math.sqrt((moonX - myRocket.getRocketAcutalPosition().x) ** 2 + (moonY - myRocket.getRocketAcutalPosition().y) ** 2) > 30;
      moonIndicator.x = myRocket.getRocketAcutalPosition().x + (moonX - myRocket.getRocketAcutalPosition().x) * 0.028;
      moonIndicator.y = myRocket.getRocketAcutalPosition().y + (moonY - myRocket.getRocketAcutalPosition().y) * 0.028;
      moonIndicator.rotation = -Math.atan2(moonX - myRocket.getRocketAcutalPosition().x, moonY - myRocket.getRocketAcutalPosition().y);
    }
  }

  if (progress == 1) {
    elapsedTime = 0;
  }

});

let angle = 0;

app.stage.on("pointerdown", () => {
  accelerateRocket = true;
  interval = setInterval(() => sendSteeringAction(angle, accelerateRocket), 100);
});

app.stage.on("pointerup", () => {
  accelerateRocket = false;
  clearInterval(interval);
  sendSteeringAction(angle, false);
});

app.stage.on("pointerupoutside", () => {
  accelerateRocket = false;
  clearInterval(interval);
  sendSteeringAction(angle, false);
});

app.stage.on("pointermove", (event) => {
  if (accelerateRocket === false) {
    return;
  }
  const rocket = rockets[getCookieValue("playerId")];
  if (rocket == null) {
    return;
  }
  const mousePosition = container.toLocal(event.data.global);
  let deltaX = mousePosition.x - rocket.getRocketAcutalPosition().x;
  let deltaY = mousePosition.y - rocket.getRocketAcutalPosition().y;
  angle = (Math.atan2(deltaX, deltaY) / Math.PI) * 100;
});
// })();

const light = new PIXI.Graphics();
light.beginFill(0xffff00, 1); // Żółte światło
light.drawCircle(0, 0, 150);  // Promień światła 150 pikseli
light.endFill();
light.blendMode = PIXI.BLEND_MODES.ADD; // Dodaje kolor, rozjaśniając
light.position.set(0, 0);
app.stage.addChild(light);

containerFire.addChild(light)


export function drawLevel(level: Level) {
  moonX = level.moonX;
  moonY = level.moonY;
  level.drawLevel(containerIndicator);
}

function lerp(start: number, end: number, amt: number) {
  return (1 - amt) * start + amt * end;
}

function getCookieValue(key: string): string {
  return document.cookie
    .split(";")
    .filter((chunk) => chunk.includes(key))
    .map((sessionChunk) => sessionChunk.split("=")[1])[0];
}
export function addRocketToView(rocket: Rocket) {
  rocket.addToContainer(container, containerFire);
}
