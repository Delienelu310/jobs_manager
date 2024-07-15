import { GOF } from "./GOF";
import { GraphElement } from "./GraphElement";


export class NullGraphElement implements GraphElement{

    getGof(): GOF {
        throw new Error("Null graph element cant have GOF");
    }
    getGofId(): string {
        return "null_element";
    }
    isNull(): boolean {
        return true;
    }
    doesContainPoint(x: number, y: number): boolean {
        return false;
    }
    draw(): void {
        return;
    }
    getChildren(): GraphElement[] {
        return [];
    }
    getParent(): GraphElement {
        throw new Error("Null element cannot have parent");
    }

}