import { Emitter, upgradeConfig } from "@pixi/particle-emitter";
import { Container, Graphics, Text, Texture, lights } from "pixi.js";
import { Layer, Stage } from '@pixi/layers';
import { diffuseGroup, normalGroup, lightGroup, PointLight } from '@pixi/lights';

export class Rocket {
  private rocketStatus: { x: number; y: number; angle: number, fuel: number }[] = [];
  private name: string;
  rocketId: string;

  private rocketSprite: Graphics | undefined;
  private fuelLabel: Text | undefined;
  private nameLabel: Text | undefined;
  private fire: Emitter | undefined;

  private fireLight = new PointLight(0xffffff, 5);

  accelerating: boolean = false;

  constructor(x: number, y: number, angle: number, fuel: number, name: string, rocketId: string,) {
    this.rocketStatus = [{ x: x, y: y, angle: angle, fuel: fuel }];
    this.rocketId = rocketId;
    this.name = name;

    this.initRocketSprite();
    this.initFuelLabel(fuel);
    this.initNameLabel(name);
  }

  private initRocketSprite() {
    this.rocketSprite = new Graphics();
    this.rocketSprite.beginFill(0x00ff00);
    this.rocketSprite.drawRect(this.rocketStatus[0].x, this.rocketStatus[0].y, 1, 3);
    this.rocketSprite.endFill();

    this.rocketSprite.pivot.x = this.rocketStatus[0].x + 0.5;
    this.rocketSprite.pivot.y = this.rocketStatus[0].y + 1.5;
    this.rocketSprite.x = this.rocketStatus[0].x;
    this.rocketSprite.y = this.rocketStatus[0].y;
  }

  private initFuelLabel(fuel: number) {
    this.fuelLabel = new Text(`${fuel}%`, {
      fontFamily: 'Roboto',
      fontSize: 16
    });

    this.fuelLabel.x = this.rocketStatus[0].x;
    this.fuelLabel.y = this.rocketStatus[0].y;
    this.fuelLabel.anchor.y = 3.9;
    this.fuelLabel.anchor.x = 0.5;
    this.fuelLabel.scale.set(-0.03);
    this.fuelLabel.scale.x *= -1;
  }

  private initNameLabel(name: string) {
    this.nameLabel = new Text(`${name}`, {
      fontFamily: 'Roboto',
      fontSize: 16
    });

    this.nameLabel.x = this.rocketStatus[0].x;
    this.nameLabel.y = this.rocketStatus[0].y;
    this.nameLabel.anchor.x = -0.7;
    this.nameLabel.scale.set(-0.03);
    this.nameLabel.scale.x *= -1;
  }

  private initFire(container: Container) {
    this.fire = new Emitter(
      container,
      upgradeConfig(
        {
          "alpha": {
            "start": 0.45,
            "end": 1
          },
          "scale": {
            "start": 0.0051,
            "end": 0.0003,
            "minimumScaleMultiplier": 10
          },
          "color": {
            "start": "#ffd900",
            "end": "#ff0000"
          },
          "speed": {
            "start": 5,
            "end": 50,
            "minimumSpeedMultiplier": 0.5
          },
          "acceleration": {
            "x": 9,
            "y": 0
          },
          "maxSpeed": 0,
          "startRotation": {
            "min": 0,
            "max": 360
          },
          "noRotation": false,
          "rotationSpeed": {
            "min": 100,
            "max": 7
          },
          "lifetime": {
            "min": 0.3,
            "max": 0.6
          },
          "blendMode": "screen",
          "frequency": 0.001,
          "emitterLifetime": -1,
          "maxParticles": 210,
          "pos": {
            "x": 0,
            "y": 0
          },
          "addAtBack": false,
          "spawnType": "point"
        },
        [Texture.from("particle.jpg")]
      )
    );
  }

  addRocketState(x: number, y: number, angle: number, fuel: number) {
    this.rocketStatus.push({ x: x, y: y, angle: angle, fuel: fuel });
  }
  setName(name: string) {
    this.name = name;
  }

  animate(delta: number) {
    this.fire?.update(this.accelerating && this.rocketStatus[0]?.fuel > 0 ? 0.03 : 0.005);
    this.fire?.updateSpawnPos(this.rocketSprite!.x, this.rocketSprite!.y - 0.5);

    this.fireLight.x = this.rocketSprite!.x;
    this.fireLight.y = this.rocketSprite!.y;

    if (!this.rocketSprite || this.rocketStatus.length == 0) {
      return;
    }

    let recentStatus = this.rocketStatus.shift();

    if (this.rocketStatus.length >= 10) {
      recentStatus = this.rocketStatus.shift(); //jump
    }
    if (this.rocketStatus.length >= 30) {
      recentStatus = this.rocketStatus.shift(); //jump
    }
    if (this.rocketStatus.length >= 50) {
      recentStatus = this.rocketStatus.shift(); //jump
    }
    this.rocketSprite!.x = this.interpolate(this.rocketSprite!.x, recentStatus!.x, delta);
    this.rocketSprite!.y = this.interpolate(this.rocketSprite!.y, recentStatus!.y, delta);
    this.rocketSprite!.rotation = this.interpolate(this.rocketSprite!.rotation, recentStatus!.angle, delta);

    this.nameLabel!.x = this.rocketSprite!.x;
    this.nameLabel!.y = this.rocketSprite!.y;
    this.nameLabel!.rotation = this.rocketSprite!.rotation;

    this.fuelLabel!.x = this.rocketSprite!.x;
    this.fuelLabel!.y = this.rocketSprite!.y;
    this.fuelLabel!.rotation = this.rocketSprite!.rotation;
    this.fuelLabel!.text = `${recentStatus!.fuel}%`;
  }

  private interpolate(start: number, end: number, time: number) {
    return (1 - time) * start + time * end;
  }

  getRocketAcutalPosition() {
    return {
      x: this.rocketSprite!.x,
      y: this.rocketSprite!.y,
      angle: this.rocketSprite!.rotation,
    };
  }

  addToContainer(rocketContainer: Container, otherContainer: Container) {
    rocketContainer.addChild(this.fuelLabel!);
    rocketContainer.addChild(this.nameLabel!);
    rocketContainer.addChild(this.rocketSprite!)
    this.initFire(otherContainer);
    otherContainer.addChild(this.fireLight);
  }
}