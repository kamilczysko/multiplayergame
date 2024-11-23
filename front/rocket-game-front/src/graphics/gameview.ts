import * as PIXI from "pixi.js";
import { Rocket } from "../infrastructure/rocket";
import { Level } from "../infrastructure/level";
import { rockets } from "../connection/controller";

let accelerateRocket: boolean = false;
let interval: any;

// (async () => {
const app = new PIXI.Application({
  width: 1200,
  height: 760,
  background: "#1099bb",
});

app.stage.interactive = true;
app.stage.eventMode = "static";
app.stage.hitArea = app.screen;

document.getElementById("gameplay")!.appendChild(app.view);

const container = new PIXI.Container();
const containerIndicator = new PIXI.Container();

container.x = app.screen.width / 2;
container.y = app.screen.height / 2;
container.scale.x *= -1;

containerIndicator.x = app.screen.width / 2;
containerIndicator.y = app.screen.height;

app.stage.addChild(container);
app.stage.addChild(containerIndicator);

let scale = -10;
let moonIndicator: PIXI.Graphics | null = null;
let moon: PIXI.Graphics | null = null;

app.ticker.add((delta) => {
  console.log(getCookieValue("playerId"))
  const myRocket = rockets[getCookieValue("playerId")];
  if (myRocket) {
    scale = lerp(scale, -35, 0.1);
    container.scale.set(scale);
    container.scale.x *= -1;
    container.x = app.renderer.width / 2 - myRocket.getRocketAcutalPosition().x * container.scale.x;
    container.y = app.renderer.height / 1.2 - myRocket.getRocketAcutalPosition().y * container.scale.y;

    containerIndicator.scale.set(scale);
    containerIndicator.scale.x *= -1;
    containerIndicator.x = app.renderer.width / 2 - myRocket.getRocketAcutalPosition().x * containerIndicator.scale.x;
    containerIndicator.y = app.renderer.height / 1.2 - myRocket.getRocketAcutalPosition().y * containerIndicator.scale.y;

    if (moonIndicator != null) {
      moonIndicator.visible = Math.sqrt((moon!.x - myRocket.getRocketAcutalPosition().x) ** 2 + (moon!.y - myRocket.getRocketAcutalPosition().y) ** 2) > 30;
      moonIndicator.x = myRocket.getRocketAcutalPosition().x + (moon!.x - myRocket.getRocketAcutalPosition().x) * 0.07;
      moonIndicator.y = myRocket.getRocketAcutalPosition().y + (moon!.y - myRocket.getRocketAcutalPosition().y) * 0.07;
      moonIndicator.rotation = -Math.atan2(moon!.x - myRocket.getRocketAcutalPosition().x, moon!.y - myRocket.getRocketAcutalPosition().y);
    }
  }


});

app.stage.on("pointerdown", (event) => {
  accelerateRocket = true;
  interval = setInterval(
    () =>
      //sendSteeringAction(angle, accelerateRocket),
      100
  );
});
app.stage.on("pointerup", (event) => {
  accelerateRocket = false;
  clearInterval(interval);
  //sendSteeringAction(angle, false);
});
app.stage.on("pointerupoutside", (event) => {
  accelerateRocket = false;
  clearInterval(interval);
  //sendSteeringAction(angle, false);
});

app.stage.on("pointermove", (event) => {
  // if (accelerateRocket === false) {
  //   return;
  // }
  // const rocket = rocketSprites[getPlayerId()];
  // if (rocket == null) {
  //   return;
  // }
  // const mousePosition = container.toLocal(event.data.global);
  // let deltaX = mousePosition.x - rocket.x;
  // let deltaY = mousePosition.y - rocket.y;
  // angle = parseInt((Math.atan2(deltaX, deltaY) / Math.PI) * 100);
});
// })();

export function drawLevel(level: Level) {
  level.drawLevel(container);
}

function drawMoonIndicator(
  x: number,
  y: number,
  radius: number
): PIXI.Graphics {
  moonIndicator = new PIXI.Graphics();

  moonIndicator.beginFill(0xffffff);
  moonIndicator.lineStyle(0.2, 0xffffff);
  moonIndicator.moveTo(0, 0.001);
  moonIndicator.lineTo(-0.001, 0);
  moonIndicator.lineTo(0.001, 0);
  moonIndicator.closePath();
  moonIndicator.endFill();

  return moonIndicator;
}

function drawMoon(x: number, y: number, radius: number): PIXI.Graphics {
  moon = new PIXI.Graphics();
  moon.beginFill(0xffffff);
  moon.drawCircle(x, y, radius);
  moon.endFill();
  return moon;
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
  rocket.addToContainer(container);
}