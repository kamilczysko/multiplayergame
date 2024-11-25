import { Sprite, Texture } from "pixi.js";
let idx = 0;
const rockets = [
    Texture.from("rockets/rocket1.png"),
    Texture.from("rockets/rocket2.png"),
    Texture.from("rockets/rocket3.png"),
    Texture.from("rockets/rocket4.png"),
    Texture.from("rockets/rocket5.png")]

export function getRocketSprite() {
    if (idx >= rockets.length - 1) {
        idx = 0;
    } else {
        idx++;
    }
    return new Sprite(rockets[idx])
}