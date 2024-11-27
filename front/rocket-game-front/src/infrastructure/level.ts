import { PointLight } from "@pixi/lights";
import { Container, Graphics, Sprite, Texture } from "pixi.js"

export class Level {
    private graphic = new Graphics();
    private block = this.graphic;
    private: PointLight | null = null;
    private image: Sprite | null = null;
    private moon = this.graphic;
    moonX = 0;
    moonY = 0;

    setMoon(x: number, y: number, radius: number) {
        const texture = Texture.from('moon.png');
        this.image = new Sprite(texture);
        this.image.width = radius * 2 + 4;
        this.image.height = radius * 2 + 4;
        this.image.x = x - radius - 2;
        this.image.y = y - radius - 2;

        this.moon.beginFill(0xffffff, 0.73);
        this.moon.drawCircle(x, y, radius);
        this.moon.endFill();


        this.moonX = x;
        this.moonY = y;

    }

    addBlock(x: number, y: number, w: number, h: number) {
        const block = this.graphic;
        block.beginFill(0xffffff);
        block.drawRect(x, y, w, h);
        block.endFill();
    }

    drawLevel(container: Container) {
        console.log("draw level...")
        container.addChild(this.moon!);
        container.addChild(this.image!);
        container.addChild(this.block!);
    }

}