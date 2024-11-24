import { Container, Graphics } from "pixi.js"

export class Level {
    private graphic = new Graphics();
    private block = this.graphic;
    moon = this.graphic;

    setMoon(x: number, y: number, radius: number) {
        this.moon.beginFill(0xffffff);
        this.moon.drawCircle(x, y, radius);
        this.moon.endFill();
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
        container.addChild(this.block!);
    }

}