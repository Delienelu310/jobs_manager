import { GraphElementEventHandler } from "./eventHandlers/GraphElementEventHandler";
import { GOF } from "./GOF";
import { GraphElement } from "./GraphElement";


export class NullGraphElement implements GraphElement{
    public getMenuComponent(): JSX.Element {
        throw new Error("Null graph element cannot return menu.");
    }


    public getEventHandler(): GraphElementEventHandler {
        throw new Error("Null element cant handle any events");
    }

    public getGof(): GOF {
        throw new Error("Null graph element cant have GOF");
    }
    public getGofId(): string {
        return "null_element";
    }
    public isNull(): boolean {
        return true;
    }
    public doesContainPoint(x: number, y: number): boolean {
        return false;
    }
    public draw(ctx : CanvasRenderingContext2D): void {
        return;
    }
    public getChildren(): GraphElement[] {
        return [];
    }
    public getParent(): GraphElement {
        throw new Error("Null element cannot have parent");
    }

}