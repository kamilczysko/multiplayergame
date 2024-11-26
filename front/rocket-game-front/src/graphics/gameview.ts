import * as PIXI from "pixi.js";
import { Rocket } from "../infrastructure/rocket";
import { Level } from "../infrastructure/level";
import { rockets, sendSteeringAction } from "../connection/controller";

let accelerateRocket: boolean = false;
let interval: any;

const container = new PIXI.Container();
const containerFrontal = new PIXI.Container();
const containerFire = new PIXI.Container();
const backgroundContainer = new PIXI.Container();

const app = new PIXI.Application({
  resizeTo: window,
  background: "#211753",
  forceCanvas: false,
  antialias: true
});

app.stage.eventMode = "static";
app.stage.hitArea = app.screen;

document.getElementById("gameplay")!.appendChild(app.view);

let scale = -10;

container.x = app.screen.width / 2;
container.y = app.screen.height;
container.scale.x *= -1;
container.scale.set(scale)

containerFrontal.x = app.screen.width / 2;
containerFrontal.y = app.screen.height;

const background = PIXI.Texture.from('sky.jpg');
const backogroundSprite = new PIXI.Sprite(background);
backgroundContainer.scale.set(0.8)
backgroundContainer.addChild(backogroundSprite);

const cloud = PIXI.Texture.from('clouds1.png');
const cloudSprite = new PIXI.Sprite(cloud);
cloudSprite.scale.set(0.2);
cloudSprite.x = -150;
cloudSprite.y = 60;

const cloud2 = PIXI.Texture.from('clouds2.png');
const cloudSprite2 = new PIXI.Sprite(cloud2);
cloudSprite2.scale.set(-0.2);
cloudSprite2.x = 200;
cloudSprite2.y = 220;

const cloud3 = PIXI.Texture.from('clouds3.png');
const cloudSprite3 = new PIXI.Sprite(cloud3);
cloudSprite3.scale.set(-0.1);
cloudSprite3.x = 30;
cloudSprite3.y = 240;

const cloud4 = PIXI.Texture.from('clouds5.png');
const cloudSprite4 = new PIXI.Sprite(cloud4);
cloudSprite4.scale.set(-0.1);
cloudSprite4.x = 100;
cloudSprite4.y = 300;

containerFrontal.addChild(cloudSprite, cloudSprite2, cloudSprite3, cloudSprite4);

containerFire.x = app.screen.width / 2;
containerFire.y = app.screen.height;

app.stage.addChild(backgroundContainer, containerFire, container, containerFrontal);


containerFrontal.scale.set(scale)
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

    scale = lerp(scale, -38, 0.1);
    container.scale.set(scale);
    container.scale.x *= -1;
    container.x = app.renderer.width / 2 - myRocket.getRocketAcutalPosition().x * container.scale.x;
    container.y = app.renderer.height / 1.4 - myRocket.getRocketAcutalPosition().y * container.scale.y;

    containerFrontal.scale.set(scale);
    containerFrontal.scale.x *= -1;
    containerFrontal.x = app.renderer.width / 2 - myRocket.getRocketAcutalPosition().x * containerFrontal.scale.x;
    containerFrontal.y = app.renderer.height / 1.4 - myRocket.getRocketAcutalPosition().y * containerFrontal.scale.y;

    containerFire.scale.set(scale);
    containerFire.scale.x *= -1;
    containerFire.x = app.renderer.width / 2 - myRocket.getRocketAcutalPosition().x * containerFrontal.scale.x;
    containerFire.y = app.renderer.height / 1.4 - myRocket.getRocketAcutalPosition().y * containerFrontal.scale.y;

    backgroundContainer.x = myRocket.getRocketAcutalPosition().x - 100;
    backgroundContainer.y = (myRocket.getRocketAcutalPosition().y - 500) * 1.1;

    if (moonIndicator == null) {
      moonIndicator = new PIXI.Graphics();
      moonIndicator.beginFill(0x000000);
      moonIndicator.lineStyle(0.3, 0x000000);
      moonIndicator.moveTo(0, 0.003);
      moonIndicator.lineTo(-0.003, 0);
      moonIndicator.lineTo(0.003, 0);
      moonIndicator.closePath();
      moonIndicator.endFill();

      containerFrontal.addChild(moonIndicator);
    }

    if (moonIndicator != null) {
      moonIndicator.visible = Math.sqrt((moonX - myRocket.getRocketAcutalPosition().x) ** 2 + (moonY - myRocket.getRocketAcutalPosition().y) ** 2) > 20;
      moonIndicator.x = myRocket.getRocketAcutalPosition().x + (moonX - myRocket.getRocketAcutalPosition().x) * 0.032;
      moonIndicator.y = myRocket.getRocketAcutalPosition().y + (moonY - myRocket.getRocketAcutalPosition().y) * 0.032;
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

export function drawLevel(level: Level) {
  moonX = level.moonX;
  moonY = level.moonY;
  level.drawLevel(container);
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

export function removeRocket(rocketId: string) {
  rockets[rocketId].destroyRocket(container);
  delete rockets[rocketId];
}

export function zoomOut() {
  container.scale.set(-10)
  container.x = app.screen.width / 2;
  container.y = app.screen.height;

  containerFire.scale.set(-10)
  containerFire.x = app.screen.width / 2;
  containerFire.y = app.screen.height;

  containerFrontal.scale.set(-10)
  containerFrontal.x = app.screen.width / 2;
  containerFrontal.y = app.screen.height;
}