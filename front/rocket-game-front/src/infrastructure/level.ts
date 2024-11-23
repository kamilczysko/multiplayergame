import { Container, Graphics } from "pixi.js"

export class Level {
    private moon: Graphics | undefined;
    private blocks: Graphics[] = [];

    setMoon(x: number, y: number, radius: number) {
        this.moon = new Graphics();
        this.moon.beginFill(0xffffff);
        this.moon.drawCircle(x, y, radius);
        this.moon.endFill();
    }

    addBlock(x: number, y: number, w: number, h: number) {
        const block = new Graphics();
        block.beginFill(0xffffff);
        block.drawRect(x, y, w, h);
        block.endFill();
        this.blocks.push(block);
    }

    drawLevel(container: Container) {
        console.log("draw level...")
        container.addChild(this.moon!);
        this.blocks.forEach(block => {
            container.addChild(block)
        })
    }

}