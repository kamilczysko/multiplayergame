import { Emitter, upgradeConfig } from "@pixi/particle-emitter";
import { Container, Sprite, Text, Texture } from "pixi.js";
import { getRocketSprite } from "../graphics/rockets-pool";

export class Rocket {
  private rocketStatus: { x: number; y: number; angle: number, fuel: number, points: number, timestamp: number }[] = [];
  rocketId: string;

  private rocketSprite: Sprite | undefined;
  private fuelLabel: Text | undefined;
  private pointsLabel: Text | undefined;
  private fire: Emitter | undefined;

  accelerating: boolean = false;

  constructor(x: number, y: number, angle: number, fuel: number, rocketId: string, points: number) {
    this.rocketStatus = [{ x: x, y: y, angle: angle, fuel: fuel, points: points, timestamp: 0 }];
    this.rocketId = rocketId;

    this.initRocketSprite();
    this.initFuelLabel(fuel);
    this.initNameLabel(points.toString());
  }

  private initRocketSprite() {
    this.rocketSprite = getRocketSprite();
    this.rocketSprite.width = 1;
    this.rocketSprite.height = 3;
    this.rocketSprite.scale.y *= -1;
    this.rocketSprite.x = this.rocketStatus[0].x;
    this.rocketSprite.y = this.rocketStatus[0].y;
    this.rocketSprite.anchor.x = 0.5;
    this.rocketSprite.anchor.y = .5;
  }

  private initFuelLabel(fuel: number) {
    this.fuelLabel = new Text(`${fuel}%`, {
      fontFamily: 'Roboto',
      fontSize: 16
    });

    this.fuelLabel.x = this.rocketStatus[0].x;
    this.fuelLabel.y = this.rocketStatus[0].y;
    this.fuelLabel.anchor.y = 5.5;
    this.fuelLabel.anchor.x = 0.5;
    this.fuelLabel.scale.set(-0.02);
    this.fuelLabel.scale.x *= -1;
  }

  private initNameLabel(name: string) {
    this.pointsLabel = new Text(`${name}`, {
      fontFamily: 'Roboto',
      fontSize: 16
    });

    this.pointsLabel.x = this.rocketStatus[0].x;
    this.pointsLabel.y = this.rocketStatus[0].y;
    this.pointsLabel.anchor.x = -2;
    this.pointsLabel.scale.set(-0.03);
    this.pointsLabel.scale.x *= -1;
  }

  private initFire(container: Container) {
    this.fire = new Emitter(
      container,
      upgradeConfig(
        {
          "alpha": {
            "start": 0.7,
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
            "min": 180,
            "max": 270
          },
          "noRotation": false,
          "rotationSpeed": {
            "min": 100,
            "max": 7
          },
          "lifetime": {
            "min": 0.4,
            "max": 0.85
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

  addRocketState(x: number, y: number, angle: number, fuel: number, points: number, timestamp: number) {
    this.rocketStatus.push({ x: x, y: y, angle: angle, fuel: fuel, points: points, timestamp: timestamp });
    console.log("add state to rocekt: " + this.rocketStatus.length)
    this.rocketStatus.sort((a, b) => a.timestamp - b.timestamp)
  }

  animate(delta: number) {
    this.fire?.update(this.accelerating && this.rocketStatus[0]?.fuel > 0 ? 0.03 : 0.005);
    this.fire?.updateSpawnPos(this.rocketSprite!.x + 1.2 * Math.sin(this.rocketSprite!.rotation), this.rocketSprite!.y - 1.2 * Math.cos(this.rocketSprite!.rotation));

    if (!this.rocketSprite || this.rocketStatus.length == 0) {
      return;
    }

    let recentStatus = this.rocketStatus[0];

    console.log("length of steps: " + this.rocketStatus.length)
    // if (this.rocketStatus.length >= 10) {
    //   this.rocketStatus.shift()!!; //jump
    // }


    this.rocketSprite!.x = this.lerp(this.rocketSprite!.x, recentStatus!.x, delta);
    this.rocketSprite!.y = this.lerp(this.rocketSprite!.y, recentStatus!.y, delta);
    this.rocketSprite!.rotation = this.lerp(this.rocketSprite!.rotation, recentStatus!.angle, delta);

    this.pointsLabel!.x = this.rocketSprite!.x;
    this.pointsLabel!.y = this.rocketSprite!.y;
    this.pointsLabel!.rotation = this.rocketSprite!.rotation;
    this.pointsLabel!.text = recentStatus?.points + "";

    this.fuelLabel!.x = this.rocketSprite!.x;
    this.fuelLabel!.y = this.rocketSprite!.y;
    this.fuelLabel!.rotation = this.rocketSprite!.rotation;
    this.fuelLabel!.text = `${recentStatus!.fuel}%`;

    if (delta == 1) {
      this.rocketStatus.shift();
    }

  }

  private lerp(start: number, end: number, amt: number) {
    return (1 - amt) * start + amt * end;
  }

  getRocketAcutalPosition() {
    return {
      x: this.rocketSprite!.x,
      y: this.rocketSprite!.y,
      angle: this.rocketSprite!.rotation,
    };
  }

  addToContainer(rocketContainer: Container, otherContainer: Container) {
    rocketContainer.addChild(this.fuelLabel!, this.pointsLabel!, this.rocketSprite!)
    this.initFire(otherContainer);
  }

  destroyRocket(container: Container) {
    container.removeChild(this.fuelLabel!, this.pointsLabel!, this.rocketSprite!)
    this.fuelLabel!.destroy({ children: true, texture: true, baseTexture: true })
    this.pointsLabel!.destroy({ children: true, texture: true, baseTexture: true })
    this.rocketSprite!.destroy({ children: true, texture: true, baseTexture: true })
    this.fire!.emit = false;
    this.fire?.cleanup();
    this.fire?.destroy();
  }
}
